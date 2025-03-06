package cu.redcuba.worker;

import cu.redcuba.factory.VariableFactory;
import cu.redcuba.helper.CastHelper;
import cu.redcuba.object.Website;
import cu.redcuba.output.PluginOuput;
import org.springframework.stereotype.Service;

@Service
public class PluginWorker extends AbstractWorker<PluginOuput> {

    /**
     * Returns the ID of the variable evaluated by this worker.
     *
     * @return The variable's ID.
     */
    @Override
    long getVariableId() {
        return VariableFactory.VAR_PLUGIN;
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
    public PluginOuput analyse(Object... args) {

        final String flash = CastHelper.cast(args[0], String.class);
        final String java = CastHelper.cast(args[1], String.class);
        final String silverlight = CastHelper.cast(args[2], String.class);

        PluginOuput output = new PluginOuput();

        output.setNoExistFlash(true);
        output.setNoExistJava(true);
        output.setNoExistSilverlight(true);

        if (flash != null) {
            output.setNoExistFlash(false);
        }

        if (java != null) {
            output.setNoExistJava(false);
        }

        if (silverlight != null) {
            output.setNoExistSilverlight(false);
        }


        // TODO: 2019-02-26 Procesar el nuevo indicador relacionado con que meta name viewport tenga valor válido.

        if (output.isNoExistFlash() && output.isNoExistJava() && output.isNoExistSilverlight()) {
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
     * @param website The {@link cu.redcuba.object.Website} instance with values related to the
     *                evaluated website.
     * @param args    Arguments needed by the worker.
     */

    public void fullWork(Website website, Object... args) {

        PluginOuput output = analyse(args);
        save(website, getIndicatorSlugWithPrefix(), output.getValue());
        save(website, getIndicatorSlugWithPrefix("no-flash-exist"), output.isNoExistFlash());
        save(website, getIndicatorSlugWithPrefix("no-java-exist"), output.isNoExistJava());
        save(website, getIndicatorSlugWithPrefix("no-silverlight-exist"), output.isNoExistSilverlight());

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
