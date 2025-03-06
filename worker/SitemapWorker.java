package cu.redcuba.worker;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.rabbitmq.client.Channel;
import cu.redcuba.entity.Visit;
import cu.redcuba.output.SitemapOutput;
import cu.redcuba.factory.VariableFactory;
import cu.redcuba.helper.CastHelper;
import cu.redcuba.helper.UrlFetchHelper;
import cu.redcuba.message.WebsiteSitemapMessage;
import cu.redcuba.object.Website;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@Profile("consumer")
public class SitemapWorker extends AbstractConsumerWorker<SitemapOutput> {

    private static final Logger LOG = Logger.getLogger(SitemapWorker.class.getName());

    private static final Gson GSON = new Gson();

    private final UrlFetchHelper normalUrlFetchHelper;

    @Autowired
    public SitemapWorker(UrlFetchHelper normalUrlFetchHelper) {
        this.normalUrlFetchHelper = normalUrlFetchHelper;
    }

    /**
     * Returns the ID of the variable evaluated by this worker.
     *
     * @return The variable's ID.
     */
    @Override
    long getVariableId() {
        return VariableFactory.VAR_SITEMAP;
    }

    /**
     * Analyses the values of a particular indicator.
     *
     * @param args Arguments needed by the worker.
     *             In this case is expected:
     *             <ul>
     *             <li>last-url</li>
     *             <li>sitemaps</li>
     *             </ul>
     * @return The analysis result.
     */
    @Override
    SitemapOutput analyse(Object... args) {
        String lastUrl = CastHelper.cast(args[0], String.class);
        @SuppressWarnings("unchecked") List<String> sitemaps = (List<String>) args[1];

        SitemapOutput output = new SitemapOutput();

        // We assume that anyone is valid
        output.setAtLeastOneValid(false);

        if (sitemaps != null) {
            for (String sitemap : sitemaps) {
                String encoded = UrlFetchHelper.getUrlEncoded(sitemap);
                String sitemapUrl = UrlFetchHelper.getAbsolute(encoded, lastUrl);

                Visit sitemapVisit = normalUrlFetchHelper.visit(sitemapUrl, false);
                if (sitemapVisit != null && validCode(sitemapVisit.getCode()) && validContentType(sitemapVisit.getContentType())) {
                    output.setAtLeastOneValid(true);
                    break;
                }
            }
        }

        if (output.isAtLeastOneValid()) {
            output.setValue(VALUE_CORRECT);
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
     *                <li>sitemaps</li>
     *                </ul>
     */
    @Override
    void fullWork(Website website, Object... args) {
        SitemapOutput output = analyse(args[0], args[1]);

        save(website, getIndicatorSlugWithPrefix(), output.getValue());
        save(website, getIndicatorSlugWithPrefix("one-valid"), output.isAtLeastOneValid());

        // Reporting the evaluation to the monitor
        sendEvaluation(website, output);
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
            // Getting the message
            WebsiteSitemapMessage websiteSitemapMessage = GSON.fromJson(new String(message.getBody()), WebsiteSitemapMessage.class);
            Website website = websiteSitemapMessage.getWebsite();
            String lastUrl = websiteSitemapMessage.getLastUrl();
            List<String> sitemaps = websiteSitemapMessage.getSitemaps();

            fullWork(website, lastUrl, sitemaps);
        } catch (JsonSyntaxException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
        } finally {
            basicAck(message, channel);
        }
    }

    private boolean validCode(short code) {
        return code == 200;
    }

    private boolean validContentType(String contentType) {
        return contentType != null && (contentType.contains("gzip") || contentType.contains("deflate") || contentType.contains("xml"));
    }

}
