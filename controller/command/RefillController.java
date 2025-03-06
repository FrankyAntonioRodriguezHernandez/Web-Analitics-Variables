package cu.redcuba.controller.command;

import cu.redcuba.evaluations.reporter.EvaluationsReporter;
import cu.redcuba.evaluations.reporter.TotalsReporter;
import io.swagger.annotations.Api;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

@Profile("producer")
@Api(tags = "Rellenar Medición")
@RestController
@RequestMapping(path = "/command/refill")
public class RefillController {

    private static final Logger LOG = Logger.getLogger(RefillController.class.getName());

    private static final SimpleDateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private final TotalsReporter totalsReporter;

    private final EvaluationsReporter evaluationsReporter;

    private final RabbitTemplate rabbitTemplate;

    @Value("${evw.queue.monitor.send-totals}")
    private String monitorSendTotalsQueueName;

    @Autowired
    public RefillController(
            TotalsReporter totalsReporter,
            EvaluationsReporter evaluationsReporter,
            @Qualifier("directWorkTemplate") RabbitTemplate rabbitTemplate
    ) {
        this.totalsReporter = totalsReporter;
        this.evaluationsReporter = evaluationsReporter;
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping(path = "/" + EvaluationsReporter.MEAS_WEBSITE_EVALUATION, produces = "text/plain")
    public String evaluations() {
        try {
            //Refilling
            evaluationsReporter.refill();

            LOG.log(Level.INFO, "Measurement {0} Refilled.", EvaluationsReporter.MEAS_WEBSITE_EVALUATION);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }

        return String.format("Measurement %s Refilled.", EvaluationsReporter.MEAS_WEBSITE_EVALUATION);
    }

    @PostMapping(path = "/" + TotalsReporter.MEAS_TOTALS, produces = "text/plain")
    public String total() {
        try {
            // Refilling
            totalsReporter.refill();

            LOG.log(Level.INFO, "Measurement {0} Refilled.", TotalsReporter.MEAS_TOTALS);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }

        return String.format("Measurement %s Refilled.", TotalsReporter.MEAS_TOTALS);
    }

    @PostMapping(path = "/totals-for-day")
    public boolean totalsForDay(
            @RequestParam("day") String day
    ) {
        try {
            SHORT_DATE_FORMAT.setLenient(false);
            SHORT_DATE_FORMAT.parse(day);

            MessageProperties messageProperties = new MessageProperties();
            rabbitTemplate.send(monitorSendTotalsQueueName, new Message(day.getBytes(), messageProperties));
        } catch (ParseException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
            return false;
        }

        return true;
    }

}
