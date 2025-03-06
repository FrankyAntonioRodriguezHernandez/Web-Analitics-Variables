/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cu.redcuba.factory;

import cu.redcuba.entity.RoundControl;
import cu.redcuba.entity.RoundControlPK;
import cu.redcuba.repository.RoundControlRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author developer
 */
@SuppressWarnings("OptionalGetWithoutIsPresent")
public class RoundControlFactory {

    private static final Logger LOG = Logger.getLogger(RoundControlFactory.class.getName());

    private static final SimpleDateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private static final SimpleDateFormat SHORT_DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Autowired
    private RoundControlRepository roundControlRepository;

    /**
     * Obtener la fecha de la ronda.
     *
     * @param roundDaily String
     * @return Date
     */
    private Date roundDailyToDate(String roundDaily) {
        Date roundDate = null;
        try {
            roundDate = SHORT_DATE_FORMAT.parse(roundDaily);
        } catch (ParseException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return roundDate;
    }

    /**
     * Obtener la fecha y hora de la ronda.
     *
     * @param roundHourly String
     * @return Date
     */
    private Date roundHourlyToDate(String roundHourly) {
        Date roundDate = null;
        try {
            roundDate = SHORT_DATETIME_FORMAT.parse(roundHourly);
        } catch (ParseException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return roundDate;
    }

    public RoundControl init(String round, String type, int itemsSize) {
        Optional<RoundControl> roundControl = roundControlRepository.findById(new RoundControlPK(round, type));
        return roundControl.orElseGet(() -> roundControlRepository.save(new RoundControl(round, type, itemsSize, 0)));
    }

    public RoundControl get(String round, String type) {
        return roundControlRepository.findById(new RoundControlPK(round, type)).get();
    }

    @Transactional
    public RoundControl increment(String round, String type, int itemsProcessed) {
        roundControlRepository.incItemsProcessed(round, type, itemsProcessed);
        return roundControlRepository.findById(new RoundControlPK(round, type)).get();
    }

    public RoundControl increment(String round, String type) {
        return increment(round, type, 1);
    }
}
