package cu.redcuba.model;

import com.fasterxml.jackson.annotation.JsonView;
import cu.redcuba.controller.api.views.ApiView;

import java.io.Serializable;
import java.util.Objects;

public class ImageItem implements Serializable {
    String src;
    String alt;

    @JsonView(ApiView.class)
    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    @JsonView(ApiView.class)
    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageItem imageItem = (ImageItem) o;
        return src.equals(imageItem.src) &&
                alt.equals(imageItem.alt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(src, alt);
    }
}
