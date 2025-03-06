package cu.redcuba.controller.api;

import com.google.gson.Gson;
import cu.redcuba.client.WebsitesDirectoryClient;
import cu.redcuba.entity.EvaluationDaily;
import cu.redcuba.factory.RoundControlFactory;
import cu.redcuba.object.Website;
import cu.redcuba.repository.EvaluationDailyRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Profile("api")
@Api(tags = "Evaluación")
@RestController
@RequestMapping(path = "/api/evaluate")
public class EvaluateController {

    private static final Logger LOG = LoggerFactory.getLogger(EvaluateController.class);

    private static final Gson GSON = new Gson();

    private static final String ROUND_HEADER = "manual_evaluation";

    private static final SimpleDateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private final EvaluationDailyRepository evaluationDailyRepository;

    private final WebsitesDirectoryClient websitesDirectoryClient;

    private final RoundControlFactory roundControlFactory;

    private final RabbitTemplate rabbitTemplate;

    @Value("${evw.queue.homepage}")
    private String homepageQueueName;

    @Autowired
    public EvaluateController(
            EvaluationDailyRepository evaluationDailyRepository,
            WebsitesDirectoryClient websitesDirectoryClient,
            RoundControlFactory roundControlFactory,
            @Qualifier("directWorkTemplate") RabbitTemplate rabbitTemplate
    ) {
        this.evaluationDailyRepository = evaluationDailyRepository;
        this.websitesDirectoryClient = websitesDirectoryClient;
        this.roundControlFactory = roundControlFactory;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * TODO: add support to create new website in wsd if isn't exists.
     */
    @Deprecated(since = "2019-04-25 no deberá ser empleada más.")
    @ApiOperation("Evaluar un sitio web a partir de hostname")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Website's evaluation requested."),
    })
    @ResponseBody
    @GetMapping(path = "hostname/{hostname}", produces = "text/plain")
    public boolean hostname(@PathVariable("hostname") String hostname) {
        // Validar el hostname.
        if ("".equals(hostname)) {
            LOG.info("Evaluación manual para {}, el hostname no puede estar vacío.", hostname);
            return false;
        }
        Date date = new Date();
        // Obtener el valor simplificado de la fecha.
        String shortDate = SHORT_DATE_FORMAT.format(date);
        String newHostname = hostname.replaceAll("\\+", ".");
        Website website = websitesDirectoryClient.getWebsiteByHostname(newHostname);
        // Registrar la entrada en el log.
        LOG.info("Evaluación manual para {}", newHostname);
        // Inicializar el control de las rondas.
        roundControlFactory.init(shortDate, homepageQueueName, 1);
        // Crear una cabecera para identificar a que ronda pertenece.
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeader(ROUND_HEADER, shortDate);
        // Construir cada uno de los mensajes, establecer la cabecera y enviarlo.
        rabbitTemplate.send(homepageQueueName, new Message(GSON.toJson(website).getBytes(), messageProperties));
        return true;
    }

    @ApiOperation("Evaluar un sitio web a partir de id y hostname")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Website's evaluation requested."),
    })
    @ResponseBody
    @PostMapping(path = "website")
    public boolean website(
            @RequestParam("id") Long id,
            @RequestParam("hostname") String hostname
    ) {
        // Validar el identificador del sitio web.
        if (id <= 0) {
            LOG.error("Website evaluation: id must be positive");
            return false;
        }
        // Validar el hostname del sitio web.
        if (hostname.isEmpty()) {
            LOG.error("Website evaluation: hostname can't be empty");
            return false;
        }
        // Validar que el sitio web no ha sido evaluado en el día.
        Date day = new Date();
        List<EvaluationDaily> evaluationDaily = evaluationDailyRepository.findByPkWebsiteIdAndPkDay(id, day);
        if (!evaluationDaily.isEmpty()) {
            LOG.error("Website evaluation: already evaluated");
            return false;
        }
        // Construir un objeto para ser encolado.
        Website website = new Website(id, hostname);
        // Construir cada uno de los mensajes, establecer la cabecera y enviarlo.
        MessageProperties messageProperties = new MessageProperties();
        rabbitTemplate.send(homepageQueueName, new Message(GSON.toJson(website).getBytes(), messageProperties));
        // Registrar la entrada en el log.
        LOG.info("Website evaluation: id {}, hostname {}", id, hostname);
        return true;
    }

}
