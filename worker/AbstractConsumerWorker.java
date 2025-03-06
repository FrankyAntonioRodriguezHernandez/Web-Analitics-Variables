package cu.redcuba.worker;

import com.rabbitmq.client.Channel;
import cu.redcuba.output.Output;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;

abstract class AbstractConsumerWorker<O extends Output> extends AbstractWorker<O> implements ChannelAwareMessageListener {

    private static final Logger LOG = Logger.getLogger(AbstractConsumerWorker.class.getName());

    /**
     * Marks the message as processed.
     *
     * @param message The message itself.
     * @param channel The channel against RabbitMQ.
     */
    void basicAck(Message message, Channel channel) {
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
        }
    }

    /**
     * Evaluate if the website applies for having a variable evaluation
     *
     * @param args
     * @return {@link Boolean}
     */
    @Override
    boolean appliesForEvaluation(Object... args) {
        return true;
    }

}
