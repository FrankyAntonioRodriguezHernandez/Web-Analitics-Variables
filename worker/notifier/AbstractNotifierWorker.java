package cu.redcuba.worker.notifier;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract class AbstractNotifierWorker implements ChannelAwareMessageListener {

    private static final Logger LOG = Logger.getLogger(AbstractNotifierWorker.class.getName());

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
     * Rejects and requeue the message if specified.
     *
     * @param message The message itself.
     * @param channel The channel against RabbitMQ.
     * @param requeue Requeue or not the message.
     */
    void basicReject(Message message, Channel channel, boolean requeue) {
        try {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), requeue);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
        }
    }

}
