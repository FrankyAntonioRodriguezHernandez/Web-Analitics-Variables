package cu.redcuba.worker;

import cu.redcuba.output.MinifiedHTMLOutput;
import cu.redcuba.factory.VariableFactory;
import cu.redcuba.object.Website;
import org.springframework.stereotype.Component;

@Component
public class MinifiedHTMLWorker extends AbstractWorker<MinifiedHTMLOutput> {

    /**
     * Returns the ID of the variable evaluated by this worker.
     *
     * @return The variable's ID.
     */
    @Override
    long getVariableId() {
        return VariableFactory.VAR_MINIFIED_HTML;
    }

    /**
     * Analyses the values of a particular indicator.
     *
     * @param args Arguments needed by the worker.
     *             In this case is expected:
     *             <ul>
     *             <li>has-repeated-whitespaces</li>
     *             </ul>
     * @return The analysis result.
     */
    @Override
    MinifiedHTMLOutput analyse(Object... args) {
        boolean hasRepeatedWhitespaces = ((Boolean) args[0]);

        MinifiedHTMLOutput output = new MinifiedHTMLOutput();

        output.setNotRepeatedWhitespaces(!hasRepeatedWhitespaces);

        if (output.isNotRepeatedWhitespaces()) {
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
     *                In this case is expected:
     *                <ul>
     *                <li>has-repeated-whitespaces</li>
     *                </ul>
     */
    @Override
    void fullWork(Website website, Object... args) {
        MinifiedHTMLOutput output = analyse(args[0]);

        save(website, getIndicatorSlugWithPrefix(), output.getValue());
        save(website, getIndicatorSlugWithPrefix("repeated-whitespaces"), output.isNotRepeatedWhitespaces());

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
    boolean appliesForEvaluation (Object... args) {
        return true;
    }

}
