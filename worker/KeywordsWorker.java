package cu.redcuba.worker;

import cu.redcuba.factory.VariableFactory;
import cu.redcuba.factory.WebsiteKeywordsFactory;
import cu.redcuba.helper.CastHelper;
import cu.redcuba.helper.Language;
import cu.redcuba.helper.StopwordsHelper;
import cu.redcuba.object.Website;
import cu.redcuba.output.KeywordsOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class KeywordsWorker extends AbstractWorker<KeywordsOutput> {

    private static final int MAX_ALLOWED_KEYWORDS = 7;

    private static final int MIN_ALLOWED_KEYWORDS = 3;

    private final TitleWorker titleWorker;

    private final DescriptionWorker descriptionWorker;

    private final StopwordsHelper stopwordsHelper;

    private final WebsiteKeywordsFactory websiteKeywordsFactory;

    @Autowired
    public KeywordsWorker(
            TitleWorker titleWorker,
            DescriptionWorker descriptionWorker,
            StopwordsHelper stopwordsHelper,
            WebsiteKeywordsFactory websiteKeywordsFactory) {
        this.titleWorker = titleWorker;
        this.descriptionWorker = descriptionWorker;
        this.stopwordsHelper = stopwordsHelper;
        this.websiteKeywordsFactory = websiteKeywordsFactory;
    }

    /**
     * Returns the ID of the variable evaluated by this worker.
     *
     * @return The variable's ID.
     */
    @Override
    long getVariableId() {
        return VariableFactory.VAR_KEYWORDS;
    }

    /**
     * Analyses the values of a particular indicator.
     *
     * @param args Arguments needed by the worker.
     *             <ul>
     *             <li>keywords</li>
     *             <li>{@link cu.redcuba.helper.Language} instance with detected language</li>
     *             </ul>
     * @return The analysis result.
     */
    @Override
    public KeywordsOutput analyse(Object... args) {
        String keywords = CastHelper.cast(args[0], String.class);
        Language language = (Language) args[1];

        KeywordsOutput output = new KeywordsOutput();
        if (keywords == null || keywords.isEmpty()) {
            output.setKeywords(new LinkedList<>());
            output.setExist(false);
            output.setValue(VALUE_INCORRECT);
            return output;
        }

        output.setExist(true);

        String[] splittedKeywords = keywords.split(",");
        List<String> finalKeywords = new LinkedList<>();

        output.setNotHasStopwords(true);

        for (String keyword : splittedKeywords) {
            String lowerKeyword = keyword.toLowerCase().trim();
            if (!stopwordsHelper.isStopword(language, lowerKeyword)) {
                finalKeywords.add(lowerKeyword);
            } else {
                output.setNotHasStopwords(false);
            }
        }

        output.setKeywords(finalKeywords);

        output.setNotExceedsMaximum(finalKeywords.size() <= MAX_ALLOWED_KEYWORDS);

        output.setReachMinimum(finalKeywords.size() >= MIN_ALLOWED_KEYWORDS);

        output.setValue(VALUE_TO_IMPROVE);

        if (output.notExceedsMaximum() && output.reachMinimum() && output.notHasStopwords()) {
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
     *                <li>{@link cu.redcuba.helper.Language} instance with detected language</li>
     *                <li>keywords</li>
     *                <li>title</li>
     *                <li>description</li>
     *                </ul>
     */
    public void fullWork(Website website, Object... args) {
        KeywordsOutput output = analyse(args[1], args[0]);

        save(website, getIndicatorSlugWithPrefix(), output.getValue());
        save(website, getIndicatorSlugWithPrefix("exist"), output.exist());
        save(website, getIndicatorSlugWithPrefix("not-exceeds-maximum"), output.notExceedsMaximum());
        save(website, getIndicatorSlugWithPrefix("reach-minimum"), output.reachMinimum());
        save(website, getIndicatorSlugWithPrefix("not-has-stopwords"), output.notHasStopwords());

        //Reporting the evaluation to the monitor
        sendEvaluation(website, output);
        websiteKeywordsFactory.createAndSave(website.getId(), output.getKeywords());

        titleWorker.fullWork(website, output.getKeywords(), args[2]);
        descriptionWorker.fullWork(website, output.getKeywords(), args[3]);
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
