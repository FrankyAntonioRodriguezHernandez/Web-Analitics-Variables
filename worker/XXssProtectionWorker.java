package cu.redcuba.worker;

import cu.redcuba.factory.VariableFactory;
import cu.redcuba.helper.CastHelper;
import cu.redcuba.object.Website;
import cu.redcuba.output.XXssProtectionOutput;

import org.springframework.stereotype.Service;

@Service
public class XXssProtectionWorker extends AbstractWorker<XXssProtectionOutput> {

    /**
     * Returns the ID of the variable evaluated by this worker.
     *
     * @return The variable's ID.
     */
    @Override
    long getVariableId() {
        return VariableFactory.VAR_X_XSS_PROTECTION;
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
    public XXssProtectionOutput analyse(Object... args) {
        final String xXssProtection = CastHelper.cast(args[0], String.class);

        XXssProtectionOutput output = new XXssProtectionOutput();

        output.setValid(false);
        output.setExist(false);
        if (xXssProtection != null) {
            output.setExist(true);
            String lowerCaseXXssProtection = xXssProtection.toLowerCase();
            if (lowerCaseXXssProtection.equals("0") || lowerCaseXXssProtection.equals("1") ||
                    lowerCaseXXssProtection.equals("1; mode=block")) {
                output.setValid(true);
            }
        }

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

        XXssProtectionOutput output = analyse(args);

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
