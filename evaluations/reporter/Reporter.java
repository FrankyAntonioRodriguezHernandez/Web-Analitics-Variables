package cu.redcuba.evaluations.reporter;

import java.text.SimpleDateFormat;

/**
 * A reporter is a component which send data to only one measurement.
 */
public interface Reporter {

    SimpleDateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Refills the measurement associated to this reporter.
     *
     * @param args Arguments needed by the reporter.
     *
     * @throws Exception Thrown during the refilling process.
     */
    void refill(Object... args) throws Exception;

}
