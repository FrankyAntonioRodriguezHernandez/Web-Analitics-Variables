package cu.redcuba.worker;

import cu.redcuba.factory.VariableFactory;
import cu.redcuba.helper.CastHelper;
import cu.redcuba.object.Website;
import cu.redcuba.output.XFrameOptionsOutput;

import org.springframework.stereotype.Service;

@Service
public class XFrameOptionsWorker extends AbstractWorker<XFrameOptionsOutput> {

    /**
     * Returns the ID of the variable evaluated by this worker.
     *
     * @return The variable's ID.
     */
    @Override
    long getVariableId() {
        return VariableFactory.VAR_X_FRAME_OPTIONS;
    }

    /**
     * Analyses the values of a particular indicator.
     *
     * @param args Arguments needed by the worker. In this case is expected:
     *             <ul>
     *             <li>xFrameOptions</li>
     *             </ul>
     * @return The analysis result.
     */
    @Override
    public XFrameOptionsOutput analyse(Object... args) {
        final String xFrameOptions = CastHelper.cast(args[0], String.class);

        XFrameOptionsOutput output = new XFrameOptionsOutput();

        output.setValid(false);
        output.setExist(false);
        if (xFrameOptions != null) {
            output.setExist(true);
            String lowerCaseXFrameOptions = xFrameOptions.toLowerCase();

            if (lowerCaseXFrameOptions.equals("deny") || lowerCaseXFrameOptions.equals("sameorigin") ||
                    lowerCaseXFrameOptions.contains("allow-from https://")) {
                output.setValid(true);
            }
        }


        // TODO: 2019-04-22 Procesar el indicador valid, con exist se obtendría VALUE_TO_IMPROVE y con valid VALUE_CORRECT.

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

        XFrameOptionsOutput output = analyse(args);

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
