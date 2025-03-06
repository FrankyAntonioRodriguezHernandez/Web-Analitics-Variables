package cu.redcuba.worker;

import com.google.gson.Gson;
import cu.redcuba.client.notifier.model.RedCubaScriptValue;
import cu.redcuba.factory.VariableFactory;
import cu.redcuba.helper.CastHelper;
import cu.redcuba.object.Website;
import cu.redcuba.output.AnalyticToolsOutput;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AnalyticToolsWorker extends AbstractWorker<AnalyticToolsOutput> {

    private static final Gson GSON = new Gson();

    private final RabbitTemplate directRabbitTemplate;

    private final String redCubaScriptValueQueueName;

    public AnalyticToolsWorker(
            @Qualifier("directWorkTemplate") RabbitTemplate directRabbitTemplate,
            @Value("${evw.queue.notifier.redCubaScriptValue}") String redCubaScriptValueQueueName
    ) {
        this.directRabbitTemplate = directRabbitTemplate;
        this.redCubaScriptValueQueueName = redCubaScriptValueQueueName;
    }

    /**
     * Returns the ID of the variable evaluated by this worker.
     *
     * @return Tha variable's ID.
     */
    @Override
    public long getVariableId() {
        return VariableFactory.VAR_ANALYTIC_TOOLS;
    }

    /**
     * Analyses the values of a particular indicator.
     * Removed param imageGoogleAnalytic for google-analytic-image indicator.
     *
     * @param args Arguments needed by the worker. In this case is expected:
     *             <ul>
     *             <li>script Google Analytics</li>
     *             <li>script Piwik</li>
     *             <li>image Piwik</li>
     *             <li>script Matomo</li>
     *             <li>image Matomo</li>
     *             <li>script Google Tag</li>
     *             <li>script RedCuba</li>
     *             </ul>
     * @return The analysis result.
     */
    @Override
    public AnalyticToolsOutput analyse(Object... args) {
        final String scriptGoogleAnalytics = CastHelper.cast(args[0], String.class);
        final String scriptPiwik = CastHelper.cast(args[1], String.class);
        final String imagePiwik = CastHelper.cast(args[2], String.class);
        final String scriptMatomo = CastHelper.cast(args[3], String.class);
        final String imageMatomo = CastHelper.cast(args[4], String.class);
        final String scriptRedcuba = CastHelper.cast(args[5], String.class);
        final String gaTrackingIdScriptGtag = CastHelper.cast(args[6], String.class);

        AnalyticToolsOutput output = new AnalyticToolsOutput();

        output.setGaTrackingIdScriptGtag(gaTrackingIdScriptGtag);

        output.setHasGoogleAnalyticsScript(scriptGoogleAnalytics != null);

        output.setHasPiwikScript(scriptPiwik != null);

        output.setHasPiwikImage(imagePiwik != null);

        output.setHasMatomoScript(scriptMatomo != null);

        output.setHasMatomoImage(imageMatomo != null);

        output.setHasGoogleTagScript(gaTrackingIdScriptGtag != null);

        output.setHasRedCubaScript(scriptRedcuba != null);

        // Actualmente
        //[Bien] si tiene alguno de los dos scripts, GA, Piwik, Gtag o Matomo
        //[A mejorar] si tiene solo la imagen de Piwik o Matomo
        //[Mal] Si no tiene nada
        if (output.hasGoogleAnalyticsScript() || output.hasPiwikScript() || output.hasGoogleTagScript() || output.hasMatomoScript() || output.isHasRedCubaScript()) {
            output.setValue(VALUE_CORRECT);
        } else if (output.hasPiwikImage() || output.hasMatomoImage()) {
            output.setValue(VALUE_TO_IMPROVE);
        } else {
            output.setValue(VALUE_INCORRECT);
        }

        return output;
    }

    /**
     * Analyses the language value, inserts the result into the database and
     * sends to Influx a report.
     * Removed param imageGoogleAnalytic for google-analytic-image indicator.
     *
     * @param website The {@link Website} instance with values related to the
     *                evaluated website.
     * @param args    Arguments needed by the worker. In this case is expected:
     *                <ul>
     *                <li>script Google Analytic</li>
     *                <li>script Piwik</li>
     *                <li>image Piwik</li>
     *                <li>script Matomo</li>
     *                <li>image Matomo</li>
     *                <li>script Google Tag</li>
     *                <li>script RedCuba</li>
     *                </ul>
     */
    public void fullWork(Website website, Object... args) {
        final String scriptGoogleAnalytics = CastHelper.cast(args[0], String.class);
        final String scriptPiwik = CastHelper.cast(args[1], String.class);
        final String imagePiwik = CastHelper.cast(args[2], String.class);
        final String scriptMatomo = CastHelper.cast(args[3], String.class);
        final String imageMatomo = CastHelper.cast(args[4], String.class);
        final String scriptRedcuba = CastHelper.cast(args[5], String.class);
        final String gaTrackingIdScriptGtag = CastHelper.cast(args[6], String.class);

        AnalyticToolsOutput output = analyse(scriptGoogleAnalytics, scriptPiwik, imagePiwik, scriptMatomo, imageMatomo, scriptRedcuba, gaTrackingIdScriptGtag);

        save(website, getIndicatorSlugWithPrefix(), output.getValue());
        save(website, getIndicatorSlugWithPrefix("google-analytic-script"), output.hasGoogleAnalyticsScript());
        save(website, getIndicatorSlugWithPrefix("piwik-script"), output.hasPiwikScript());
        save(website, getIndicatorSlugWithPrefix("piwik-image"), output.hasPiwikImage());
        save(website, getIndicatorSlugWithPrefix("matomo-script"), output.hasMatomoScript());
        save(website, getIndicatorSlugWithPrefix("matomo-image"), output.hasMatomoImage());
        save(website, getIndicatorSlugWithPrefix("redcuba-script"), output.isHasRedCubaScript());
        save(website, getIndicatorSlugWithPrefix("google-tag-script"), output.hasGoogleTagScript());

        // Reporting the evaluation to the monitor
        sendEvaluation(website, output);

        // Enqueue indicator evaluation to process
        enqueueRedCubaScriptValue(website, output.isHasRedCubaScript() ? 1 : 0);
    }

    /**
     * Evaluate if the website applies for having an Analytic variable evaluation
     *
     * @param args Arguments needed by the worker. In this case is expected:
     *             <ul>
     *             <li>script Google Analytic</li>
     *             <li>script Piwik</li>
     *             <li>image Piwik</li>
     *             <li>script Matomo</li>
     *             <li>image Matomo</li>
     *             <li>script Google Tag</li>
     *             <li>script RedCuba</li>
     *             </ul>
     * @return {@link Boolean}
     */
    @Override
    boolean appliesForEvaluation(Object... args) {
        return true;
    }

    /**
     * Trigger website indicator evaluation to rc-con-notifier
     *
     * @param website    Website general data
     * @param evaluation The indicator evaluation
     */
    void enqueueRedCubaScriptValue(Website website, float evaluation) {
        RedCubaScriptValue redCubaScriptValue = new RedCubaScriptValue(new Date(), website.getId(), website.getHostname(), evaluation);
        directRabbitTemplate.send(redCubaScriptValueQueueName, new Message(GSON.toJson(redCubaScriptValue, RedCubaScriptValue.class).getBytes(), new MessageProperties()));
    }

}
