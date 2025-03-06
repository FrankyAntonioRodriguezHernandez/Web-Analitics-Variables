package cu.redcuba.worker;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.rabbitmq.client.Channel;
import cu.redcuba.entity.Download;
import cu.redcuba.factory.VariableFactory;
import cu.redcuba.factory.WebsiteJSFactory;
import cu.redcuba.helper.CastHelper;
import cu.redcuba.helper.MinifiedHelper;
import cu.redcuba.helper.UrlFetchHelper;
import cu.redcuba.message.WebsiteScriptMessage;
import cu.redcuba.model.JsItem;
import cu.redcuba.object.MinificationTestResult;
import cu.redcuba.object.Website;
import cu.redcuba.output.MinifiedJSOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Profile("consumer")
public class MinifiedJSWorker extends AbstractConsumerWorker<MinifiedJSOutput> {

    private static final int CORRECT_LIMIT = 100;

    private static final int TO_IMPROVE_LIMIT = 60;

    private static final MinifiedJSOutput NOT_EXIST_OUTPUT;

    static {
        NOT_EXIST_OUTPUT = new MinifiedJSOutput();
        NOT_EXIST_OUTPUT.setValue(VALUE_INCORRECT);
        NOT_EXIST_OUTPUT.setHas(false);
        NOT_EXIST_OUTPUT.setPercent(0);
        NOT_EXIST_OUTPUT.setJsItems(new ArrayList<>());
    }

    private static final Logger LOG = LoggerFactory.getLogger(MinifiedJSWorker.class);

    private static final Gson GSON = new Gson();

    private final UrlFetchHelper normalUrlFetchHelper;

    private final MinifiedHelper minifiedHelper;

    private final WebsiteJSFactory websiteJsFactory;

    @Autowired
    public MinifiedJSWorker(
            UrlFetchHelper normalUrlFetchHelper,
            MinifiedHelper minifiedHelper,
            WebsiteJSFactory websiteJsFactory
    ) {
        this.normalUrlFetchHelper = normalUrlFetchHelper;
        this.minifiedHelper = minifiedHelper;
        this.websiteJsFactory = websiteJsFactory;
    }

    /**
     * Returns the ID of the variable evaluated by this worker.
     *
     * @return The variable's ID.
     */
    @Override
    long getVariableId() {
        return VariableFactory.VAR_MINIFIED_JS;
    }

    /**
     * Analyses the values of a particular indicator.
     *
     * @param args Arguments needed by the worker.
     *             In this case is expected:
     *             <ul>
     *             <li>last-url</li>
     *             <li>scripts</li>
     *             </ul>
     * @return The analysis result.
     */
    @Override
    MinifiedJSOutput analyse(Object... args) {
        String lastUrl = CastHelper.cast(args[0], String.class);

        if (args[1] == null) {
            return NOT_EXIST_OUTPUT;
        }

        @SuppressWarnings("unchecked") List<Object[]> scripts = (List<Object[]>) args[1];

        if (scripts.isEmpty()) {
            return NOT_EXIST_OUTPUT;
        }

        MinifiedJSOutput output = new MinifiedJSOutput();
        output.setHas(true);

        float minifiedAmount = 0;

        List<JsItem> jsItems = new ArrayList<>();

        for (Object[] scriptObject : scripts) {
            String encoded = UrlFetchHelper.getUrlEncoded((String) scriptObject[0]);
            String script = UrlFetchHelper.getAbsolute(encoded, lastUrl);

            if (script != null) {
                Download scriptDownload = normalUrlFetchHelper.download(script, false);

                JsItem jsItem = new JsItem();
                jsItem.setUrl(script);

                if (scriptDownload != null
                        && validCode(scriptDownload.getCode())
                        && validContentType(scriptDownload.getContentType())
                        && validContent(scriptDownload.getContent())) {

                    MinificationTestResult minificationTestResult = minifiedHelper.minificationTest(scriptDownload.getContent());

                    jsItem.setLinesAmount(minificationTestResult.getLinesAmount());
                    jsItem.setLinesIdentCount(minificationTestResult.getLinesIdentCount());
                    jsItem.setLinesLengthMedian(minificationTestResult.getLinesLengthMedian());
                    jsItem.setMinified(minificationTestResult.getMinified());

                    minifiedAmount++;
                } else {
                    jsItem.setMinified(false);
                }

                jsItems.add(jsItem);
            }
        }

        output.setJsItems(jsItems);

        output.setPercent(minifiedAmount * 100f / scripts.size());

        if (output.getPercent() >= CORRECT_LIMIT) {
            output.setValue(VALUE_CORRECT);
        } else if (output.getPercent() >= TO_IMPROVE_LIMIT) {
            output.setValue(VALUE_TO_IMPROVE);
        } else {
            output.setValue(VALUE_INCORRECT);
        }

        return output;
    }

    /**
     * Analyses the values, inserts the results into the database and sends to Influx a report.
     *
     * @param website The {@link Website} instance with values related to the evaluated website.
     * @param args    Arguments needed by the worker.
     *                In this case is expected:
     *                <ul>
     *                <li>last-url</li>
     *                <li>scripts</li>
     *                </ul>
     */
    @Override
    void fullWork(Website website, Object... args) {
        MinifiedJSOutput output = analyse(args[0], args[1]);

        save(website, getIndicatorSlugWithPrefix(), output.getValue());
        save(website, getIndicatorSlugWithPrefix("has"), output.has());
        save(website, getIndicatorSlugWithPrefix("percent"), output.getPercent());

        // Reportar la evaluación al monitor.
        sendEvaluation(website, output);
        // Almacenar los datos adicionales.
        websiteJsFactory.createAndSave(website.getId(), output.getJsItems());
    }

    /**
     * Callback for processing a received Rabbit message.
     * <p>Implementors are supposed to process the given Message,
     * typically sending reply messages through the given Session.
     *
     * @param message the received AMQP message (never <code>null</code>)
     * @param channel the underlying Rabbit Channel (never <code>null</code>)
     */
    @Override
    public void onMessage(Message message, Channel channel) {
        try {
            // Getting the message.
            WebsiteScriptMessage websiteScriptMessage = GSON.fromJson(new String(message.getBody()), WebsiteScriptMessage.class);
            Website website = websiteScriptMessage.getWebsite();
            String lastUrl = websiteScriptMessage.getLastUrl();
            List<Object[]> scripts = websiteScriptMessage.getScripts();

            fullWork(website, lastUrl, scripts);
        } catch (JsonSyntaxException ex) {
            LOG.error(ex.getMessage());
        } finally {
            basicAck(message, channel);
        }
    }

    private boolean validCode(short code) {
        return code == 200;
    }

    private boolean validContent(String content) {
        return content != null;
    }

    private boolean validContentType(String contentType) {
        return contentType != null && (contentType.contains("gzip") || contentType.contains("deflate") || contentType.contains("javascript"));
    }

}
