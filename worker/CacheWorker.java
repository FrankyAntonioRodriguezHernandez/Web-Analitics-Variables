package cu.redcuba.worker;

import cu.redcuba.output.CacheOutput;
import cu.redcuba.factory.VariableFactory;
import cu.redcuba.helper.CastHelper;
import cu.redcuba.object.Website;
import org.springframework.stereotype.Service;

@Service
public class CacheWorker extends AbstractWorker<CacheOutput> {

    /**
     * Returns the ID of the variable evaluated by this worker.
     *
     * @return The variable's ID.
     */
    @Override
    long getVariableId() {
        return VariableFactory.VAR_CACHE_HEADER;
    }

    /**
     * Analyses the values of a particular indicator.
     *
     * @param args Arguments needed by the worker.
     *             In this case is expected:
     *             <ul>
     *             <li>cache-control</li>
     *             </ul>
     * @return The analysis result.
     */
    @Override
    public CacheOutput analyse(Object... args) {
        final String cacheControl = CastHelper.cast(args[0], String.class);

        CacheOutput output = new CacheOutput();

        // https://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html
        // https://www.keycdn.com/blog/http-cache-headers/
        // https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Cache-Control
        if (cacheControl == null) {
            output.setSupportCache(true);
        } else {
            if (cacheControl.contains("no-cache") || cacheControl.contains("no-store")) {
                output.setSupportCache(false);
            } else {
                output.setSupportCache(true);
            }
        }

        if (output.supportCache()) {
            output.setValue(VALUE_CORRECT);
        } else {
            output.setValue(VALUE_INCORRECT);
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
     *                <li>cache-control</li>
     *                </ul>
     */
    public void fullWork(Website website, Object... args) {
        CacheOutput output = analyse(args[0]);

        save(website, getIndicatorSlugWithPrefix(), output.getValue());
        save(website, getIndicatorSlugWithPrefix("support"), output.supportCache());

        //Reporting the evaluation to the monitor
        sendEvaluation(website, output);
    }

    /**
     * Evaluate if the website applies for having a Cache variable evaluation
     *
     * @param args
     * @return {@link Boolean}
     */
    @Override
    boolean appliesForEvaluation (Object... args) {
        return true;
    }

}
