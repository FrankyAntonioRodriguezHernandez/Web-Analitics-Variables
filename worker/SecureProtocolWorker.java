package cu.redcuba.worker;

import cu.redcuba.entity.Visit;
import cu.redcuba.factory.VariableFactory;
import cu.redcuba.helper.CastHelper;
import cu.redcuba.helper.UrlFetchHelper;
import cu.redcuba.object.Website;
import cu.redcuba.output.SecureProtocolOutput;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SecureProtocolWorker extends AbstractWorker<SecureProtocolOutput> {

    /**
     * Returns the ID of the variable evaluated by this worker.
     *
     * @return The variable's ID.
     */

    private final UrlFetchHelper normalUrlFetchHelper;

    @Autowired
    public SecureProtocolWorker(UrlFetchHelper normalUrlFetchHelper) {
        this.normalUrlFetchHelper = normalUrlFetchHelper;
    }

    @Override
    long getVariableId() {
        return VariableFactory.VAR_SECURE_PROTOCOL;
    }

    /**
     * Analyses the values of a particular indicator.
     *
     * @param args Arguments needed by the worker. In this case is expected:
     *             <ul>
     *             <li>lastUrl</li>
     *             </ul>
     * @return The analysis result.
     */
    @Override
    public SecureProtocolOutput analyse(Object... args) {
        final String lastUrl = CastHelper.cast(args[0], String.class);
        final String hostname = CastHelper.cast(args[1], String.class);

        SecureProtocolOutput output = new SecureProtocolOutput();

        if (lastUrl.contains("https")) {
            output.setAnswerHttps(true);
            output.setRedirectionHttpHttps(true);
        } else {
            output.setRedirectionHttpHttps(false);
            Visit answerHttp = normalUrlFetchHelper.visitUnstored("https://" + hostname);
            if (!answerHttp.getLastUrl().equals("")) {
                output.setAnswerHttps(true);
            } else {
                output.setAnswerHttps(false);
            }
        }
        // TODO: 2019-04-22 Cuando exista una entidad certificadora trabajar la validez del certificado.
        if (output.redirectionHttpHttps()) {
            output.setValue(VALUE_CORRECT);
        } else {
            if (output.answerHttps()) {
                output.setValue(VALUE_TO_IMPROVE);
            } else {
                output.setValue(VALUE_INCORRECT);
            }
        }
        return output;
    }

    /**
     * Analyses the values, inserts the results into the database and sends to
     * Influx a report.
     *
     * @param website The {@link Website} instance with values related to the
     *                evaluated website.
     * @param args    Arguments needed by the worker.
     */

    public void fullWork(Website website, Object... args) {

        SecureProtocolOutput output = analyse(args);

        save(website, getIndicatorSlugWithPrefix(), output.getValue());
        save(website, getIndicatorSlugWithPrefix("redirection-http-https"), output.redirectionHttpHttps());
        save(website, getIndicatorSlugWithPrefix("answer-https"), output.answerHttps());
        // Reporting the evaluation to the monitor
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
