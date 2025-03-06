package cu.redcuba.object;

public class MinificationTestResult {

    private Integer linesAmount; // Cantidad de líneas.

    private Integer linesIdentCount; // Cantidad de líneas identadas.

    private Integer linesLengthMedian; // Mediana de la longitud de las líneas.

    private Boolean minified; // Minificado.

    public Integer getLinesAmount() {
        return linesAmount;
    }

    public void setLinesAmount(Integer linesAmount) {
        this.linesAmount = linesAmount;
    }

    public Integer getLinesIdentCount() {
        return linesIdentCount;
    }

    public void setLinesIdentCount(Integer linesIdentCount) {
        this.linesIdentCount = linesIdentCount;
    }

    public Integer getLinesLengthMedian() {
        return linesLengthMedian;
    }

    public void setLinesLengthMedian(Integer linesLengthMedian) {
        this.linesLengthMedian = linesLengthMedian;
    }

    public Boolean getMinified() {
        return minified;
    }

    public void setMinified(Boolean minified) {
        this.minified = minified;
    }

}
