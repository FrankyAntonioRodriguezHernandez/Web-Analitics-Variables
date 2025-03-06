package cu.redcuba.worker;

import cu.redcuba.factory.VariableFactory;
import cu.redcuba.helper.CastHelper;
import cu.redcuba.object.Website;
import cu.redcuba.output.XContentTypeOptionsOutput;

import org.springframework.stereotype.Service;

@Service
public class XContentTypeOptionsWorker extends AbstractWorker<XContentTypeOptionsOutput> {

    /**
     * Returns the ID of the variable evaluated by this worker.
     *
     * @return The variable's ID.
     */
    @Override
    long getVariableId() {
        return VariableFactory.VAR_X_CONTENT_TYPE_OPTIONS;
    }

    /**
     * Analyses the values of a particular indicator.
     *
     * @param args Arguments needed by the worker. In this case is expected:
     *             <ul>
     *             <li>xContentTypeOptions</li>
     *             </ul>
     * @return The analysis result.
     */
    @Override
    public XContentTypeOptionsOutput analyse(Object... args) {
        final String xContentTypeOptions = CastHelper.cast(args[0], String.class);

        XContentTypeOptionsOutput output = new XContentTypeOptionsOutput();

        output.setValid(false);
        output.setExist(false);
        if (xContentTypeOptions != null) {
            output.setExist(true);
            String lowerCaseXContentTypeOptions = xContentTypeOptions.toLowerCase();
            if (lowerCaseXContentTypeOptions.equals("nosniff")) {
                output.setValid(true);
            }
        }
        // TODO: 2019-04-22 Procesar el indicador valid, con exist  se obtendría VALUE_TO_IMPROVE y con valid VALUE_CORRECT.
        if (output.exist() && output.valid()) {
            output.setValue(VALUE_CORRECT);
        } else if (output.exist() && !output.valid()) {
            output.setValue(VALUE_TO_IMPROVE);
        } else {
            output.setValue(VALUE_INCORRECT);
        }

        return output;
    }

    /**
     * Analyses the values, inserts the results into the database and sends to
     * Influx a report.
     *
     * @param website The {@link Website} instance with values related to the
     *                evaluated website.
     * @param args    Arguments needed by the worker.
     */

    public void fullWork(Website website, Object... args) {

        XContentTypeOptionsOutput output = analyse(args);

        save(website, getIndicatorSlugWithPrefix(), output.getValue());
        save(website, getIndicatorSlugWithPrefix("exist"), output.exist());
        save(website, getIndicatorSlugWithPrefix("valid"), output.valid());

        // Reporting the evaluation to the monitor
        sendEvaluation(website, output);
    }

    /**
     * Evaluate if the website applies for having this variable evaluation
     *
     * @param args
     * @return {@link Boolean}
     */
    @Override
    boolean appliesForEvaluation(Object... args) {
        return true;
    }

}

