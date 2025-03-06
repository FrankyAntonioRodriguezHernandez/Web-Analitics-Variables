package cu.redcuba.worker;

import cu.redcuba.entity.Variable;
import cu.redcuba.evaluations.reporter.EvaluationsReporter;
import cu.redcuba.output.Output;
import cu.redcuba.factory.DailyEvaluationFactory;
import cu.redcuba.factory.VariableFactory;
import cu.redcuba.object.Website;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractWorker<O extends Output> {

    static final Map<Float, String> EVALUATIONS_STRING;

    static {
        EVALUATIONS_STRING = new HashMap<>();

        EVALUATIONS_STRING.put(0F, "No");
        EVALUATIONS_STRING.put(0.5F, "A mejorar");
        EVALUATIONS_STRING.put(1F, "Si");
        EVALUATIONS_STRING.put(-1F, "N/E");
        EVALUATIONS_STRING.put(-2F, "N/A");
    }

    static final float VALUE_NA = -2f;

    static final float VALUE_INCORRECT = 0f;

    static final float VALUE_TO_IMPROVE = 0.5f;

    static final float VALUE_CORRECT = 1f;

    private DailyEvaluationFactory dailyEvaluationFactory;

    EvaluationsReporter evaluationsReporter;

    private VariableFactory variableFactory;

    @Autowired
    public void setDailyEvaluationFactory(DailyEvaluationFactory dailyEvaluationFactory) {
        this.dailyEvaluationFactory = dailyEvaluationFactory;
    }

    @Autowired
    public void setEvaluationsReporter(EvaluationsReporter evaluationsReporter) {
        this.evaluationsReporter = evaluationsReporter;
    }

    @Autowired
    public void setVariableFactory(VariableFactory variableFactory) {
        this.variableFactory = variableFactory;
    }

    /**
     * Returns the ID of the variable evaluated by this worker.
     *
     * @return The variable's ID.
     */
    abstract long getVariableId();

    /**
     * Analyses the values of a particular indicator.
     *
     * @param args Arguments needed by the worker.
     * @return The analysis result.
     */
    abstract O analyse(Object... args);

    /**
     * Analyses the values, inserts the results into the database and sends to Influx a report.
     *
     * @param website The {@link Website} instance with values related to the evaluated website.
     * @param args    Arguments needed by the worker.
     */
    abstract void fullWork(Website website, Object... args);

    /**
     * Analyses if the website applies for evaluation of the variable.
     *
     * @param args Arguments needed for analysis.
     */
    abstract boolean appliesForEvaluation(Object... args);

    /**
     * Sends to Influx a report with the evaluation.
     *
     * @param website The {@link Website} instance with values related to the evaluated website.
     * @param output  The output as the result of the evaluation.
     */
    void sendEvaluation(Website website, O output) {
        sendEvaluation(website, output.getValue(), null);
    }

    /**
     * Sends to Influx a report with the evaluation.
     *
     * @param website    The {@link Website} instance with values related to the evaluated website.
     * @param evaluation The evaluation to report.
     */
    public void sendEvaluation(Website website, float evaluation, Long time) {
        evaluationsReporter.websitesEvaluationDay(
                website.getHostname(),
                getVariable(),
                evaluation,
                EVALUATIONS_STRING.get(evaluation),
                time);
    }

    /**
     * Sends to Influx a report about the failure.
     *
     * @param website The {@link Website} instance with values related to not evaluated website.
     */
    public void sendFailedEvaluation(Website website, Long time) {
        //Reporting the failure to the monitor
        evaluationsReporter.websitesEvaluationDay(
                website.getHostname(),
                getVariable(),
                EVALUATIONS_STRING.get(-1f),
                time);
    }

    /**
     * Returns the variable evaluated by this worker.
     *
     * @return The variable's ID.
     */
    Variable getVariable() {
        return variableFactory.getVariable(getVariableId());
    }

    /**
     * Returns the name of the indicator given in mind the name of the variable.
     *
     * @return The indicator's name.
     */
    String getIndicatorSlugWithPrefix(String slug) {
        return String.format("%s-%s", getVariable().getSlug(), slug);
    }

    /**
     * Returns the default indicator (which name is value) given in mind the name of the variable.
     *
     * @return The indicator's name.
     */
    String getIndicatorSlugWithPrefix() {
        return getIndicatorSlugWithPrefix("value");
    }

    /**
     * @param website
     * @param indicatorSlug
     * @param evaluation
     */
    void save(Website website, String indicatorSlug, float evaluation) {
        dailyEvaluationFactory.createAndSave(
                website.getId(),
                website.isInternational(),
                getVariable().getId(),
                variableFactory.getIndicator(indicatorSlug).getId(),
                new Date(),
                evaluation);
    }

    void save(Website website, String indicatorSlug, boolean evaluation) {
        save(website, indicatorSlug, evaluation ? 1f : 0f);
    }

}
