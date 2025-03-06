package cu.redcuba.controller.command;

import cu.redcuba.scheduler.EvaluationScheduler;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

@Profile("producer")
@Api(tags = "Comandos de Mantenimiento")
@RestController
@RequestMapping(path = "/command/scheduler")
public class SchedulerController {

    private static final Logger LOG = LoggerFactory.getLogger(SchedulerController.class);

    private final EvaluationScheduler evaluationScheduler;

    @Autowired
    public SchedulerController(EvaluationScheduler taskScheduler) {
        this.evaluationScheduler = taskScheduler;
    }

    @ResponseBody
    @PostMapping(path = "/daily", produces = "text/plain")
    public String daily() {
        evaluationScheduler.dailyWebsiteEvaluationEnqueue();
        LOG.info("Executed the daily task manually.");

        return "Executed the daily task manually";
    }
}
