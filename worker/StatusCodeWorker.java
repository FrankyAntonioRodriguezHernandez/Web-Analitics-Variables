package cu.redcuba.worker;

import cu.redcuba.output.StatusCodeOutput;
import cu.redcuba.factory.VariableFactory;
import cu.redcuba.helper.CastHelper;
import cu.redcuba.object.Website;
import org.springframework.stereotype.Component;

@Component
public class StatusCodeWorker extends AbstractWorker<StatusCodeOutput> {

    /**
     * Returns the ID of the variable evaluated by this worker.
     *
     * @return The variable's ID.
     */
    @Override
    long getVariableId() {
        return VariableFactory.VAR_STATUS_CODE;
    }

    /**
     * Analyses the values of a particular indicator.
     *
     * @param args Arguments needed by the worker.
     *             In this case is expected:
     *             <ul>
     *             <li>status-code</li>
     *             </ul>
     * @return The analysis result.
     */
    @Override
    StatusCodeOutput analyse(Object... args) {
        int statusCode = CastHelper.cast(args[0], Integer.class);

        StatusCodeOutput output = new StatusCodeOutput();
        output.setValue(statusCode);

        return output;
    }

    /**
     * Analyses the values, inserts the results into the database and sends to Influx a report.
     *
     * @param website The {@link Website} instance with values related to the evaluated website.
     * @param args    Arguments needed by the worker.
     *                In this case is expected:
     *                <ul>
     *                <li>status-code</li>
     *                </ul>
     */
    @Override
    void fullWork(Website website, Object... args) {
        StatusCodeOutput output = analyse(args[0]);

        save(website, getIndicatorSlugWithPrefix(), output.getValue());

        // Reporting the evaluation to the monitor
        sendEvaluation(website, output);
    }

    @Override
    public void sendEvaluation(Website website, float evaluation, Long time) {
        evaluationsReporter.websitesEvaluationDay(
                website.getHostname(),
                getVariable(),
                evaluation >= 200f && evaluation < 400f ? 1 : 0,
                Integer.toString(Math.round(evaluation)),
                time);
    }

    @Override
    public void sendFailedEvaluation(Website website, Long time) {
        // For this variable the reports are not needed
    }

    /**
     * Evaluate if the website applies for having this variable evaluation
     *
     * @param args
     * @return {@link Boolean}
     */
    @Override
    boolean appliesForEvaluation (Object... args) {
        return true;
    }

}
