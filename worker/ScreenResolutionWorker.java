package cu.redcuba.worker;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.css.ECSSVersion;
import com.helger.css.decl.*;
import com.helger.css.reader.CSSReader;
import com.rabbitmq.client.Channel;
import cu.redcuba.entity.Download;
import cu.redcuba.factory.VariableFactory;
import cu.redcuba.helper.CastHelper;
import cu.redcuba.helper.UrlFetchHelper;
import cu.redcuba.message.WebsiteStyleMessage;
import cu.redcuba.object.Website;
import cu.redcuba.output.ScreenResolutionOutput;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@Profile("consumer")
public class ScreenResolutionWorker extends AbstractConsumerWorker<ScreenResolutionOutput> {

    private static final ScreenResolutionOutput NOT_EXIST_OUTPUT;

    static {
        NOT_EXIST_OUTPUT = new ScreenResolutionOutput();
        NOT_EXIST_OUTPUT.setExistMobileResolution(false);
        NOT_EXIST_OUTPUT.setExistTabletResolution(false);
        NOT_EXIST_OUTPUT.setExistBigResolution(false);
        NOT_EXIST_OUTPUT.setValue(VALUE_INCORRECT);
    }

    private static final Logger LOG = Logger.getLogger(MinifiedCSSWorker.class.getName());

    private static final Gson GSON = new Gson();

    private final UrlFetchHelper normalUrlFetchHelper;

    @Autowired
    public ScreenResolutionWorker(UrlFetchHelper normalUrlFetchHelper) {
        this.normalUrlFetchHelper = normalUrlFetchHelper;
    }

    /**
     * Returns the ID of the variable evaluated by this worker.
     *
     * @return The variable's ID.
     */
    @Override
    long getVariableId() {
        return VariableFactory.VAR_SCREEN_RESOLUTION;
    }

    /**
     * Analyses the values of a particular indicator.
     *
     * @param args Arguments needed by the worker. In this case is expected:
     *             <ul>
     *             <li>last-url</li>
     *             <li>styles</li>
     *             </ul>
     * @return The analysis result.
     */
    ScreenResolutionOutput analyse(Object... args) {
        String lastUrl = CastHelper.cast(args[0], String.class);

        if (args[1] == null) {
            return NOT_EXIST_OUTPUT;
        }

        @SuppressWarnings("unchecked") List<Object[]> styles = (List<Object[]>) args[1];

        ScreenResolutionOutput output = new ScreenResolutionOutput();

        if (styles.isEmpty()) {
            return NOT_EXIST_OUTPUT;
        }

        for (Object[] styleObject : styles) {
            String encoded = UrlFetchHelper.getUrlEncoded((String) styleObject[0]);
            String style = UrlFetchHelper.getAbsolute(encoded, lastUrl);

            if (style != null) {
                Download styleDownload = normalUrlFetchHelper.download(style, false);
                if (styleDownload != null
                        && validCode(styleDownload.getCode())
                        && validContentType(styleDownload.getContentType())
                        && validContent(styleDownload.getContent())) {
                    int pos_arroba = styleDownload.getContent().indexOf("@media");
                    String substring_content = styleDownload.getContent();

                    final CascadingStyleSheet aCSS = CSSReader.readFromString(substring_content, StandardCharsets.UTF_8, ECSSVersion.CSS30);
                    ICommonsList<com.helger.css.decl.CSSMediaRule> allMediaRules = aCSS.getAllMediaRules();

                    Set<Integer> values = new HashSet<Integer>();
                    for (CSSMediaRule cssMediaRule : allMediaRules) {
                        ICommonsList<CSSMediaQuery> allMediaQueriesRule = cssMediaRule.getAllMediaQueries();
                        if (allMediaQueriesRule.size() > 0) {
                            for (CSSMediaQuery cssMediaQuery : allMediaQueriesRule) {
                                ICommonsList<CSSMediaExpression> allMediaExpressions = cssMediaQuery.getAllMediaExpressions();
                                if (allMediaExpressions.size() > 0) {
                                    for (CSSMediaExpression cssMediaExpression : allMediaExpressions) {
                                        ICommonsList<ICSSExpressionMember> allMembers = cssMediaExpression.getValue().getAllMembers();
                                        for (ICSSExpressionMember icssExpressionMember : allMembers) {
                                            values.add(Integer.parseInt(icssExpressionMember.getAsCSSString().replaceAll("px", "")));

                                        }

                                    }
                                }
                            }
                            //System.out.println(cssMediaRule.getAllMediaQueries().getFirst().getAllMediaExpressions().getFirst().getValue().getAllMembers().getFirst().getAsCSSString().replaceAll("px", ""));
                        }
                    }
                    Iterator iterator = values.iterator();
                    //AQUI VERIFICAR TUS RANGOS
                    while (iterator.hasNext()) {
                        Integer number_resolution = (Integer) iterator.next();
                        if ((number_resolution) > 320 && (number_resolution) < 736) {
                            output.setExistMobileResolution(true);
                        } else if (number_resolution > 736 && number_resolution < 1200) {
                            output.setExistTabletResolution(true);
                        } else {
                            output.setExistBigResolution(true);
                        }
                    }
                    //SI YA TIENES TODAS LAS MEDIAS UN BREACK
                }

            }
        }

        if (output.existMobileResolution() && output.existTabletResolution() && output.existBigResolution()) {
            output.setValue(VALUE_CORRECT);
        } else if (output.existMobileResolution() || output.existTabletResolution() || output.existBigResolution()) {
            output.setValue(VALUE_TO_IMPROVE);
        } else {
            output.setValue(VALUE_INCORRECT);
        }

        return output;
    }

    /**
     * Analyses the values, inserts the results into the database and sends to
     * Influx a report.
     *
     * @param website The {@link Website} instance with values related to the
     *                evaluated website.
     * @param args    Arguments needed by the worker. In this case is expected:
     *                <ul>
     *                <li>last-url</li>
     *                <li>styles</li>
     *                </ul>
     */
    @Override
    void fullWork(Website website, Object... args) {
        ScreenResolutionOutput output = analyse(args[0], args[1]);
        System.out.println(getIndicatorSlugWithPrefix());
        save(website, getIndicatorSlugWithPrefix(), output.getValue());
        save(website, getIndicatorSlugWithPrefix("mobile"), output.existMobileResolution());
        save(website, getIndicatorSlugWithPrefix("tablet"), output.existTabletResolution());
        save(website, getIndicatorSlugWithPrefix("big"), output.existBigResolution());

        // Reporting the evaluation to the monitor
        sendEvaluation(website, output);
    }

    /**
     * Callback for processing a received Rabbit message.
     * <p>
     * Implementors are supposed to process the given Message, typically sending
     * reply messages through the given Session.
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
            LOG.log(Level.SEVERE, ex.getMessage());
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
