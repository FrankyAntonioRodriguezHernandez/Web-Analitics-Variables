package cu.redcuba.scheduler;

import cu.redcuba.helper.DateHelper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@Profile({"producer", "command"})
public class MonitorScheduler {

    private static final Logger LOG = Logger.getLogger(MonitorScheduler.class.getName());

    private static final SimpleDateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private final RabbitTemplate rabbitTemplate;

    @Value("${evw.queue.monitor.send-totals}")
    private String monitorSendTotalsQueueName;

    @Autowired
    public MonitorScheduler(
            @Qualifier("directWorkTemplate") RabbitTemplate rabbitTemplate
    ) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Tarea Programada para reportar los valores de las variables de evaluación.
     */
    @Scheduled(cron = "${scheduler.variablesByDay}")
    public void variablesByDay() {
        try {
            // Obtener la fecha actual.
            Date currentDate = DateHelper.getZeroTimeDate(new Date());
            // Encolar la solicitud para procesar los totales.
            MessageProperties messageProperties = new MessageProperties();
            rabbitTemplate.send(monitorSendTotalsQueueName, new Message(SHORT_DATE_FORMAT.format(currentDate).getBytes(), messageProperties));
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

}
