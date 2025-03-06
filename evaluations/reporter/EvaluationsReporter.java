package cu.redcuba.evaluations.reporter;

import cu.redcuba.client.WebsitesDirectoryClient;
import cu.redcuba.entity.Variable;
import cu.redcuba.factory.VariableFactory;
import cu.redcuba.helper.DateHelper;
import cu.redcuba.influxdb.InfluxDbClient;
import cu.redcuba.object.Website;
import cu.redcuba.repository.EvaluationDailyRepository;
import org.influxdb.InfluxDBException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@Profile({"consumer", "command", "producer"})
public class EvaluationsReporter implements Reporter {

    private static final Logger LOG = Logger.getLogger(EvaluationsReporter.class.getName());

    public static final String MEAS_WEBSITE_EVALUATION = "evw_variables_evaluations";

    /**
     * Max Influx's exceptions allowed. This is used when Influx blow up.
     */
    private static final int MAX_EXCEPTIONS_ALLOWED = 3;

    private final InfluxDbClient influxDbClient;

    private final WebsitesDirectoryClient websitesDirectoryClient;

    private final EvaluationDailyRepository evaluationDailyRepository;

    @Autowired
    public EvaluationsReporter(
            InfluxDbClient influxDbClient,
            WebsitesDirectoryClient websitesDirectoryClient,
            EvaluationDailyRepository evaluationDailyRepository) {
        this.influxDbClient = influxDbClient;
        this.websitesDirectoryClient = websitesDirectoryClient;
        this.evaluationDailyRepository = evaluationDailyRepository;
    }

    /**
     * Sends a particular evaluation of some variable to the monitor.
     *
     * @param hostname The hostname of the evaluated website.
     * @param variable The evaluated variable.
     * @param evaluation The final evaluation.
     * @param time The time of the report.
     */
    public void websitesEvaluationDay(String hostname, Variable variable, float evaluation, String readableEvaluation, Long time) {
        // The fields
        Map<String, Object> fields = new HashMap<>();

        // Putting the evaluation
        fields.put("evaluation", evaluation);
        fields.put("evaluation_readable", readableEvaluation);

        // The tags
        Map<String, String> tags = new HashMap<>();
        tags.put("hostname", hostname);
        tags.put("variable", variable.getName());

        // Adding the point
        influxDbClient.addPoint(MEAS_WEBSITE_EVALUATION, time, fields, tags);
    }

    /**
     * Sends a report without evaluation. Used on failure cases.
     *
     * @param hostname The hostname of the evaluated website.
     * @param variable The evaluated variable.
     * @param time The time of the report.
     */
    public void websitesEvaluationDay(String hostname, Variable variable, String readableEvaluation, Long time) {
        // The fields
        Map<String, Object> fields = new HashMap<>();
        fields.put("evaluation_readable", readableEvaluation);

        // The tags
        Map<String, String> tags = new HashMap<>();
        tags.put("hostname", hostname);
        tags.put("variable", variable.getName());

        // Adding the point
        influxDbClient.addPoint(MEAS_WEBSITE_EVALUATION, time, fields, tags);
    }

    /**
     * Refills the measurement associated to this reporter.
     *
     * @param args Arguments needed by the reporter.
     *
     * @throws Exception Thrown during the refilling process.
     */
    @Override
    public void refill(Object... args) throws Exception {
        // ERROR DE MEMORIA: + de 2 millones de registros innecesariamente. Regenerar solo los 2 últimos meses. Generar los valores de atrás hacia adelante.
        LOG.log(Level.INFO, "Medición {0} regenerando", MEAS_WEBSITE_EVALUATION);

        influxDbClient.dropMeasurement(MEAS_WEBSITE_EVALUATION);

        HashMap<Long, Website> websitesMap = new HashMap<>();
        for (Website website : websitesDirectoryClient.getWebsitesEnabledCategorized()) {
            websitesMap.put(website.getId(), website);
        }

        Date maxDay = evaluationDailyRepository.findMaxDay();

        Date minDay = DateHelper.moveDate(maxDay, -60);

        Date cursorDate = DateHelper.getZeroTimeDate(maxDay);

        // From max to min
        while (cursorDate.after(minDay)) {
            LOG.log(Level.INFO, "DAY {0}", cursorDate);

            // Getting the evaluations of the day
            List<Object[]> evaluations = evaluationDailyRepository.findAllEvaluations(SHORT_DATE_FORMAT.format(cursorDate));

            if (evaluations != null) {
                for (Object[] row : evaluations) {
                    Website website = websitesMap.get(((BigInteger) row[0]).longValue());

                    // Defining the date
                    long time;

                    // Perhaps was a failure, so there isn't date available.
                    // In this case we have to use the evaluation date
                    if (row[3] == null) {
                        // We have to add 3 hours at least to avoid the reports will be displayed on previous day
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(cursorDate);
                        cal.add(Calendar.HOUR, 3);

                        time = cal.getTime().getTime();
                    } else {
                        time = ((Date) row[3]).getTime();
                    }

                    int amountExceptions = 0;

                    while (amountExceptions < MAX_EXCEPTIONS_ALLOWED) {
                        try {
                            if (row[2] == null) {
                                System.out.println(((BigInteger) row[1]));
                                VariableFactory.getWorker(((BigInteger) row[1]).intValue()).sendFailedEvaluation(website, time);
                            } else {
                                VariableFactory.getWorker(((BigInteger) row[1]).intValue()).sendEvaluation(website, (float) row[2], time);
                            }

                            break;
                        }
                        // Perhaps Influx throw some exception.
                        // In that case we have to wait a little moment and rerun again
                        catch (InfluxDBException ex) {
                            LOG.log(Level.WARNING, ex.getMessage());
                            Thread.sleep(10000);
                            amountExceptions++;
                        }
                    }
                }
            }

            // Waiting a while to avoid that Influx blow up
            Thread.sleep(30000);

            // Moving to the day before
            cursorDate = DateHelper.moveDate(cursorDate, -1);
        }

        // Registrar en el log.
        LOG.log(Level.INFO, "Medición {0} regenerada", MEAS_WEBSITE_EVALUATION);
    }

}
