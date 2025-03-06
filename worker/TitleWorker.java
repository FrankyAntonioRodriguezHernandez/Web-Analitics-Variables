package cu.redcuba.worker;

import cu.redcuba.output.TitleOutput;
import cu.redcuba.factory.VariableFactory;
import cu.redcuba.helper.CastHelper;
import cu.redcuba.object.Website;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TitleWorker extends AbstractWorker<TitleOutput> {

    private static final int MAX_ALLOWED_CHARACTERS = 70;

    private static final int MIN_ALLOWED_CHARACTERS = 10;

    /**
     * Returns the ID of the variable evaluated by this worker.
     *
     * @return The variable's ID.
     */
    @Override
    long getVariableId() {
        return VariableFactory.VAR_TITLE;
    }

    /**
     * Analyses the values of a particular indicator.
     *
     * @param args Arguments needed by the worker.
     *             In this case is expected:
     *             <ul>
     *             <li>title</li>
     *             <li>keywords</li>
     *             </ul>
     * @return The analysis result.
     */
    @Override
    public TitleOutput analyse(Object... args) {
        final String title = CastHelper.cast(args[0], String.class);
        @SuppressWarnings("unchecked") final List<String> keywords = (List<String>) args[1];

        TitleOutput output = new TitleOutput();

        if (title == null || title.isEmpty()) {
            output.setExist(false);
            output.setValue(VALUE_INCORRECT);
            return output;
        }

        output.setExist(true);

        output.setNotExceedsMaximum(title.length() <= MAX_ALLOWED_CHARACTERS);

        output.setReachMinimum(title.length() >= MIN_ALLOWED_CHARACTERS);

        output.setHasKeywords(false);

        String lowerTitle = title.trim().toLowerCase();
        for (String keyword : keywords) {
            if (lowerTitle.contains(keyword)) {
                output.setHasKeywords(true);
                break;
            }
        }

        output.setValue(VALUE_TO_IMPROVE);

        if (output.notExceedsMaximum() && output.reachMinimum() && output.hasKeywords()) {
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
     *                <li>keywords</li>
     *                <li>title</li>
     *                </ul>
     */
    public void fullWork(Website website, Object... args) {
        TitleOutput output = analyse(args[1], args[0]);

        save(website, getIndicatorSlugWithPrefix(), output.getValue());
        save(website, getIndicatorSlugWithPrefix("exist"), output.exist());
        save(website, getIndicatorSlugWithPrefix("not-exceeds-maximum"), output.notExceedsMaximum());
        save(website, getIndicatorSlugWithPrefix("reach-minimum"), output.reachMinimum());
        save(website, getIndicatorSlugWithPrefix("has-keywords"), output.hasKeywords());

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
