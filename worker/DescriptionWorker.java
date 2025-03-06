package cu.redcuba.worker;

import cu.redcuba.output.DescriptionOutput;
import cu.redcuba.factory.VariableFactory;
import cu.redcuba.helper.CastHelper;
import cu.redcuba.object.Website;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DescriptionWorker extends AbstractWorker<DescriptionOutput> {

    private static final int MAX_ALLOWED_CHARACTERS = 160;

    private static final int MIN_ALLOWED_CHARACTERS = 70;

    private static final float MIN_PERCENT_INCLUDED_KEYWORDS = 10f;

    /**
     * Returns the ID of the variable evaluated by this worker.
     *
     * @return The variable's ID.
     */
    @Override
    long getVariableId() {
        return VariableFactory.VAR_DESCRIPTION;
    }

    /**
     * Analyses the values of a particular indicator.
     *
     * @param args Arguments needed by the worker.
     *             In this case is expected:
     *             <ul>
     *             <li>description</li>
     *             <li>keywords</li>
     *             </ul>
     * @return The analysis result.
     */
    @Override
    public DescriptionOutput analyse(Object... args) {
        final String description = CastHelper.cast(args[0], String.class);
        @SuppressWarnings("unchecked") List<String> keywords = (List<String>) args[1];

        DescriptionOutput output = new DescriptionOutput();
        if (description == null || description.isEmpty()) {
            output.setExist(false);
            output.setValue(VALUE_INCORRECT);
            return output;
        }

        output.setExist(true);

        output.setNotExceedsMaximum(description.length() <= MAX_ALLOWED_CHARACTERS);

        output.setReachMinimum(description.length() >= MIN_ALLOWED_CHARACTERS);

        output.setHasKeywords(false);

        output.setValue(VALUE_TO_IMPROVE);

        //Checking if the keywords are included into the descriptions
        float amountIncludedKeywords = 0;
        String lowerDescription = description.toLowerCase();
        for (String keyword : keywords) {
            if (lowerDescription.contains(keyword)) {
                amountIncludedKeywords++;
            }
        }

        //Calculating the percent of included keywords
        float percent = amountIncludedKeywords / keywords.size() * 100;
        output.setHasKeywords(percent >= MIN_PERCENT_INCLUDED_KEYWORDS);

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
     *                <li>description</li>
     *                </ul>
     */
    public void fullWork(Website website, Object... args) {
        DescriptionOutput output = analyse(args[1], args[0]);

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
