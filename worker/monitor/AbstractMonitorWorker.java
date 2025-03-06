package cu.redcuba.worker.monitor;

import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;

abstract class AbstractMonitorWorker implements ChannelAwareMessageListener {

    private static final Logger LOG = Logger.getLogger(AbstractMonitorWorker.class.getName());

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

}
