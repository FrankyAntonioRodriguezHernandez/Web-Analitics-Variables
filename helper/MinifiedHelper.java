package cu.redcuba.helper;

import cu.redcuba.object.MinificationTestResult;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author developer
 */
public class MinifiedHelper {

    private static final float INDENT_COUNT_THRESHOLD = 20;

    private static final int COMMON_LINE_MAX_LENGTH = 200;

    private static final String WHITESPACES_REGEX = "\r\n|\r|\n";

    private static final String COMMENT_REGEX = "/\\*[\\s\\S]*?\\*/\\r?\\n?|//.{0,200}?(?:\\r?\\n|$)";

    private static final String TRAILING_LF_REGEX = "\\r?\\n$";

    private static final Pattern REPEATED_WHITESPACES_PATTERN = Pattern.compile("(\\s){2,}");

    private static final Pattern IDENT_PATTERN = Pattern.compile("^\\s+");

    /**
     * Determinar si el contenido tiene solo una línea.
     *
     * @param content Contenido a analizar.
     * @return Retorna verdadero si la longitud del contenido del archivo es el mismo que el contenido de la primera línea.
     */
    public boolean hasOnlyOneLine(String content) {
        String[] contentLines = content.split(WHITESPACES_REGEX, 2);
        return contentLines[0].trim().length() == content.trim().length();
    }

    /**
     * Determinar si el contenido no tiene caracteres de espacio repetidos.
     *
     * @param content Contenido a analizar.
     * @return Retorna verdadero si el contenido tiene espacios repetidos.
     */
    public boolean hasRepeatedWhitespaces(String content) {
        // Quitar todos los comentarios.
        String noComments = content.replaceAll(COMMENT_REGEX, "");
        // Quitar todos los fines de línea.
        String noTrailingLf = noComments.replaceAll(TRAILING_LF_REGEX, "");

        Matcher matcher = REPEATED_WHITESPACES_PATTERN.matcher(noTrailingLf);
        return matcher.find();
    }

    /**
     * Determinar la mediana de la longitud de las líneas.
     *
     * @param linesLength Listado de las longitudes de cada una de las líneas.
     * @return La longitud mediana de la lista.
     */
    private int median(List<Integer> linesLength) {
        linesLength.sort(Comparator.comparingInt(o -> o));

        if (linesLength.size() == 0) {
            return 0;
        }

        int half = (int) Math.floor(linesLength.size() / 2f);

        // Cuando es impar, existe un elemento en la mitad.
        if (linesLength.size() % 2 == 1) {
            return linesLength.get(half);
        }

        // Cuando es par, se promedian los dos elementos al rededor de la mitad.
        return (linesLength.get(half - 1) + linesLength.get(half)) / 2;
    }

    /**
     * Realizar una prueba de minificado al contenido de un archivo.
     *
     * @param content Contenido del archivo.
     * @return Objeto que contiene el resultado de la prueba de minificado.
     */
    public MinificationTestResult minificationTest(String content) {
        // Quitar todos los comentarios.
        String noComments = content.replaceAll(COMMENT_REGEX, "");
        // Quitar todos los fines de línea.
        String noTrailingLf = noComments.replaceAll(TRAILING_LF_REGEX, "");

        // Separar cada una de las líneas del contenido.
        String[] contentLines = noTrailingLf.split("\n");

        int linesAmount = contentLines.length;
        int linesIndentCount = 0;
        List<Integer> linesLength = new LinkedList<>();

        // Recorrer cada una de las líneas.
        for (String contentLine : contentLines) {
            // Determinar si la línea esta identada.
            Matcher identMatcher = IDENT_PATTERN.matcher(contentLine);
            if (identMatcher.find()) {
                linesIndentCount++;
            }
            // Almacenar la longitud de la línea.
            linesLength.add(contentLine.length());
        }

        // Determinar el porciento de líneas identadas.
        float identPercent = linesIndentCount * 100f / linesAmount;

        // Determinar la mediana de la longitud de las líneas.
        int linesLengthMedian = median(linesLength);

        // Determinar si se encuentra minificado o no el contenido.
        boolean minified = (identPercent < INDENT_COUNT_THRESHOLD) && (linesAmount <= 1 || linesLengthMedian > COMMON_LINE_MAX_LENGTH);

        // Conformar el resultado a devolver.
        MinificationTestResult minificationTestResult = new MinificationTestResult();
        minificationTestResult.setLinesAmount(linesAmount);
        minificationTestResult.setLinesIdentCount(linesIndentCount);
        minificationTestResult.setLinesLengthMedian(linesLengthMedian);
        minificationTestResult.setMinified(minified);

        return minificationTestResult;
    }

}
