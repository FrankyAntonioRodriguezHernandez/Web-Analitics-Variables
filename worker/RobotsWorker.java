package cu.redcuba.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.rabbitmq.client.Channel;
import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.SimpleRobotRulesParser;
import cu.redcuba.entity.Download;
import cu.redcuba.output.RobotsOutput;
import cu.redcuba.factory.VariableFactory;
import cu.redcuba.helper.CastHelper;
import cu.redcuba.helper.UrlFetchHelper;
import cu.redcuba.message.WebsiteSitemapMessage;
import cu.redcuba.object.Website;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@Profile("consumer")
public class RobotsWorker extends AbstractConsumerWorker<RobotsOutput> {

    private static final Logger LOG = Logger.getLogger(RobotsWorker.class.getName());

    private static final Gson GSON = new Gson();

    private static final String DEFAULT_SITEMAP_URL_FORMAT = "http://%s/sitemap.xml";

    private final String sitemapQueueName;

    private final UrlFetchHelper normalUrlFetchHelper;

    private final RabbitTemplate rabbitTemplate;

    private final String userAgent;

    @Autowired
    RobotsWorker(
            @Value("${evw.queue.sitemap}") String sitemapQueueName,
            UrlFetchHelper normalUrlFetchHelper,
            @Qualifier("directWorkTemplate") RabbitTemplate rabbitTemplate,
            @Value("${evw.robot.userAgent}") String userAgent) {
        this.sitemapQueueName = sitemapQueueName;

        this.normalUrlFetchHelper = normalUrlFetchHelper;
        this.rabbitTemplate = rabbitTemplate;
        this.userAgent = userAgent;
    }

    /**
     * Returns the ID of the variable evaluated by this worker.
     *
     * @return The variable's ID.
     */
    @Override
    long getVariableId() {
        return VariableFactory.VAR_ROBOTS;
    }

    /**
     * Analyses the values of a particular indicator.
     *
     * @param args Arguments needed by the worker.
     *             In this case is expected:
     *             <ul>
     *             <li>url</li>
     *             <li>content</li>
     *             <li>content-type</li>
     *             <li>code</li>
     *             </ul>
     * @return The analysis result.
     */
    @Override
    public RobotsOutput analyse(Object... args) {
        final String url = CastHelper.cast(args[0], String.class);
        final String content = CastHelper.cast(args[1], String.class);
        final String contentType = CastHelper.cast(args[2], String.class);
        final short code = CastHelper.cast(args[3], Short.class);

        RobotsOutput output = new RobotsOutput();

        if (content == null || !validCode(code)) {
            output.setExist(false);
            output.setPlainText(false);
            output.setValue(VALUE_INCORRECT);
            return output;
        }

        output.setExist(true);

        // Checking the content type
        if (validContentType(contentType)) {
            output.setPlainText(true);

            // Parsing rules
            SimpleRobotRulesParser robotParser = new SimpleRobotRulesParser();
            BaseRobotRules baseRobotRules = robotParser.parseContent(
                    url,
                    content.getBytes(),
                    contentType,
                    userAgent);

            // Getting sitemaps.
            output.setSitemaps(baseRobotRules.getSitemaps());
        } else {
            output.setPlainText(false);
        }

        if (output.exist() && output.isPlainText()) {
            output.setValue(VALUE_CORRECT);
        } else if(output.exist() || output.isPlainText()) {
            output.setValue(VALUE_TO_IMPROVE);
        } else {
            output.setValue(VALUE_INCORRECT);
        }

        return output;
    }

    private boolean validCode(short code) {
        return code == 200;
    }

    private boolean validContentType(String contentType) {
        return contentType != null && contentType.contains("text/plain");
    }

    /**
     * Analyses the language value, inserts the result into the database and sends to Influx a report.
     *
     * @param website The {@link Website} instance with values related to the evaluated website.
     * @param args    Arguments needed by the worker.
     *                In this case is expected:
     *                <ul>
     *                <li>url</li>
     *                <li>content</li>
     *                <li>content-type</li>
     *                <li>code</li>
     *                <li>lastURL</li>
     *                </ul>
     */
    @Override
    public void fullWork(Website website, Object... args) {
        RobotsOutput output = analyse(args[0], args[1], args[2], args[3]);

        save(website, getIndicatorSlugWithPrefix(), output.getValue());
        save(website, getIndicatorSlugWithPrefix("exist"), output.exist());
        save(website, getIndicatorSlugWithPrefix("plain-text"), output.isPlainText());

        //Reporting the evaluation to the monitor
        sendEvaluation(website, output);

        // Processing the sitemaps
        processSitemaps(website, output.getSitemaps(), CastHelper.cast(args[4], String.class));
    }

    /**
     * Sends to process the list of sitemaps found on robots.txt file.
     *
     * @param website  The {@link Website} instance with values related to the evaluated website.
     * @param sitemaps The sitemap list.
     */
    private void processSitemaps(Website website, List<String> sitemaps, String lastURL) {
        // Encolar el sitio para el procesamiento de los sitemaps encontrados.
        // Variable: Sitemap
        // Garantizar que el listado de sitemaps no sea nulo.
        if (sitemaps == null) {
            sitemaps = new ArrayList<>();
        }
        // Garantizar que el listado de sitemaps no esté vacío.
        if (sitemaps.isEmpty()) {
            sitemaps.add(String.format(DEFAULT_SITEMAP_URL_FORMAT, website.getHostname()));
        }

        // Encolar el sitio web y sus sitemaps para el procesamiento.
        WebsiteSitemapMessage websiteSitemapMessage = new WebsiteSitemapMessage(
                website, lastURL, sitemaps);
        rabbitTemplate.send(sitemapQueueName, new Message(GSON.toJson(websiteSitemapMessage).getBytes(), new MessageProperties()));
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
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        try {
            Website website = new ObjectMapper().readValue(message.getBody(), Website.class);
            String robotsUrl = String.format("http://%s/robots.txt", website.getHostname());
            Download robotsDownload = normalUrlFetchHelper.download(robotsUrl, false);

            fullWork(
                    website,
                    robotsUrl,
                    robotsDownload.getContent(),
                    robotsDownload.getContentType(),
                    robotsDownload.getCode(),
                    robotsDownload.getLastUrl());
        } catch (JsonSyntaxException | IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
        } finally {
            basicAck(message, channel);
        }
    }

}
