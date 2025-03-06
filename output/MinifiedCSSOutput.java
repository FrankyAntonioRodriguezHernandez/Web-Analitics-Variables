package cu.redcuba.output;

import cu.redcuba.model.CssItem;

import java.util.List;

public class MinifiedCSSOutput extends Output {

    private boolean has;

    private List<CssItem> cssItems;

    private float percent;

    public boolean has() {
        return has;
    }

    public void setHas(boolean has) {
        this.has = has;
    }

    public List<CssItem> getCssItems() {
        return cssItems;
    }

    public void setCssItems(List<CssItem> cssItems) {
        this.cssItems = cssItems;
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

}
