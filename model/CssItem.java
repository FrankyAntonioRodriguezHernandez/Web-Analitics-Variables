package cu.redcuba.model;

import com.fasterxml.jackson.annotation.JsonView;
import cu.redcuba.controller.api.views.ApiView;

import java.io.Serializable;
import java.util.Objects;

public class CssItem implements Serializable {

    String url;

    Integer linesAmount; // Cantidad de líneas.

    Integer linesIdentCount; // Cantidad de líneas identadas.

    Integer linesLengthMedian; // Mediana de la longitud de las líneas.

    Boolean minified; // Minificado.

    @JsonView(ApiView.class)
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @JsonView(ApiView.class)
    public Integer getLinesAmount() {
        return linesAmount;
    }

    public void setLinesAmount(Integer linesAmount) {
        this.linesAmount = linesAmount;
    }

    @JsonView(ApiView.class)
    public Integer getLinesIdentCount() {
        return linesIdentCount;
    }

    public void setLinesIdentCount(Integer linesIdentCount) {
        this.linesIdentCount = linesIdentCount;
    }

    @JsonView(ApiView.class)
    public Integer getLinesLengthMedian() {
        return linesLengthMedian;
    }

    public void setLinesLengthMedian(Integer linesLengthMedian) {
        this.linesLengthMedian = linesLengthMedian;
    }

    @JsonView(ApiView.class)
    public Boolean getMinified() {
        return minified;
    }

    public void setMinified(Boolean minified) {
        this.minified = minified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CssItem cssItem = (CssItem) o;
        return url.equals(cssItem.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, linesAmount, linesIdentCount, linesLengthMedian, minified);
    }

}
