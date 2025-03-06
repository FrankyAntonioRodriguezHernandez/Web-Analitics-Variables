package cu.redcuba.worker;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.rabbitmq.client.Channel;
import cu.redcuba.entity.Visit;
import cu.redcuba.output.Hiding404Output;
import cu.redcuba.factory.VariableFactory;
import cu.redcuba.helper.CastHelper;
import cu.redcuba.helper.UrlFetchHelper;
import cu.redcuba.object.Website;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@Profile("consumer")
public class Hiding404Worker extends AbstractConsumerWorker<Hiding404Output> {

    private static final Logger LOG = Logger.getLogger(Hiding404Worker.class.getName());

    private static final Gson GSON = new Gson();

    private static final SecureRandom RANDOM = new SecureRandom();

    private final UrlFetchHelper normalUrlFetchHelper;

    @Autowired
    public Hiding404Worker(UrlFetchHelper normalUrlFetchHelper) {
        this.normalUrlFetchHelper = normalUrlFetchHelper;
    }

    /**
     * Returns the ID of the variable evaluated by this worker.
     *
     * @return The variable's ID.
     */
    @Override
    long getVariableId() {
        return VariableFactory.VAR_HIDING_404;
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
    Hiding404Output analyse(Object... args) {
        String hostname = CastHelper.cast(args[0], String.class);

        Hiding404Output output = new Hiding404Output();

        // By default there is not 404 hiding.
        output.setNotHiding(true);

        // Generating random pages
        List<String> randomPages = Arrays.asList(
                getRandom(), getRandom(), getRandom()
        );

        for (String randomPath : randomPages) {
            String randomUrl = UrlFetchHelper.getAbsolute(randomPath, "http://" + hostname);
            Visit randomVisit = normalUrlFetchHelper.visitUnstored(randomUrl);

            // If the Visit object is null then was a successful 404
            if (randomVisit != null && !validCode(randomVisit.getCode())) {
                output.setNotHiding(false);
                break;
            }
        }

        if (output.isNotHiding()) {
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
        Hiding404Output output = analyse(website.getHostname());

        save(website, getIndicatorSlugWithPrefix(), output.getValue());
        save(website, getIndicatorSlugWithPrefix("not-hiding"), output.isNotHiding());

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

    // https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
    private boolean validCode(short code) {
        return code == 404 || code == 410;
    }

    private String getRandom() {
        return new BigInteger(130, RANDOM).toString(32);
    }

}
