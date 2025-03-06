package cu.redcuba.worker;

import cu.redcuba.output.OpenGraphOutput;
import cu.redcuba.factory.VariableFactory;
import cu.redcuba.object.Website;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

@Service
public class OpenGraphWorker extends AbstractWorker<OpenGraphOutput> {

    private static final ArrayList<String> VALID_WEB_OG_TYPES;

    static {
        VALID_WEB_OG_TYPES = new ArrayList<>();
        VALID_WEB_OG_TYPES.add("website");
        VALID_WEB_OG_TYPES.add("article");
    }

    /**
     * Returns the ID of the variable evaluated by this worker.
     *
     * @return The variable's ID.
     */
    @Override
    long getVariableId() {
        return VariableFactory.VAR_OPEN_GRAPH;
    }

    /**
     * Analyses the values of a particular indicator.
     *
     * @param args Arguments needed by the worker.
     *             In this case is expected:
     *             <ul>
     *             <li>meta</li>
     *             </ul>
     * @return The analysis result.
     */
    @Override
    public OpenGraphOutput analyse(Object... args) {
        @SuppressWarnings("unchecked") final Map<String, String> meta = (Map<String, String>) args[0];

        OpenGraphOutput output = new OpenGraphOutput();

        // Checking type
        if (meta.containsKey("og:type") &&
                VALID_WEB_OG_TYPES.contains(meta.get("og:type"))) {
            output.setHasType(true);
        } else {
            output.setHasType(false);
        }

        // Checking URL
        UrlValidator urlValidator = new UrlValidator();
        if (meta.containsKey("og:url") &&
                urlValidator.isValid(meta.get("og:url"))) {
            output.setHasUrl(true);
        } else {
            output.setHasUrl(false);
        }

        // Checking Title
        if (meta.containsKey("og:title") &&
                !meta.get("og:title").isEmpty()) {
            output.setHasTitle(true);
        } else {
            output.setHasTitle(false);
        }

        // Checking Description
        if (meta.containsKey("og:description") &&
                !meta.get("og:description").isEmpty()) {
            output.setHasDescription(true);
        } else {
            output.setHasDescription(false);
        }

        // Checking Image
        if (meta.containsKey("og:image") &&
                !meta.get("og:image").isEmpty()) {
            output.setHasImage(true);
        } else {
            output.setHasImage(false);
        }

        if (output.hasType() && output.hasUrl() && output.hasTitle() && output.hasDescription() && output.isHasImage()) {
            output.setValue(VALUE_CORRECT);
        } else if (output.hasType() || output.hasUrl() || output.hasTitle() || output.hasDescription() || output.isHasImage()) {
            output.setValue(VALUE_TO_IMPROVE);
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
     *                <li>meta</li>
     *                </ul>
     */
    public void fullWork(Website website, Object... args) {
        OpenGraphOutput output = analyse(args[0]);

        save(website, getIndicatorSlugWithPrefix(), output.getValue());
        save(website, getIndicatorSlugWithPrefix("type"), output.hasType());
        save(website, getIndicatorSlugWithPrefix("url"), output.hasUrl());
        save(website, getIndicatorSlugWithPrefix("title"), output.hasTitle());
        save(website, getIndicatorSlugWithPrefix("description"), output.hasDescription());
        save(website, getIndicatorSlugWithPrefix("image"), output.isHasImage());

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
    boolean appliesForEvaluation(Object... args) {
        return true;
    }

}
