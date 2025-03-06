package cu.redcuba.worker;

import cu.redcuba.output.WeightOutput;
import cu.redcuba.factory.VariableFactory;
import cu.redcuba.object.Website;
import org.springframework.stereotype.Service;

@Service
public class WeightWorker extends AbstractWorker<WeightOutput> {

    /**
     * Returns the ID of the variable evaluated by this worker.
     *
     * @return The variable's ID.
     */
    @Override
    long getVariableId() {
        return VariableFactory.VAR_WEIGHT;
    }

    /**
     * Analyses the values of a particular indicator.
     *
     * @param args Arguments needed by the worker.
     *             In this case is expected:
     *             <ul>
     *             <li>weight</li>
     *             </ul>
     * @return The analysis result.
     */
    @Override
    public WeightOutput analyse(Object... args) {
        WeightOutput weightOutput = new WeightOutput();
        weightOutput.setWeight(Float.parseFloat(args[0].toString()));

        return weightOutput;
    }

    /**
     * Analyses the language value, inserts the result into the database and sends to Influx a report.
     *
     * @param website The {@link Website} instance with values related to the evaluated website.
     * @param args    Arguments needed by the worker.
     *                In this case is expected:
     *                <ul>
     *                <li>weight</li>
     *                </ul>
     */
    public void fullWork(Website website, Object... args) {
        WeightOutput output = analyse(args[0]);

        save(website, getIndicatorSlugWithPrefix(), output.getWeight());
    }

    @Override
    public void sendEvaluation(Website website, float evaluation, Long time) {
        // For this variable the reports are not needed, but perhaps could be something like
        // evaluation >= 1024 ? EVALUATIONS_STRING.get(1F) : EVALUATIONS_STRING.get(0F);
    }

    @Override
    public void sendFailedEvaluation(Website website, Long time) {
        // For this variable the reports are not needed
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
