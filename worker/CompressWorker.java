package cu.redcuba.worker;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.rabbitmq.client.Channel;
import cu.redcuba.entity.Visit;
import cu.redcuba.output.CompressOutput;
import cu.redcuba.factory.VariableFactory;
import cu.redcuba.helper.CastHelper;
import cu.redcuba.helper.UrlFetchHelper;
import cu.redcuba.object.Website;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@Profile("consumer")
public class CompressWorker extends AbstractConsumerWorker<CompressOutput> {

    private static final Logger LOG = Logger.getLogger(CompressWorker.class.getName());

    private static final Gson GSON = new Gson();

    private final UrlFetchHelper compressUrlFetchHelper;

    @Autowired
    public CompressWorker(UrlFetchHelper compressUrlFetchHelper) {
        this.compressUrlFetchHelper = compressUrlFetchHelper;
    }

    /**
     * Returns the ID of the variable evaluated by this worker.
     *
     * @return The variable's ID.
     */
    @Override
    long getVariableId() {
        return VariableFactory.VAR_COMPRESSION;
    }

    /**
     * Analyses the values of a particular indicator.
     *
     * @param args Arguments needed by the worker.
     *             In this case is expected:
     *             <ul>
     *             <li>hostname</li>
     *             </ul>
     * @return The analysis result.
     */
    @Override
    CompressOutput analyse(Object... args) {
        String hostname = CastHelper.cast(args[0], String.class);

        CompressOutput output = new CompressOutput();

        // By default the compression is not supported.
        output.setSupport(false);

        // Checking if the page is downloaded to not download it again.
        Visit compressVisit = compressUrlFetchHelper.visit("http://" + hostname, true);
        if (compressVisit != null) {
            output.setSupport(supportsCompression(compressVisit.getContentEncoding()));
        }

        if (output.support()) {
            output.setValue(VALUE_CORRECT);
        } else {
            output.setValue(VALUE_INCORRECT);
        }

        return output;
    }

    /**
     * Analyses the values, inserts the results into the database and sends to Influx a report.
     *
     * @param website The {@link Website} instance with values related to the evaluated website.
     * @param args    Arguments needed by the worker.
     */
    @Override
    void fullWork(Website website, Object... args) {
        CompressOutput output = analyse(website.getHostname());

        save(website, getIndicatorSlugWithPrefix(), output.getValue());
        save(website, getIndicatorSlugWithPrefix("support"), output.support());

        // Reporting the evaluation to the monitor
        sendEvaluation(website, output);
    }

    /**
     * Callback for processing a received Rabbit message.
     * <p>Implementors are supposed to process the given Message,
     * typically sending reply messages through the given Session.
     *
     * @param message the received AMQP message (never <code>null</code>)
     * @param channel the underlying Rabbit Channel (never <code>null</code>)
     * @throws Exception Any.
     */
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        try {
            // Getting the message
            Website website = GSON.fromJson(new String(message.getBody()), Website.class);

            fullWork(website);
        } catch (JsonSyntaxException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
        } finally {
            basicAck(message, channel);
        }
    }

    /**
     * Checks if there is support for gzip, zip or deflate compression.
     *
     * @param contentEncoding Content encoding.
     * @return True if the content supports encoding or not in other case.
     */
    private boolean supportsCompression(String contentEncoding) {
        return contentEncoding != null && (contentEncoding.contains("gzip") || contentEncoding.contains("deflate"));
    }

}
