package cu.redcuba.worker;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.yahoo.platform.yui.compressor.CssCompressor;
import cu.redcuba.evaluations.reporter.EvaluationsReporter;
import cu.redcuba.output.MinifiedCSSOutput;
import cu.redcuba.factory.DailyEvaluationFactory;
import cu.redcuba.factory.VariableFactory;
import cu.redcuba.helper.MinifiedHelper;
import cu.redcuba.helper.UrlFetchHelper;
import cu.redcuba.object.Website;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class Style2Worker extends AbstractWorker<MinifiedCSSOutput> {

    private static final Logger LOG = Logger.getLogger(Style2Worker.class.getName());

    private static final Gson GSON = new Gson();

    @Value("${worker.style.minMinifiedPercent}")
    private Float minMinifiedPercent;

    @Value("${worker.style.turnOffMinificationMethod}")
    private boolean turnOffMinificationMethod;

    private final UrlFetchHelper normalUrlFetchHelper;

    private final MinifiedHelper minifiedHelper;

    private final DailyEvaluationFactory dailyEvaluationFactory;

    //private final EvaluationsReporter evaluationsReporter;

    @Autowired
    public Style2Worker(
            UrlFetchHelper normalUrlFetchHelper,
            MinifiedHelper minifiedHelper,
            DailyEvaluationFactory dailyEvaluationFactory,
            EvaluationsReporter evaluationsReporter) {
        this.normalUrlFetchHelper = normalUrlFetchHelper;
        this.minifiedHelper = minifiedHelper;
        this.dailyEvaluationFactory = dailyEvaluationFactory;
        this.evaluationsReporter = evaluationsReporter;
    }

    /**
     * Returns the ID of the variable evaluated by this worker.
     *
     * @return The variable's ID.
     */
    @Override
    long getVariableId() {
        return VariableFactory.VAR_MINIFIED_CSS2;
    }

    /**
     * Analyses the values of a particular indicator.
     *
     * @param args Arguments needed by the worker.
     * @return The analysis result.
     */
    @Override
    MinifiedCSSOutput analyse(Object... args) {
        return null;
    }

    /**
     * Analyses the values, inserts the results into the database and sends to Influx a report.
     *
     * @param website The {@link Website} instance with values related to the evaluated website.
     * @param args    Arguments needed by the worker.
     */
    @Override
    void fullWork(Website website, Object... args) {

    }

    /**
     * Callback for processing a received Rabbit message.
     * <p>Implementors are supposed to process the given Message,
     * typically sending reply messages through the given Session.
     *
     * @param message the received AMQP message (never <code>null</code>)
     * @param channel the underlying Rabbit Channel (never <code>null</code>)
     * @throws Exception Any.
     */
//    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
//        try {
//            // Obtener el objeto del mensaje.
//            WebsiteStyleMessage websiteStyleMessage = GSON.fromJson(new String(message.getBody()), WebsiteStyleMessage.class);
//            Website website = websiteStyleMessage.getWebsite();
//            String lastUrl = websiteStyleMessage.getLastUrl();
//            List<Object[]> styles = websiteStyleMessage.getStyles();
//
//
//            Date evDate = new Date();
//            float hasStyleMinified = 0, hasStyleMinified2 = 0;
//            int stylesCount = styles != null ? styles.size() : 0;
//            LOG.log(Level.FINER, "ws = {0}, host = {1}, styles = {2}", new Object[]{website.getId(), website.getHostname(), stylesCount});
//            if (stylesCount > 0) {
//                int voter = 0, voter2 = 0;
//                hasStyleMinified = 0.5f;
//                hasStyleMinified2 = 0.5f;
//                for (Object[] styleObject : styles) {
//                    String encoded = UrlFetchHelper.getUrlEncoded((String) styleObject[0]);
//                    String style = UrlFetchHelper.getAbsolute(encoded, lastUrl);
//                    LOG.log(Level.FINER, "ws = {0}, host = {1}, style = {2}", new Object[]{website.getId(), website.getHostname(), style});
//                    if (style != null) {
//                        Download styleDownload = normalUrlFetchHelper.download(style, false);
//                        if (styleDownload != null && validCode(styleDownload.getCode()) && validContentType(styleDownload.getContentType()) && validContent(styleDownload.getContent())) {
//                            // Primer método de comprobación de minificado.
//                            if (!minifiedHelper.hasRepeatedWhitespaces(styleDownload.getContent())) {
//                                voter++;
//                            }
//                            // Segundo método de comprobación de minificado.
//                            if (!turnOffMinificationMethod) {
//                                try {
//                                    String minifiedContent = minifyStyle(styleDownload.getContent());
//                                    // Si el texto minificado no es vacío.
//                                    if (minifiedContent != null) {
//                                        // Obtener el tamaño del contenido minificado.
//                                        long minifiedContentSize = minifiedContent.length();
//                                        // Calcular el porciento que representa el tamaño del contenido minificado respesto del original.
//                                        float minifiedPercent = (float) minifiedContentSize * 100 / styleDownload.getContentLength();
//                                        // Si el porciento es superior a X se cuenta como minificado.
//                                        if (minifiedPercent > minMinifiedPercent) {
//                                            voter2++;
//                                        }
//                                    }
//                                } catch (Exception ex) {
//                                    LOG.log(Level.SEVERE, "MINIFIED2: " + ex.getMessage());
//                                }
//                            }
//                        }
//                    }
//                }
//                LOG.log(Level.FINE, "ws = {0}, host = {1}, styles = {2}, voter = {3}", new Object[]{website.getId(), website.getHostname(), stylesCount, voter});
//                // Determinar si todos los CSS enlazados están minificados según el primer método.
//                if (stylesCount == voter) {
//                    hasStyleMinified = 1;
//                }
//                // Determinar si todos los CSS enlazados están minificados según el segundo método.
//                if (stylesCount == voter2) {
//                    hasStyleMinified2 = 1;
//                }
//            }
//            // Almacenar la evaluación de las variables.
//            dailyEvaluationFactory.createAndSave(website, "minified-css", "minified-css-value", evDate, hasStyleMinified);
//
//            //Reporting the evaluation to the monitor
//            evaluationsReporter.websitesEvaluationDay(
//                    website,
//                    "minified-css",
//                    hasStyleMinified);
//
//            dailyEvaluationFactory.createAndSave(website, "minified-css2", "minified-css2-value", evDate, hasStyleMinified2);
//
//            //Reporting the evaluation to the monitor
//            evaluationsReporter.websitesEvaluationDay(
//                    website,
//                    "minified-css2",
//                    hasStyleMinified2);
//        } catch (JsonSyntaxException ex) {
//            LOG.log(Level.SEVERE, ex.getMessage());
//        } finally {
//            basicAck(message, channel);
//        }
    }

    /**
     * Marcar el mensaje como procesado.
     *
     * @param message
     * @param channel
     */
    private void basicAck(Message message, Channel channel) {
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
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

    /**
     *
     * @param sourceCode Style source code to compile.
     * @return Compiled version of the code.
     */
    public String minifyStyle(String sourceCode) {
        Reader reader = null;
        Writer writer = null;
        String compressedCode = null;
        try {
            InputStream inputStream = new ByteArrayInputStream(sourceCode.getBytes(StandardCharsets.UTF_8));
            reader = new InputStreamReader(inputStream);
            CssCompressor compressor = new CssCompressor(reader);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            writer = new OutputStreamWriter(os);
            compressor.compress(writer, 0);
            compressedCode = os.toString();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getLocalizedMessage());
        } catch (StringIndexOutOfBoundsException ex) {
            LOG.log(Level.SEVERE, ex.getLocalizedMessage() + ": " + sourceCode, ex);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getClass() + ": " + ex.getLocalizedMessage(), ex);
        } finally {
//            IOUtils.closeQuietly(reader);
//            IOUtils.closeQuietly(writer);
//            IOUtils.close(reader);
//            IOUtils.close(writer);
        }
        return compressedCode;
    }

    @Override
    public void sendEvaluation(Website website, float evaluation, Long time) {
        // For this variable the reports are not needed
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
