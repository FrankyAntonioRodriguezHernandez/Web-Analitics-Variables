package cu.redcuba.worker;

import cu.redcuba.output.TwitterCardOutput;
import cu.redcuba.factory.VariableFactory;
import cu.redcuba.object.Website;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

@Service
public class TwitterCardWorker extends AbstractWorker<TwitterCardOutput> {

    private static final String TWITTER_SITE_PATTERN = "^@[A-Za-z0-9_]{1,15}$";

    private static final String TWITTER_SITE_ID_PATTERN = "^[0-9]+$";

    private static final ArrayList<String> VALID_WEB_TWITTER_CARDS;

    static {
        VALID_WEB_TWITTER_CARDS = new ArrayList<>();
        VALID_WEB_TWITTER_CARDS.add("summary");
        VALID_WEB_TWITTER_CARDS.add("summary_large_image");
    }

    /**
     * Returns the ID of the variable evaluated by this worker.
     *
     * @return The variable's ID.
     */
    @Override
    long getVariableId() {
        return VariableFactory.VAR_TWITTER_CARD;
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
    public TwitterCardOutput analyse(Object... args) {
        @SuppressWarnings("unchecked") final Map<String, String> meta = (Map<String, String>) args[0];

        TwitterCardOutput output = new TwitterCardOutput();

        // Checking the Twitter Card
        if (meta.containsKey("twitter:card") &&
                VALID_WEB_TWITTER_CARDS.contains(meta.get("twitter:card"))) {
            output.setHasTwitterCard(true);
        } else {
            output.setHasTwitterCard(false);
        }

        // Checking the identifier
        if (meta.containsKey("twitter:site") &&
                meta.get("twitter:site").matches(TWITTER_SITE_PATTERN)) {
            output.setHasTwitterSite(true);
        }
        // Second variant
        else if (meta.containsKey("twitter:site:id") &&
                meta.get("twitter:site:id").matches(TWITTER_SITE_ID_PATTERN)) {
            output.setHasTwitterSite(true);
        } else {
            output.setHasTwitterSite(false);
        }

        // Checking the title
        if (meta.containsKey("twitter:title") &&
                !meta.get("twitter:title").isEmpty()) {
            output.setHasTwitterTitle(true);
        } else {
            output.setHasTwitterTitle(false);
        }

        if (meta.containsKey("twitter:description") &&
                !meta.get("twitter:description").isEmpty()) {
            output.setHasTwitterDescription(true);
        } else {
            output.setHasTwitterDescription(false);
        }

        if (meta.containsKey("twitter:image") &&
                !meta.get("twitter:image").isEmpty()) {
            output.setHasTwitterImage(true);
        } else {
            output.setHasTwitterImage(false);
        }


        if (output.hasTwitterCard() && output.hasTwitterSite() && output.hasTwitterTitle() && output.hasTwitterDescription() && output.isHasTwitterImage()) {
            output.setValue(VALUE_CORRECT);
        } else if (output.hasTwitterCard() || output.hasTwitterSite() || output.hasTwitterTitle() || output.hasTwitterDescription() || output.isHasTwitterImage()) {
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
        TwitterCardOutput output = analyse(args[0]);

        save(website, getIndicatorSlugWithPrefix(), output.getValue());
        save(website, getIndicatorSlugWithPrefix("card"), output.hasTwitterCard());
        save(website, getIndicatorSlugWithPrefix("site"), output.hasTwitterSite());
        save(website, getIndicatorSlugWithPrefix("title"), output.hasTwitterTitle());
        save(website, getIndicatorSlugWithPrefix("description"), output.hasTwitterDescription());
        save(website, getIndicatorSlugWithPrefix("image"), output.isHasTwitterImage());

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
