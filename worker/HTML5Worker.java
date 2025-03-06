package cu.redcuba.worker;

import cu.redcuba.output.HTML5Output;
import cu.redcuba.factory.VariableFactory;
import cu.redcuba.helper.CastHelper;
import cu.redcuba.object.Website;
import org.springframework.stereotype.Service;

@Service
public class HTML5Worker extends AbstractWorker<HTML5Output> {

    /**
     * Returns the ID of the variable evaluated by this worker.
     *
     * @return The variable's ID.
     */
    @Override
    long getVariableId() {
        return VariableFactory.VAR_HTML5;
    }

    /**
     * Analyses the values of a particular indicator.
     *
     * @param args Arguments needed by the worker.
     *             In this case is expected:
     *             <ul>
     *             <li>document type</li>
     *             <li>header amount</li>
     *             <li>footer amount</li>
     *             <li>nav amount</li>
     *             <li>article amount</li>
     *             <li>section amount</li>
     *             </ul>
     * @return The analysis result.
     */
    @Override
    public HTML5Output analyse(Object... args) {
        final String documentType = CastHelper.cast(args[0], String.class);
        final int headerAmount = CastHelper.cast(args[1], Integer.class);
        final int footerAmount = CastHelper.cast(args[2], Integer.class);
        final int navAmount = CastHelper.cast(args[3], Integer.class);
        final int articleAmount = CastHelper.cast(args[4], Integer.class);
        final int sectionAmount = CastHelper.cast(args[5], Integer.class);

        HTML5Output output = new HTML5Output();

        if (documentType == null) {
            output.setHasDocType(false);
        } else {
            String docType = documentType.trim().toLowerCase();

            if (docType.contains("<!doctype html>")) {
                output.setHasDocType(true);
            } else {
                output.setHasDocType(false);
            }
        }

        //Checking if is used the main semantic tags
        // https://www.w3.org/TR/html5-diff/
        // http://www.inwebson.com/html5/10-useful-html5-tags-and-attributes/
        if (headerAmount > 0 &&
                footerAmount > 0 &&
                navAmount > 0 &&
                (sectionAmount > 0 ||
                        articleAmount > 0)) {
            output.setIncludeSemanticTags(true);
        } else {
            output.setIncludeSemanticTags(false);
        }

        if (output.hasDoctype() && output.includeTags()) {
            output.setValue(VALUE_CORRECT);
        } else if (output.hasDoctype() || output.includeTags()) {
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
     *                <li>document type</li>
     *                <li>header amount</li>
     *                <li>footer amount</li>
     *                <li>nav amount</li>
     *                <li>article amount</li>
     *                <li>section amount</li>
     *                </ul>
     */
    public void fullWork(Website website, Object... args) {
        HTML5Output output = analyse(args);

        save(website, getIndicatorSlugWithPrefix(), output.getValue());
        save(website, getIndicatorSlugWithPrefix("doctype"), output.hasDoctype());
        save(website, getIndicatorSlugWithPrefix("tags"), output.includeTags());

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
