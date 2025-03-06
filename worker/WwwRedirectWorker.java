package cu.redcuba.worker;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.rabbitmq.client.Channel;
import cu.redcuba.entity.Visit;
import cu.redcuba.factory.VariableFactory;
import cu.redcuba.helper.CastHelper;
import cu.redcuba.helper.UrlFetchHelper;
import cu.redcuba.object.Website;
import cu.redcuba.output.WwwRedirectOutput;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@Profile("consumer")
@RestController
public class WwwRedirectWorker extends AbstractConsumerWorker<WwwRedirectOutput> {

    private static final Logger LOG = Logger.getLogger(WwwRedirectWorker.class.getName());

    private static final Gson GSON = new Gson();

    private static final String WWW_PATTERN = "^www\\.(.+)$";

    private static final String WWW_PREFIX = "www.";

    private final UrlFetchHelper normalUrlFetchHelper;

    @Autowired
    public WwwRedirectWorker(UrlFetchHelper normalUrlFetchHelper) {
        this.normalUrlFetchHelper = normalUrlFetchHelper;
    }

    /**
     * Returns the ID of the variable evaluated by this worker.
     *
     * @return The variable's ID.
     */
    @Override
    long getVariableId() {
        return VariableFactory.VAR_WWW;
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
    WwwRedirectOutput analyse(Object... args) {
        String hostname = CastHelper.cast(args[0], String.class);

        WwwRedirectOutput output = new WwwRedirectOutput();

        if (!appliesForEvaluation(hostname)) {
            output.setHasRedirection(false);
            output.setValue(VALUE_NA);
            return output;
        }

        // By default, we assume that it doesn't have redirection.
        output.setHasRedirection(false);

        // Getting the complementary www redirection.
        String complement = hostname.matches(WWW_PATTERN) ? hostname.replace(WWW_PREFIX, "") : WWW_PREFIX + hostname;

        Visit complementVisit = normalUrlFetchHelper.visit("http://" + complement, false);

        if (complementVisit != null) {
            output.setHasRedirection(existsByStatusCode(complementVisit.getCode()));
        }

        // Set general evaluation.
        if (output.hasRedirection()) {
            Visit hostnameVisit = this.normalUrlFetchHelper.visit("http://" + hostname, false);
            output.setValue(VALUE_TO_IMPROVE);

            assert complementVisit != null;
            assert hostnameVisit != null;

            // Cuando ambas solicitudes terminan en la misma URL.
            if (hostnameVisit.getLastUrl().equals(complementVisit.getLastUrl())) {
                output.setValue(VALUE_CORRECT);
            }
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
        WwwRedirectOutput output = analyse(website.getHostname());

        save(website, getIndicatorSlugWithPrefix(), output.getValue());
        save(website, getIndicatorSlugWithPrefix("has-redirection"), output.hasRedirection());

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
            Website website = GSON.fromJson(new String(message.getBody()), Website.class);
            fullWork(website);
        } catch (JsonSyntaxException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
        } finally {
            basicAck(message, channel);
        }
    }

    /**
     * Evalúa si existe una página de acuerdo al código de estado.
     *
     * @param statusCode status code from visit
     * @return returns true if code is between 200 and 399
     */
    private boolean existsByStatusCode(int statusCode) {
        return statusCode >= 200 && statusCode < 400;
    }

    /**
     * Evaluate if the hostname applies for having a redirection to www
     *
     * @param args hostname
     * @return {@link Boolean}
     */
    @Override
    boolean appliesForEvaluation(Object... args) {
        return true;
    }
}