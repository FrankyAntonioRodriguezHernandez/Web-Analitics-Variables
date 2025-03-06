package cu.redcuba.worker;

import cu.redcuba.factory.WebsiteLanguageFactory;
import cu.redcuba.helper.Language;
import cu.redcuba.output.LanguageOutput;
import cu.redcuba.factory.VariableFactory;
import cu.redcuba.helper.CastHelper;
import cu.redcuba.helper.LocalesHelper;
import cu.redcuba.object.Website;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LanguageWorker extends AbstractWorker<LanguageOutput> {

    private static final LanguageOutput NOT_EXIST_OUTPUT;

    static {
        NOT_EXIST_OUTPUT = new LanguageOutput();
        NOT_EXIST_OUTPUT.setDeclaredLanguage(null);
        NOT_EXIST_OUTPUT.setDetectedLanguage(null);
        NOT_EXIST_OUTPUT.setExist(false);
        NOT_EXIST_OUTPUT.setValid(false);
        NOT_EXIST_OUTPUT.setMatchesDetected(false);
        NOT_EXIST_OUTPUT.setValue(VALUE_INCORRECT);
    }

    private final LocalesHelper localesHelper;

    private final WebsiteLanguageFactory websiteLanguageFactory;

    @Autowired
    public LanguageWorker(
            LocalesHelper localesHelper,
            WebsiteLanguageFactory websiteLanguageFactory
    ) {
        this.localesHelper = localesHelper;
        this.websiteLanguageFactory = websiteLanguageFactory;
    }

    /**
     * Returns the ID of the variable evaluated by this worker.
     *
     * @return The variable's ID.
     */
    @Override
    long getVariableId() {
        return VariableFactory.VAR_HTML_LANG;
    }

    /**
     * Analyses the values of a particular indicator.
     *
     * @param args Arguments needed by the worker. In this case is expected:
     *             <ul>
     *             <li>html-lang</li>
     *             <li>detected-lang</li>
     *             </ul>
     * @return The analysis result.
     */
    @Override
    public LanguageOutput analyse(Object... args) {
        String declaredLanguage = CastHelper.cast(args[0], String.class);
        String detectedLanguage = CastHelper.cast(args[1], String.class);

        return analyse(declaredLanguage, detectedLanguage);
    }

    private LanguageOutput analyse(String declaredLanguage, String detectedLanguage) {
        if (declaredLanguage == null) {
            NOT_EXIST_OUTPUT.setDetectedLanguage(detectedLanguage);
            return NOT_EXIST_OUTPUT;
        }

        declaredLanguage = declaredLanguage.trim();

        if (declaredLanguage.isEmpty()) {
            NOT_EXIST_OUTPUT.setDetectedLanguage(detectedLanguage);
            return NOT_EXIST_OUTPUT;
        }

        String declaredLanguageWithoutCountry = extractLanguage(declaredLanguage);

        LanguageOutput output = new LanguageOutput();

        output.setDeclaredLanguage(declaredLanguageWithoutCountry);

        output.setDetectedLanguage(detectedLanguage);

        output.setExist(true);

        output.setValid(localesHelper.isLocale(declaredLanguage));

        output.setValue(VALUE_TO_IMPROVE);

        if (output.getDetectedLanguage().isEmpty() || output.getDeclaredLanguage().equals(output.getDetectedLanguage())) {
            output.setMatchesDetected(true);
        }

        if (output.isValid() && output.matchesDetected()) {
            output.setValue(VALUE_CORRECT);
        }

        return output;
    }

    /**
     * Helper method to extract the language and delete the country.
     *
     * @param languageWithCountry Language with country included.
     * @return The language.
     */
    private String extractLanguage(String languageWithCountry) {
        return languageWithCountry.split("-")[0];
    }

    /**
     * Analyses the language value, inserts the result into the database and
     * sends to Influx a report.
     *
     * @param website The {@link Website} instance with values related to the
     *                evaluated website.
     * @param args    Arguments needed by the worker. In this case is expected:
     *                <ul>
     *                <li>html-lang</li>
     *                <li>detected-lang</li>
     *                <li>keywords</li>
     *                <li>title</li>
     *                <li>description</li>
     *                </ul>
     */
    public void fullWork(Website website, Object... args) {
        LanguageOutput output = analyse(args[0], args[1]);

        save(website, getIndicatorSlugWithPrefix(), output.getValue());
        save(website, getIndicatorSlugWithPrefix("exist"), output.exist());
        save(website, getIndicatorSlugWithPrefix("valid"), output.isValid());
        save(website, getIndicatorSlugWithPrefix("matches-detected"), output.matchesDetected());

        // Reporting the evaluation to the monitor
        sendEvaluation(website, output);
        websiteLanguageFactory.createAndSave(website.getId(), output.getDeclaredLanguage(), output.getDetectedLanguage());

        // Variables: keywords, title, description
        VariableFactory.getWorker(VariableFactory.VAR_KEYWORDS)
                .fullWork(website, Language.valueFrom(output.getDeclaredLanguage()), args[2], args[3], args[4]);
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
