package cu.redcuba.worker;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.rabbitmq.client.Channel;
import cu.redcuba.client.availability.AvailabilityClient;
import cu.redcuba.factory.VariableFactory;
import cu.redcuba.helper.CastHelper;
import cu.redcuba.object.Website;
import cu.redcuba.output.ResponseTimeOutput;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@Profile("consumer")
public class ResponseTimeWorker extends AbstractConsumerWorker<ResponseTimeOutput> {

    private static final Logger LOG = Logger.getLogger(ResponseTimeWorker.class.getName());

    private static final Gson GSON = new Gson();

    private final AvailabilityClient availabilityClient;

    @Autowired
    public ResponseTimeWorker(AvailabilityClient availabilityClient) {
        this.availabilityClient = availabilityClient;
    }

    /**
     * Returns the ID of the variable evaluated by this worker.
     *
     * @return The variable's ID.
     */
    @Override
    long getVariableId() {
        return VariableFactory.VAR_RESPONSE_TIME;
    }

    /**
     * Analyses the values of a particular indicator.
     *
     * @param args Arguments needed by the worker.
     *             In this case is expected:
     *             <ul>
     *             <li>id</li>
     *             </ul>
     * @return The analysis result.
     */
    @Override
    ResponseTimeOutput analyse(Object... args) {
        Long id = CastHelper.cast(args[0], Long.class);

        Float responseTime = availabilityClient.getResponseTime(id).getValue();

        if (responseTime == null) {
            return null;
        }

        ResponseTimeOutput output = new ResponseTimeOutput();
        output.setValue(responseTime);

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
        ResponseTimeOutput output = analyse(website.getId());

        // If there is not a response time available, we send a failed evaluation
        if (output == null) {
            sendFailedEvaluation(website, null);
            return;
        }

        save(website, getIndicatorSlugWithPrefix(), output.getValue());

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

    @Override
    public void sendEvaluation(Website website, float evaluation, Long time) {
        evaluationsReporter.websitesEvaluationDay(
                website.getHostname(),
                getVariable(),
                getEvaluation(evaluation),
                Float.toString(evaluation),
                time);
    }

    private float getEvaluation(float evaluation) {
        if (evaluation > 3) {
            return 0f;
        } else if (evaluation == 3) {
            return 0.5f;
        } else {
            return 1f;
        }
    }

}
