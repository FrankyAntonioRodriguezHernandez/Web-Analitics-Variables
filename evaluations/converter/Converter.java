package cu.redcuba.evaluations.converter;

import cu.redcuba.entity.Variable;

public interface Converter {

    /**
     * Converts an evaluation from float to string.
     *
     * @param variable The evaluated variable.
     * @param evaluation The evaluation to convert.
     * @return The converted evaluation.
     */
    String evaluationAsString (Variable variable, float evaluation);

}
