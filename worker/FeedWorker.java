package cu.redcuba.worker;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.rabbitmq.client.Channel;
import cu.redcuba.entity.Visit;
import cu.redcuba.output.FeedOutput;
import cu.redcuba.factory.VariableFactory;
import cu.redcuba.helper.CastHelper;
import cu.redcuba.helper.UrlFetchHelper;
import cu.redcuba.message.WebsiteFeedMessage;
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
public class FeedWorker extends AbstractConsumerWorker<FeedOutput> {

    private static final Logger LOG = Logger.getLogger(FeedWorker.class.getName());

    private static final Gson GSON = new Gson();

    private static final FeedOutput NOT_EXIST_OUTPUT;

    static {
        NOT_EXIST_OUTPUT = new FeedOutput();
        NOT_EXIST_OUTPUT.setValue(VALUE_TO_IMPROVE);
        NOT_EXIST_OUTPUT.setExist(false);
        NOT_EXIST_OUTPUT.setValid(false);
    }

    private final UrlFetchHelper normalUrlFetchHelper;

    @Autowired
    public FeedWorker(UrlFetchHelper normalUrlFetchHelper) {
        this.normalUrlFetchHelper = normalUrlFetchHelper;
    }

    /**
     * Returns the ID of the variable evaluated by this worker.
     *
     * @return The variable's ID.
     */
    @Override
    long getVariableId() {
        return VariableFactory.VAR_FEEDS;
    }

    /**
     * Analyses the values of a particular indicator.
     *
     * @param args Arguments needed by the worker.
     *             In this case is expected:
     *             <ul>
     *             <li>last-url</li>
     *             <li>feeds</li>
     *             </ul>
     * @return The analysis result.
     */
    @Override
    FeedOutput analyse(Object... args) {
        String lastUrl = CastHelper.cast(args[0], String.class);

        if (args[1] == null) {
            return NOT_EXIST_OUTPUT;
        }

        @SuppressWarnings("unchecked") List<Object[]> feeds = (List<Object[]>) args[1];

        FeedOutput feedOutput = new FeedOutput();

        if (feeds.isEmpty()) {
            return NOT_EXIST_OUTPUT;
        }

        feedOutput.setExist(true);

        // By default none feed is valid.
        feedOutput.setValid(false);

        for (Object[] feedObject : feeds) {
            String encoded = UrlFetchHelper.getUrlEncoded((String) feedObject[0]);
            String feed = UrlFetchHelper.getAbsolute(encoded, lastUrl);

            if (feed != null) {
                Visit feedVisit = normalUrlFetchHelper.visit(feed, false);

                // Has at least one feed which response and it has a valid status code
                if (feedVisit != null && validCode(feedVisit.getCode())) {
                    feedOutput.setValid(true);
                    break;
                }
            }
        }

        if (feedOutput.exist() && feedOutput.isValid()) {
            feedOutput.setValue(VALUE_CORRECT);
        } else {
            // As feeds are not mandatory, never is incorrect and by default we assume that it isn't ok.
            feedOutput.setValue(VALUE_TO_IMPROVE);
        }

        return feedOutput;
    }

    /**
     * Analyses the values, inserts the results into the database and sends to Influx a report.
     *
     * @param website The {@link Website} instance with values related to the evaluated website.
     * @param args    Arguments needed by the worker.
     *                In this case is expected:
     *                <ul>
     *                <li>last-url</li>
     *                <li>feeds</li>
     *                </ul>
     */
    @Override
    void fullWork(Website website, Object... args) {
        FeedOutput output = analyse(args[0], args[1]);

        save(website, getIndicatorSlugWithPrefix(), output.getValue());
        save(website, getIndicatorSlugWithPrefix("exist"), output.exist());
        save(website, getIndicatorSlugWithPrefix("valid"), output.isValid());

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
            WebsiteFeedMessage websiteFeedMessage = GSON.fromJson(new String(message.getBody()), WebsiteFeedMessage.class);
            Website website = websiteFeedMessage.getWebsite();
            List<Object[]> feeds = websiteFeedMessage.getLinksFeed();
            String lastUrl = websiteFeedMessage.getLastUrl();

            fullWork(
                    website,
                    lastUrl,
                    feeds
            );
        } catch (JsonSyntaxException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
        } finally {
            basicAck(message, channel);
        }
    }

    private boolean validCode(short code) {
        return code >= 200 && code < 400;
    }

}
