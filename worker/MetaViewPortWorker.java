package cu.redcuba.worker;

import cu.redcuba.factory.VariableFactory;
import cu.redcuba.helper.CastHelper;
import cu.redcuba.object.Website;
import cu.redcuba.output.ResponsiveOutput;
import org.springframework.stereotype.Service;

@Service
public class MetaViewPortWorker extends AbstractWorker<ResponsiveOutput> {

    /**
     * Returns the ID of the variable evaluated by this worker.
     *
     * @return The variable's ID.
     */
    @Override
    long getVariableId() {
        return VariableFactory.VAR_META_VIEW_PORT;
    }

    /**
     * Analyses the values of a particular indicator.
     *
     * @param args Arguments needed by the worker. In this case is expected:
     *             <ul>
     *             <li>viewport</li>
     *             </ul>
     * @return The analysis result.
     */
    @Override
    public ResponsiveOutput analyse(Object... args) {

        final String viewport = CastHelper.cast(args[0], String.class);

        ResponsiveOutput output = new ResponsiveOutput();

        output.setExistViewport(false);

        if (viewport != null) {
            output.setExistViewport(true);
        } else {
            output.setExistViewport(false);
        }

        // TODO: 2019-02-26 Procesar el nuevo indicador relacionado con que meta name viewport tenga valor válido.

        if (output.existViewport()) {
            output.setValue(VALUE_CORRECT);
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

        ResponsiveOutput output = analyse(args);

        save(website, getIndicatorSlugWithPrefix(), output.getValue());
        save(website, getIndicatorSlugWithPrefix("exist"), output.existViewport());

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
