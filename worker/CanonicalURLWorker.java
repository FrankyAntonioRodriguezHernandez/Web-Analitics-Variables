package cu.redcuba.worker;

import cu.redcuba.output.CanonicalURLOutput;
import cu.redcuba.factory.VariableFactory;
import cu.redcuba.helper.CastHelper;
import cu.redcuba.object.Website;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class CanonicalURLWorker extends AbstractWorker<CanonicalURLOutput> {

    /**
     * Returns the ID of the variable evaluated by this worker.
     *
     * @return The variable's ID.
     */
    @Override
    public long getVariableId() {
        return VariableFactory.VAR_LINK_CANONICAL;
    }

    /**
     * Analyses the values of a particular indicator.
     *
     * @param args Arguments needed by the worker.
     *             In this case is expected:
     *             <ul>
     *             <li>canonical-url</li>
     *             </ul>
     * @return The analysis result.
     */
    @Override
    public CanonicalURLOutput analyse(Object... args) {
        String canonicalURL = CastHelper.cast(args[0], String.class);

        CanonicalURLOutput output = new CanonicalURLOutput();

        if (canonicalURL == null || canonicalURL.isEmpty()) {
            output.setExist(false);
            output.setValue(VALUE_INCORRECT);
            return output;
        }

        output.setExist(true);
        output.setValue(VALUE_TO_IMPROVE);

        try {
            // TODO: 2/12/17 Ver si esto se puede hacer con la libreria que se usa en OpenGraphAnalyser
            output.setAbsolute(new URI(canonicalURL).isAbsolute());
            output.setValid(true);
        } catch (URISyntaxException e) {
            output.setValid(false);
        }

        if (output.isAbsolute() && output.isValid()) {
            output.setValue(VALUE_CORRECT);
        }

        return output;
    }

    /**
     * Analyses the language value, inserts the result into the database and sends to Influx a report.
     *
     * @param website The {@link Website} instance with values related to the evaluated website.
     * @param args    Arguments needed by the worker.
     *                In this case is expected:
     *                <ul>
     *                <li>canonical-url</li>
     *                </ul>
     */
    public void fullWork(Website website, Object... args) {
        CanonicalURLOutput output = analyse(args[0]);

        save(website, getIndicatorSlugWithPrefix(), output.getValue());
        save(website, getIndicatorSlugWithPrefix("exist"), output.exist());
        save(website, getIndicatorSlugWithPrefix("valid"), output.isValid());
        save(website, getIndicatorSlugWithPrefix("absolute"), output.isAbsolute());

        //Reporting the evaluation to the monitor
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
