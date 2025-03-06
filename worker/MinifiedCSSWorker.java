package cu.redcuba.worker;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.rabbitmq.client.Channel;
import cu.redcuba.entity.Download;
import cu.redcuba.factory.VariableFactory;
import cu.redcuba.factory.WebsiteCSSFactory;
import cu.redcuba.helper.CastHelper;
import cu.redcuba.helper.MinifiedHelper;
import cu.redcuba.helper.UrlFetchHelper;
import cu.redcuba.message.WebsiteStyleMessage;
import cu.redcuba.model.CssItem;
import cu.redcuba.object.MinificationTestResult;
import cu.redcuba.object.Website;
import cu.redcuba.output.MinifiedCSSOutput;
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
public class MinifiedCSSWorker extends AbstractConsumerWorker<MinifiedCSSOutput> {

    private static final int CORRECT_LIMIT = 100;

    private static final int TO_IMPROVE_LIMIT = 60;

    private static final MinifiedCSSOutput NOT_EXIST_OUTPUT;

    static {
        NOT_EXIST_OUTPUT = new MinifiedCSSOutput();
        NOT_EXIST_OUTPUT.setValue(VALUE_INCORRECT);
        NOT_EXIST_OUTPUT.setHas(false);
        NOT_EXIST_OUTPUT.setPercent(0);
        NOT_EXIST_OUTPUT.setCssItems(new ArrayList<>());
    }

    private static final Logger LOG = LoggerFactory.getLogger(MinifiedCSSWorker.class);

    private static final Gson GSON = new Gson();

    private final UrlFetchHelper normalUrlFetchHelper;

    private final MinifiedHelper minifiedHelper;

    private final WebsiteCSSFactory websiteCssFactory;

    @Autowired
    public MinifiedCSSWorker(
            UrlFetchHelper normalUrlFetchHelper,
            MinifiedHelper minifiedHelper,
            WebsiteCSSFactory websiteCssFactory
    ) {
        this.normalUrlFetchHelper = normalUrlFetchHelper;
        this.minifiedHelper = minifiedHelper;
        this.websiteCssFactory = websiteCssFactory;
    }

    /**
     * Returns the ID of the variable evaluated by this worker.
     *
     * @return The variable's ID.
     */
    @Override
    long getVariableId() {
        return VariableFactory.VAR_MINIFIED_CSS;
    }

    /**
     * Analyses the values of a particular indicator.
     *
     * @param args Arguments needed by the worker.
     *             In this case is expected:
     *             <ul>
     *             <li>last-url</li>
     *             <li>styles</li>
     *             </ul>
     * @return The analysis result.
     */
    @Override
    MinifiedCSSOutput analyse(Object... args) {
        String lastUrl = CastHelper.cast(args[0], String.class);

        if (args[1] == null) {
            return NOT_EXIST_OUTPUT;
        }

        @SuppressWarnings("unchecked") List<Object[]> styles = (List<Object[]>) args[1];

        if (styles.isEmpty()) {
            return NOT_EXIST_OUTPUT;
        }

        MinifiedCSSOutput output = new MinifiedCSSOutput();
        output.setHas(true);

        float minifiedAmount = 0;

        List<CssItem> cssItems = new ArrayList<>();

        for (Object[] styleObject : styles) {
            String encoded = UrlFetchHelper.getUrlEncoded((String) styleObject[0]);
            String style = UrlFetchHelper.getAbsolute(encoded, lastUrl);

            if (style != null) {
                Download styleDownload = normalUrlFetchHelper.download(style, false);

                CssItem cssItem = new CssItem();
                cssItem.setUrl(style);

                if (styleDownload != null
                        && validCode(styleDownload.getCode())
                        && validContentType(styleDownload.getContentType())
                        && validContent(styleDownload.getContent())) {

                    MinificationTestResult minificationTestResult = minifiedHelper.minificationTest(styleDownload.getContent());

                    cssItem.setLinesAmount(minificationTestResult.getLinesAmount());
                    cssItem.setLinesIdentCount(minificationTestResult.getLinesIdentCount());
                    cssItem.setLinesLengthMedian(minificationTestResult.getLinesLengthMedian());
                    cssItem.setMinified(minificationTestResult.getMinified());

                    minifiedAmount++;
                } else {
                    cssItem.setMinified(false);
                }

                cssItems.add(cssItem);
            }
        }

        output.setCssItems(cssItems);

        output.setPercent(minifiedAmount * 100f / styles.size());

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
     *                <li>styles</li>
     *                </ul>
     */
    @Override
    void fullWork(Website website, Object... args) {
        MinifiedCSSOutput output = analyse(args[0], args[1]);

        save(website, getIndicatorSlugWithPrefix(), output.getValue());
        save(website, getIndicatorSlugWithPrefix("has"), output.has());
        save(website, getIndicatorSlugWithPrefix("percent"), output.getPercent());

        // Reportar la evaluación al monitor.
        sendEvaluation(website, output);
        // Almacenar los datos adicionales.
        websiteCssFactory.createAndSave(website.getId(), output.getCssItems());
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
            // Obtener el objeto del mensaje.
            WebsiteStyleMessage websiteStyleMessage = GSON.fromJson(new String(message.getBody()), WebsiteStyleMessage.class);
            Website website = websiteStyleMessage.getWebsite();
            String lastUrl = websiteStyleMessage.getLastUrl();
            List<Object[]> styles = websiteStyleMessage.getStyles();

            fullWork(website, lastUrl, styles);
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
        return contentType != null && (contentType.contains("gzip") || contentType.contains("deflate") || contentType.contains("css"));
    }

}
