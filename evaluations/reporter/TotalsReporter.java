package cu.redcuba.evaluations.reporter;

import cu.redcuba.influxdb.InfluxDbClient;
import cu.redcuba.repository.EvaluationDailyRepository;

import java.math.BigInteger;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TotalsReporter implements Reporter {

    private static final Logger LOG = Logger.getLogger(TotalsReporter.class.getName());

    public static final String MEAS_TOTALS = "evw_variables_total";

    private static final String SENT_MESSAGE_FORMAT = "Sent values for the day {0}.";

    private final EvaluationDailyRepository evaluationDailyRepository;

    private final InfluxDbClient influxDbClient;

    @Autowired
    public TotalsReporter(
            EvaluationDailyRepository evaluationDailyRepository,
            InfluxDbClient influxDbClient) {
        this.evaluationDailyRepository = evaluationDailyRepository;
        this.influxDbClient = influxDbClient;
    }

    /**
     * Sends reports to InfluxDB about the totals of the evaluations of a given day.
     * This stands only for National Websites
     *
     * @param day The given day.
     */
    public void sendTotalsOfDay(final Date day) {
        // Getting the short value of the date.
        final String shortDate = SHORT_DATE_FORMAT.format(day);

        try {
            // Getting the totals for each variable of National Websites
            List<Object[]> totals = evaluationDailyRepository.findNationalTotalsForDay(shortDate);

            // Adding the points to InfluxDB
            totals.forEach(row -> {
                // Fields
                Map<String, Object> fields = new HashMap<>();
                fields.put("websites_ok", ((BigInteger) row[1]).intValue());
                fields.put("websites_to_improve", ((BigInteger) row[2]).intValue());
                fields.put("websites_bad", ((BigInteger) row[3]).intValue());

                // Tags
                Map<String, String> tags = new HashMap<>();
                tags.put("variable", (String) row[0]);

                // Adding the point
                influxDbClient.addPoint(MEAS_TOTALS, day.getTime(), fields, tags);
            });

            LOG.log(Level.INFO, SENT_MESSAGE_FORMAT, shortDate);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    /**
     * Refills the measurement associated to this reporter.
     * This stands only for National Websites
     *
     * @param args Arguments needed by the reporter.
     * @throws Exception Thrown during the refilling process.
     */
    @Override
    public void refill(Object... args) throws Exception {
        // Deleting the measurement before work on this
        influxDbClient.dropMeasurement(TotalsReporter.MEAS_TOTALS);

        // Getting the totals for each day and variable of National WS
        List<Object[]> totals = evaluationDailyRepository.findNationalTotals();

        Date day = null;

        // Adding the points to InfluxDB
        for (Object[] row : totals) {

            // Waiting 10 seconds when the day change to avoid InfluxDB blows up
            if (day != null && !day.equals(row[0])) {
                LOG.log(Level.INFO, SENT_MESSAGE_FORMAT, day);
                Thread.sleep(5000);
            }

            day = (Date) row[0];

            // Fields
            Map<String, Object> fields = new HashMap<>();
            fields.put("websites_ok", ((BigInteger) row[2]).intValue());
            fields.put("websites_to_improve", ((BigInteger) row[3]).intValue());
            fields.put("websites_bad", ((BigInteger) row[4]).intValue());

            // Tags
            Map<String, String> tags = new HashMap<>();
            tags.put("variable", (String) row[1]);

            // We have to add 6 hours at least to avoid the reports will be displayed on previous day
            Calendar cal = Calendar.getInstance();
            cal.setTime(day);
            cal.add(Calendar.HOUR, 6);

            // Adding the point
            influxDbClient.addPoint(MEAS_TOTALS, cal.getTime().getTime(), fields, tags);
        }

        // The last day
        LOG.log(Level.INFO, SENT_MESSAGE_FORMAT, day);
    }

}
