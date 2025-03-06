package cu.redcuba.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import cu.redcuba.entity.Download;
import cu.redcuba.entity.RoundControl;
import cu.redcuba.factory.RoundControlFactory;
import cu.redcuba.factory.VariableFactory;
import cu.redcuba.helper.HtmlContentHelper;
import cu.redcuba.helper.LanguageDetectionHelper;
import cu.redcuba.helper.MinifiedHelper;
import cu.redcuba.helper.UrlFetchHelper;
import cu.redcuba.message.WebsiteFeedMessage;
import cu.redcuba.message.WebsiteScriptMessage;
import cu.redcuba.message.WebsiteStyleMessage;
import cu.redcuba.object.Website;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Profile("consumer")
public class HomePageWorker implements ChannelAwareMessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(HomePageWorker.class);

    private static final Gson GSON = new Gson();

    private static final String ROUND_HEADER = "round";

    @Value("${evw.queue.homepage}")
    private String homepageQueueName;

    @Value("${evw.queue.feed}")
    private String feedQueueName;

    @Value("${evw.queue.style}")
    private String styleQueueName;

    @Value("${evw.queue.script}")
    private String scriptQueueName;

    // @Value("${evw.queue.screen-resolution}")
    // private String resolutionQueueName;

    private final UrlFetchHelper normalUrlFetchHelper;

    private final MinifiedHelper minifiedHelper;

    private final RoundControlFactory roundControlFactory;

    private final RabbitTemplate directRabbitTemplate;
    private final RabbitTemplate massiveRabbitTemplate;

    @Autowired
    public HomePageWorker(
            UrlFetchHelper normalUrlFetchHelper,
            MinifiedHelper minifiedHelper,
            RoundControlFactory roundControlFactory,
            @Qualifier("directWorkTemplate") RabbitTemplate directRabbitTemplate,
            @Qualifier("massiveWorkTemplate") RabbitTemplate massiveRabbitTemplate
    ) {
        this.normalUrlFetchHelper = normalUrlFetchHelper;
        this.minifiedHelper = minifiedHelper;
        this.roundControlFactory = roundControlFactory;

        this.directRabbitTemplate = directRabbitTemplate;
        this.massiveRabbitTemplate = massiveRabbitTemplate;
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
    public void onMessage(Message message, Channel channel) throws IOException {
        // Getting the round header value
        MessageProperties messageProperties = message.getMessageProperties();
        String round = (String) messageProperties.getHeaders().get(ROUND_HEADER);

        // Getting the website instance
        Website website;
        try {
            website = new ObjectMapper().readValue(new String(message.getBody()), Website.class);
        } catch (IOException ex) {
            LOG.error(ex.getMessage());
            basicAck(message, channel);
            return;
        }

        // Downloading the homepage
        Download homePageDownload = downloadHomepage(website);
        if (homePageDownload != null) {

            // Variable: Status code
            VariableFactory.getWorker(VariableFactory.VAR_STATUS_CODE).fullWork(website, (int) homePageDownload.getCode());

            // Getting the homepage content
            String content = homePageDownload.getContent();

            // If everything is OK, we evaluate the content of the homepage
            if (content != null && !content.trim().isEmpty()) {
                // Variables: Compress, Hiding 404, Robots Txt, WWW Redirects, Availability, Response Time
                massiveRabbitTemplate.send(new Message(GSON.toJson(website).getBytes(), messageProperties));

                HtmlContentHelper htmlContentHelper = new HtmlContentHelper(content.trim());

                // Variables: HTML Lang, keywords, title, description
                VariableFactory.getWorker(VariableFactory.VAR_HTML_LANG).fullWork(website,
                        htmlContentHelper.getHtmlLang(),
                        LanguageDetectionHelper.getLanguage(content),
                        htmlContentHelper.getMetaKeywords(),
                        htmlContentHelper.getTitle(),
                        htmlContentHelper.getMetaDescription());

                // Variable: Html5
                VariableFactory.getWorker(VariableFactory.VAR_HTML5).fullWork(website,
                        htmlContentHelper.getFirstLine(),
                        htmlContentHelper.getHeaderAmount(),
                        htmlContentHelper.getFooterAmount(),
                        htmlContentHelper.getNavAmount(),
                        htmlContentHelper.getArticleAmount(),
                        htmlContentHelper.getSectionAmount());

                // Variable: Meta Charset
                VariableFactory.getWorker(VariableFactory.VAR_META_CHARSET).fullWork(website,
                        htmlContentHelper.getMetaCharset(),
                        htmlContentHelper.getMetaHttpEquiv());

                // Variable: Responsive
                VariableFactory.getWorker(VariableFactory.VAR_META_VIEW_PORT).fullWork(website,
                        htmlContentHelper.getMetaViewport());

                // Variable: URL Canonical
                VariableFactory.getWorker(VariableFactory.VAR_LINK_CANONICAL).fullWork(website,
                        htmlContentHelper.getLinkCanonical());
                // Variable: Twitter Card
                VariableFactory.getWorker(VariableFactory.VAR_TWITTER_CARD).fullWork(website,
                        htmlContentHelper.getMetasTwitter());

                // Variable: Open Graph
                VariableFactory.getWorker(VariableFactory.VAR_OPEN_GRAPH).fullWork(website,
                        htmlContentHelper.getMetasOg());

                // Variable: Plugins
                VariableFactory.getWorker(VariableFactory.VAR_PLUGIN).fullWork(website,
                        htmlContentHelper.getFlash(),
                        htmlContentHelper.getSilverlight(),
                        htmlContentHelper.getJava());

                // Variable: Analytics
                VariableFactory.getWorker(VariableFactory.VAR_ANALYTIC_TOOLS).fullWork(website,
                        htmlContentHelper.getScriptGoogleAnalytics(),
                        htmlContentHelper.getScriptPiwik(),
                        htmlContentHelper.getImagePiwik(),
                        htmlContentHelper.getScriptMatomo(),
                        htmlContentHelper.getImageMatomo(),
                        htmlContentHelper.getScriptRedCuba(),
                        htmlContentHelper.getGaTrackingIdScriptGoogleGtag());

                // Variable: Images Alt
                VariableFactory.getWorker(VariableFactory.VAR_IMAGES_ALT).fullWork(website,
                        htmlContentHelper.getImages());

                // Variable: Homepage's weight
                VariableFactory.getWorker(VariableFactory.VAR_WEIGHT).fullWork(website,
                        homePageDownload.getContentLength().floatValue());

                // Variable: Secure Protocol
                VariableFactory.getWorker(VariableFactory.VAR_SECURE_PROTOCOL).fullWork(website,
                        homePageDownload.getLastUrl(),
                        homePageDownload.getHostname());

                // Variable: X-Frame-Options
                VariableFactory.getWorker(VariableFactory.VAR_X_FRAME_OPTIONS).fullWork(website,
                        homePageDownload.getXFrameOptions());

                // Variable: X-XSS-Protection
                VariableFactory.getWorker(VariableFactory.VAR_X_XSS_PROTECTION).fullWork(website,
                        homePageDownload.getxXssProtection());

                // Variable: X-Content-Type-Options
                VariableFactory.getWorker(VariableFactory.VAR_X_CONTENT_TYPE_OPTIONS).fullWork(website,
                        homePageDownload.getXContentTypeOptions());

                // Variable: EmailPrivacy
                VariableFactory.getWorker(VariableFactory.VAR_EMAIL_PRIVACY).fullWork(website,
                        htmlContentHelper.getPublicEmails());

                // Variable: Favicon
                VariableFactory.getWorker(VariableFactory.VAR_FAVICON).fullWork(website,
                        htmlContentHelper.getLinkIcon(),
                        htmlContentHelper.getLinkShortcutIcon(),
                        homePageDownload.getUrl());

                // Variable: Cache
                VariableFactory.getWorker(VariableFactory.VAR_CACHE_HEADER).fullWork(website,
                        homePageDownload.getCacheControl());

                // Variable: Minified HTML
                VariableFactory.getWorker(VariableFactory.VAR_MINIFIED_HTML).fullWork(website,
                        minifiedHelper.hasRepeatedWhitespaces(content));

                // Variable: Enlaces Rotos
                VariableFactory.getWorker(VariableFactory.VAR_BROKEN_LINK).fullWork(website,
                        content);
                
             // Variable: Resource Load
                VariableFactory.getWorker(VariableFactory.VAR_RESOURCE_LOAD).fullWork(website,
                        content);

                //Variable: Subresource Integrity
                VariableFactory.getWorker(VariableFactory.VAR_SUBRESOURCE_INTEGRITY).fullWork(website,
                        content);

                // Variable: StrictTransportSecutiry
                VariableFactory.getWorker(VariableFactory.VAR_STRICT_TRANSPORT_SECURITY).fullWork(website,
                        homePageDownload.getUrl());

                // Variable: Seguridad Cookies
                VariableFactory.getWorker(VariableFactory.VAR_SEGURIDAD_COOKIES).fullWork(website,
                        homePageDownload.getUrl());

                // Variable: Content Security Policy
                VariableFactory.getWorker(VariableFactory.VAR_CONTENT_SECURITY_POLICY).fullWork(website,
                        homePageDownload.getUrl());
                // Variable: Referrer Policy
                VariableFactory.getWorker(VariableFactory.VAR_REFERRER_POLICY).fullWork(website,
                        homePageDownload.getUrl());


                // Variable: Meta Feed
                List<Object[]> linksFeed = htmlContentHelper.getLinkFeed();
                WebsiteFeedMessage websiteFeedMessage = new WebsiteFeedMessage(website, homePageDownload.getLastUrl(), linksFeed);
                directRabbitTemplate.send(feedQueueName, new Message(GSON.toJson(websiteFeedMessage).getBytes(), messageProperties));

                // Variable: Minified CSS
                List<Object[]> styles = new ArrayList<>();
                styles.addAll(htmlContentHelper.getStyleImportCss());
                styles.addAll(htmlContentHelper.getLinkCss());
                WebsiteStyleMessage websiteStyleMessage = new WebsiteStyleMessage(website, homePageDownload.getLastUrl(), styles);
                directRabbitTemplate.send(styleQueueName, new Message(GSON.toJson(websiteStyleMessage).getBytes(), messageProperties));

                // Variable: Screen Resolution
                // directRabbitTemplate.send(resolutionQueueName, new Message(GSON.toJson(websiteStyleMessage).getBytes(), messageProperties));

                // Variable: Minified JS
                List<Object[]> scripts = htmlContentHelper.getScriptJs();
                WebsiteScriptMessage websiteScriptMessage = new WebsiteScriptMessage(website, homePageDownload.getLastUrl(), scripts);
                directRabbitTemplate.send(scriptQueueName, new Message(GSON.toJson(websiteScriptMessage).getBytes(), messageProperties));
            } else {
                // Reporting the failures
                VariableFactory.getWorker(VariableFactory.VAR_COMPRESSION).sendFailedEvaluation(website, null);
                VariableFactory.getWorker(VariableFactory.VAR_HIDING_404).sendFailedEvaluation(website, null);
                VariableFactory.getWorker(VariableFactory.VAR_ROBOTS).sendFailedEvaluation(website, null);
                VariableFactory.getWorker(VariableFactory.VAR_WWW).sendFailedEvaluation(website, null);
                VariableFactory.getWorker(VariableFactory.VAR_AVAILABILITY).sendFailedEvaluation(website, null);
                VariableFactory.getWorker(VariableFactory.VAR_RESPONSE_TIME).sendFailedEvaluation(website, null);

                VariableFactory.getWorker(VariableFactory.VAR_HTML_LANG).sendFailedEvaluation(website, null);
                VariableFactory.getWorker(VariableFactory.VAR_META_CHARSET).sendFailedEvaluation(website, null);
                VariableFactory.getWorker(VariableFactory.VAR_KEYWORDS).sendFailedEvaluation(website, null);
                VariableFactory.getWorker(VariableFactory.VAR_LINK_CANONICAL).sendFailedEvaluation(website, null);
                VariableFactory.getWorker(VariableFactory.VAR_HTML5).sendFailedEvaluation(website, null);
                VariableFactory.getWorker(VariableFactory.VAR_CACHE_HEADER).sendFailedEvaluation(website, null);
                VariableFactory.getWorker(VariableFactory.VAR_TWITTER_CARD).sendFailedEvaluation(website, null);
                VariableFactory.getWorker(VariableFactory.VAR_OPEN_GRAPH).sendFailedEvaluation(website, null);
                VariableFactory.getWorker(VariableFactory.VAR_ANALYTIC_TOOLS).sendFailedEvaluation(website, null);
                VariableFactory.getWorker(VariableFactory.VAR_IMAGES_ALT).sendFailedEvaluation(website, null);
                VariableFactory.getWorker(VariableFactory.VAR_WEIGHT).sendFailedEvaluation(website, null);
                VariableFactory.getWorker(VariableFactory.VAR_MINIFIED_HTML).sendFailedEvaluation(website, null);
                VariableFactory.getWorker(VariableFactory.VAR_SECURE_PROTOCOL).sendFailedEvaluation(website, null);

                VariableFactory.getWorker(VariableFactory.VAR_FEEDS).sendFailedEvaluation(website, null);
                VariableFactory.getWorker(VariableFactory.VAR_MINIFIED_CSS).sendFailedEvaluation(website, null);
                VariableFactory.getWorker(VariableFactory.VAR_MINIFIED_JS).sendFailedEvaluation(website, null);
                VariableFactory.getWorker(VariableFactory.VAR_PLUGIN).sendFailedEvaluation(website, null);
                VariableFactory.getWorker(VariableFactory.VAR_EMAIL_PRIVACY).sendFailedEvaluation(website, null);

                // VariableFactory.getWorker(VariableFactory.VAR_SCREEN_RESOLUTION).sendFailedEvaluation(website, null);
            }
        }

        basicAck(message, channel);

        // Ejecutar el control de ronda.
        if (round != null && !round.isEmpty()) {
            RoundControl roundControl = roundControlFactory.increment(round, homepageQueueName);
            /*
            if (roundControl.getItemsProcessed() == roundControl.getItemsSize()) {
                // TODO: Cuando sea necesario enviar mensaje a cola aparte para procesar la ronda terminada.
            }
            */
        }
    }

    /**
     * Marks the message as processed.
     *
     * @param message The message itself.
     * @param channel The channel against RabbitMQ.
     */
    private void basicAck(Message message, Channel channel) {
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException ex) {
            LOG.error(ex.getMessage());
        }
    }

    /**
     * Download the homepage of a given website.
     *
     * @param website The given website.
     * @return A wrapper with the values related to the download.
     */
    private Download downloadHomepage(Website website) {
        return normalUrlFetchHelper.download("http://" + website.getHostname(), true);
    }

}
