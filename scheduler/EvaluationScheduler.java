package cu.redcuba.scheduler;

import com.google.gson.Gson;
import cu.redcuba.client.WebsitesDirectoryClient;
import cu.redcuba.factory.RoundControlFactory;
import cu.redcuba.object.Website;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
@EnableScheduling
@Profile({"producer", "command"})
public class EvaluationScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(EvaluationScheduler.class);

    private static final Gson GSON = new Gson();

    private static final String ROUND_HEADER = "round";

    private static final SimpleDateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private final WebsitesDirectoryClient websitesDirectoryClient;

    private final RoundControlFactory roundControlFactory;

    private final RabbitTemplate rabbitTemplate;

    @Value("${evw.queue.homepage}")
    private String homepageQueueName;

    @Autowired
    public EvaluationScheduler(
            WebsitesDirectoryClient websitesDirectoryClient,
            RoundControlFactory roundControlFactory,
            @Qualifier("directWorkTemplate") RabbitTemplate rabbitTemplate
    ) {
        this.websitesDirectoryClient = websitesDirectoryClient;
        this.roundControlFactory = roundControlFactory;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(cron = "${scheduler.dailyWebsiteEvaluation}")
    public void dailyWebsiteEvaluationEnqueue() {
        // Obtener el valor simplicado de la fecha.
        Date date = new Date();
        String shortDate = SHORT_DATE_FORMAT.format(date);
        // Obtener los Sitios Web habilitados.
        List<Website> websites = websitesDirectoryClient.getWebsitesEnabledCategorized();
        int websitesSize = websites != null ? websites.size() : 0;
        if (websitesSize <= 0) {
            LOG.info("Day evaluation {}, there are no enabled Websites to process", shortDate);
            return;
        }
        // Registrar la entrada en el log.
        LOG.info("Day evaluation {}, processing {} Websites enabled", shortDate, websitesSize);
        // Inicializar el control de las rondas.
        roundControlFactory.init(shortDate, homepageQueueName, websitesSize);
        // Crear una cabecera para identificar a que ronda pertenece.
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeader(ROUND_HEADER, shortDate);
        // Construir cada uno de los mensajes, establecer la cabecera y enviarlo.
        websites.forEach(
                website -> rabbitTemplate.send(homepageQueueName, new Message(GSON.toJson(website).getBytes(), messageProperties))
        );
    }

}
