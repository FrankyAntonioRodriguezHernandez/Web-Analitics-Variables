package cu.redcuba.worker.notifier;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.rabbitmq.client.Channel;
import cu.redcuba.client.notifier.ConNotifierClient;
import cu.redcuba.client.notifier.model.RedCubaScriptValue;
import feign.RetryableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("consumer")
public class RedCubaScriptValueNotifierWorker extends AbstractNotifierWorker {

    private static final Logger LOG = LoggerFactory.getLogger(RedCubaScriptValueNotifierWorker.class);

    private static final Gson GSON = new Gson();

    private final ConNotifierClient conNotifierClient;

    public RedCubaScriptValueNotifierWorker(ConNotifierClient conNotifierClient) {
        this.conNotifierClient = conNotifierClient;
    }

    @Override
    public void onMessage(Message message, Channel channel) {
        try {
            // Obtener el contenido del mensaje.
            RedCubaScriptValue redCubaScriptValue = GSON.fromJson(new String(message.getBody()), RedCubaScriptValue.class);
            try {
                // Notificar que la evaluación fue realizada.
                conNotifierClient.analyzeIndicatorValueRedCubaScript(redCubaScriptValue);
                // Confirmar el procesamiento del mensaje.
                basicAck(message, channel);
            } catch (RetryableException ex) {
                // Rechazar el mensaje.
                basicReject(message, channel, false);
                // Registrar el error en el log.
                LOG.error("No está disponible rc-con-notifier para entregar la notificación");
            }
        } catch (JsonSyntaxException ex) {
            // Rechazar el mensaje desechándolo.
            basicReject(message, channel, false);
            // Registrar el error en el log.
            LOG.error(ex.getMessage());
        }
    }

}
