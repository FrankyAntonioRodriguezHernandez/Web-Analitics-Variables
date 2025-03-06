package cu.redcuba.worker.monitor;

import com.google.gson.JsonSyntaxException;
import com.rabbitmq.client.Channel;
import cu.redcuba.evaluations.reporter.TotalsReporter;
import cu.redcuba.helper.DateHelper;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class SendTotalsMonitorWorker extends AbstractMonitorWorker {

    private static final Logger LOG = Logger.getLogger(AbstractMonitorWorker.class.getName());

    private static final SimpleDateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private final TotalsReporter totalsReporter;

    @Autowired
    public SendTotalsMonitorWorker(TotalsReporter totalsReporter) {
        this.totalsReporter = totalsReporter;
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
            String strDate = new String(message.getBody());
            Date theDate = SHORT_DATE_FORMAT.parse(strDate);
            // Obtener la fecha de interés.
            theDate = DateHelper.getZeroTimeDate(theDate);
            // Obtener los datos y registrar la operación en el log.
            totalsReporter.sendTotalsOfDay(theDate);
        } catch (JsonSyntaxException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
        } finally {
            basicAck(message, channel);
        }
    }

}
