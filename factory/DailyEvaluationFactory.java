package cu.redcuba.factory;

import cu.redcuba.entity.EvaluationDaily;
import cu.redcuba.entity.EvaluationDailyPK;
import cu.redcuba.repository.EvaluationDailyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Component
public class DailyEvaluationFactory {

    private final EvaluationDailyRepository evaluationDailyRepository;

    @Autowired
    public DailyEvaluationFactory(EvaluationDailyRepository evaluationDailyRepository) {
        this.evaluationDailyRepository = evaluationDailyRepository;
    }

    public void createAndSave(long websiteId, boolean international, long variableId, long variableIndicatorId, Date day, float evaluation) {
        try {
            EvaluationDaily evDay = new EvaluationDaily(websiteId, variableId, variableIndicatorId, day);
            evDay.setInternational(international);
            evDay.setEvaluation(evaluation);
            evDay.setCreated(new Date());
            evaluationDailyRepository.save(evDay);
        } catch (Exception ignored) {

        }
    }

}
