package cu.redcuba.controller.api;

import com.fasterxml.jackson.annotation.JsonView;
import cu.redcuba.client.WebsitesDirectoryClient;
import cu.redcuba.controller.api.views.ApiView;
import cu.redcuba.controller.api.views.NotifierApiView;
import cu.redcuba.entity.EvaluationDaily;
import cu.redcuba.entity.Variable;
import cu.redcuba.entity.VariableIndicator;
import cu.redcuba.evaluations.converter.ApiConverter;
import cu.redcuba.factory.VariableFactory;
import cu.redcuba.object.EvaluationValueInterval;
import cu.redcuba.object.Website;
import cu.redcuba.repository.EvaluationDailyRepository;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Profile("api")
@Api(tags = "Evaluación")
@RestController
@RequestMapping(path = "/api/evaluations")
public class EvaluationController {

    private final EvaluationDailyRepository evaluationDailyRepository;

    private final VariableFactory variableFactory;

    private final ApiConverter apiReporter;

    private final WebsitesDirectoryClient websitesDirectoryClient;

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-M-dd", Locale.ENGLISH);

    @Autowired
    public EvaluationController(
            VariableFactory variableFactory,
            EvaluationDailyRepository evaluationDailyRepository,
            ApiConverter apiReporter, WebsitesDirectoryClient websitesDirectoryClient) {
        this.variableFactory = variableFactory;
        this.evaluationDailyRepository = evaluationDailyRepository;
        this.apiReporter = apiReporter;
        this.websitesDirectoryClient = websitesDirectoryClient;
    }

    @ApiOperation(value = "Obtención de los sitios web que han agregado o quitado el código de analítica de Telus", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list")
            , @ApiResponse(code = 401, message = "You are not authorized to view the resource")
            , @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
            , @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping(value = "/analytic_verify", produces = "application/json")
    @JsonView(ApiView.class)
    public List<Map<String, Object>> getSeoWebMasAnalyticChange() {
        List<Website> websites = websitesDirectoryClient.getWebsitesEnabledCategorized();
        List<Map<String, Object>> results = new ArrayList<>();
        for (Website w : websites) {
            List<Object[]> lastEvaluationsBySiteAndVariableAndIndicator = evaluationDailyRepository.findLastEvaluationsBySiteAndVariableAndIndicator(w.getId(), 10, 102);
            if (lastEvaluationsBySiteAndVariableAndIndicator.size() == 2) {
                if ((Float.parseFloat(lastEvaluationsBySiteAndVariableAndIndicator.get(1)[0].toString()) != Float.parseFloat(lastEvaluationsBySiteAndVariableAndIndicator.get(0)[0].toString()))) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("website", w.getId());
                    result.put("date", lastEvaluationsBySiteAndVariableAndIndicator.get(1)[1]);
                    result.put("change", (Float.parseFloat(lastEvaluationsBySiteAndVariableAndIndicator.get(1)[0].toString()) > Float.parseFloat(lastEvaluationsBySiteAndVariableAndIndicator.get(0)[0].toString())) ? 1 : 0);
                    results.add(result);
                }
            }
        }
        return results;
    }

    @ApiOperation(value = "Obtener la evaluacion de un sitio web en una fecha determinada", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list")
            , @ApiResponse(code = 401, message = "You are not authorized to view the resource")
            , @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
            , @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping(value = "/get_by_day", produces = "application/json")
    @JsonView(ApiView.class)
    public Map<String, Object> getEvalDay(
            @ApiParam(value = "The website identifier.")
            @RequestParam(value = "website_id") Long websiteId,
            @ApiParam(value = "The evaluation date.")
            @RequestParam(value = "evaluation_date") Long evaluationDate
    ) {
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd 'de' MMM 'del' yyyy");

        Date lastDay = new Date();
        lastDay.setTime(evaluationDate);

        List<EvaluationDaily> evaluationsList = evaluationDailyRepository.findByPkWebsiteIdAndPkDay(websiteId, lastDay);

        Map<String, Map<String, EvaluationDaily>> evaluations = new HashMap<>();

        for (EvaluationDaily evaluation : evaluationsList) {
            // Getting the variable
            Variable variable = variableFactory.getVariable(
                    evaluation.getEvaluationDailyPK().getVariableId());

            // Getting the map for that variable
            Map<String, EvaluationDaily> list = evaluations.getOrDefault(
                    variable.getSlug(), new HashMap<>());

            // Getting the indicator
            VariableIndicator indicator = variableFactory.getIndicator(
                    evaluation.getEvaluationDailyPK().getIndicatorId());

            //Defining the evaluation as a string
            evaluation.setEvaluationAsString(
                    apiReporter.evaluationAsString(variable, evaluation.getEvaluation()));

            // Adding the new evaluation
            list.put(indicator.getSlug(), evaluation);

            if (!evaluations.containsKey(variable.getSlug())) {
                evaluations.put(variable.getSlug(), list);
            }
        }

        Map<String, Object> result = new HashMap<>();
        String date = "";
        try {
            date = DATE_FORMAT.format(lastDay);
        } catch (Exception ex) {
            // Nothing to do.
        }
        result.put("day", lastDay);
        result.put("dayText", date);
        result.put("evaluations", evaluations);

        return result;
    }

    @ApiOperation(value = "Determinar si un sitio web específico ha agregado o quitado el código de analítica de Telus", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list")
            , @ApiResponse(code = 401, message = "You are not authorized to view the resource")
            , @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
            , @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping(value = "/analytic_verify_by_website", produces = "application/json")
    @JsonView(ApiView.class)
    public Map<String, Object> getSeoWebMasAnalyticChangeByWebSite(
            @ApiParam(value = "The website identifier.")
            @RequestParam(value = "website_id") Long websiteId
    ) {
        List<Object[]> lastEvaluationsBySiteAndVariableAndIndicator = evaluationDailyRepository.findLastEvaluationsBySiteAndVariableAndIndicator(websiteId, 10, 102);
        Map<String, Object> result = new HashMap<>();
        if (lastEvaluationsBySiteAndVariableAndIndicator.size() == 2) {
            if ((Float.parseFloat(lastEvaluationsBySiteAndVariableAndIndicator.get(1)[0].toString()) != Float.parseFloat(lastEvaluationsBySiteAndVariableAndIndicator.get(0)[0].toString()))) {
                result.put("website", websiteId);
                result.put("date", lastEvaluationsBySiteAndVariableAndIndicator.get(1)[1]);
                result.put("change", (Float.parseFloat(lastEvaluationsBySiteAndVariableAndIndicator.get(1)[0].toString()) > Float.parseFloat(lastEvaluationsBySiteAndVariableAndIndicator.get(0)[0].toString())) ? 0 : 1);
            }
        }
        return result;
    }

    @ApiOperation(value = "Obtener los sitios dada una evaluacion una fecha y un indicador", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list")
            , @ApiResponse(code = 401, message = "You are not authorized to view the resource")
            , @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
            , @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping(value = "/websites_indicator_day_eval", produces = "application/json")
    @JsonView(ApiView.class)
    public List<String> getAllWebsiteByStatusDateAndIndicator(
            @ApiParam(value = "The evaluation day.")
            @RequestParam(value = "day") String day,
            @ApiParam(value = "The indicator.")
            @RequestParam(value = "indicator") String indicator,
            @ApiParam(value = "The indicator value.")
            @RequestParam(value = "value") String value
    ) throws ParseException {
        Date parseDate = formatter.parse(day);
        List<Object[]> lastEvaluationsBySiteAndVariableAndIndicator = evaluationDailyRepository.getAllWebsiteByStatusDateAndIndicator(parseDate, Long.parseLong(indicator), Float.parseFloat(value));
        List<String> result = new ArrayList<>();
        lastEvaluationsBySiteAndVariableAndIndicator.forEach(objects -> result.add(String.valueOf(objects[0])));
        return result;
    }

    @ApiOperation(value = "Obtener la última evaluación de un sitio web, si este no lleva mas de 7 días de evaluado", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list")
            , @ApiResponse(code = 401, message = "You are not authorized to view the resource")
            , @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
            , @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping(value = "/last", produces = "application/json")
    @JsonView(ApiView.class)
    public Map<String, Object> getLastWebsite(
            @ApiParam(value = "The website identifier.")
            @RequestParam(value = "website_id") Long websiteId
    ) {
        // Obtener solo las evaluaciones con código de estado 200 que no tengan más de 7 dias de antigüedad en la base de datos.
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DAY_OF_YEAR, -7);

        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd 'de' MMM 'del' yyyy");

        Date lastDay = evaluationDailyRepository.findMaxDayByWebSiteIdANDDay(websiteId, calendar.getTime());

        List<EvaluationDaily> evaluationsList = evaluationDailyRepository.findByPkWebsiteIdAndPkDay(websiteId, lastDay);

        Map<String, Map<String, EvaluationDaily>> evaluations = new HashMap<>();

        for (EvaluationDaily evaluation : evaluationsList) {
            // Getting the variable
            Variable variable = variableFactory.getVariable(
                    evaluation.getEvaluationDailyPK().getVariableId());

            // Getting the map for that variable
            Map<String, EvaluationDaily> list = evaluations.getOrDefault(
                    variable.getSlug(), new HashMap<>());

            // Getting the indicator
            VariableIndicator indicator = variableFactory.getIndicator(
                    evaluation.getEvaluationDailyPK().getIndicatorId());

            //Defining the evaluation as a string
            evaluation.setEvaluationAsString(
                    apiReporter.evaluationAsString(variable, evaluation.getEvaluation()));

            // Adding the new evaluation
            list.put(indicator.getSlug(), evaluation);

            if (!evaluations.containsKey(variable.getSlug())) {
                evaluations.put(variable.getSlug(), list);
            }
        }

        Map<String, Object> result = new HashMap<>();
        String date = "";
        try {
            date = DATE_FORMAT.format(lastDay);
        } catch (Exception ex) {
            // Nothing to do.
        }
        result.put("day", lastDay);
        result.put("dayText", date);
        result.put("evaluations", evaluations);

        return result;
    }

    @ApiOperation(value = "View values of the website´s indicator evaluation between two days", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list")
            , @ApiResponse(code = 401, message = "You are not authorized to view the resource")
            , @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
            , @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @PostMapping(value = "website_indicator_evaluation_between_days", produces = "application/json")
    @JsonView(NotifierApiView.class)
    public List<EvaluationDaily> getWebsiteIndicatorEvaluationBetweenDays(
            @ApiParam(value = "The website identifier.") @RequestParam Long websiteId,
            @ApiParam(value = "The variable identifier.") @RequestParam Long variableId,
            @ApiParam(value = "The indicator identifier.") @RequestParam Long indicatorId,
            @ApiParam(value = "The minimal evaluation day.") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date dayMin,
            @ApiParam(value = "The maximum evaluation day.") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date dayMax
    ) {
        return evaluationDailyRepository.findByPk_WebsiteIdAndPk_VariableIdAndPk_IndicatorIdAndPk_DayBetweenOrderByPk_DayDesc(websiteId, variableId, indicatorId, dayMin, dayMax);
    }

    @ApiOperation(value = "View last evaluation interval of the website´s indicator evaluation")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list")
            , @ApiResponse(code = 401, message = "You are not authorized to view the resource")
            , @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
            , @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @PostMapping(value = "last_evaluation_value_interval", produces = "application/json")
    public EvaluationValueInterval getLastEvaluationValueInterval(
            @ApiParam(value = "The website identifier.") @RequestParam Long websiteId,
            @ApiParam(value = "The variable identifier.") @RequestParam Long variableId,
            @ApiParam(value = "The indicator identifier.") @RequestParam Long indicatorId
    ) {
        return evaluationDailyRepository.lastEvaluationValueInterval(websiteId, variableId, indicatorId);
    }

}
