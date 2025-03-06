package cu.redcuba.evaluations.converter;

import cu.redcuba.entity.Variable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ApiConverter implements Converter {

    private static final Map<Float, String> EVALUATIONS_STRING;

    static {
        EVALUATIONS_STRING = new HashMap<>();

        EVALUATIONS_STRING.put(0F, "Incorrecto");
        EVALUATIONS_STRING.put(0.5F, "A mejorar");
        EVALUATIONS_STRING.put(1F, "Correcto");
    }

    /**
     * Converts an evaluation from float to string.
     *
     * @param variable  The evaluated variable.
     * @param evaluation The evaluation to convert.
     * @return The converted evaluation.
     */
    @Override
    public String evaluationAsString(Variable variable, float evaluation) {
        switch (variable.getSlug()) {
            case "status-code":
                return String.valueOf(evaluation);
            case "page-weight":
                return evaluation >= 1024 ? EVALUATIONS_STRING.get(1F) : EVALUATIONS_STRING.get(0F);
            default:
                return EVALUATIONS_STRING.get(evaluation);
        }
    }

}
