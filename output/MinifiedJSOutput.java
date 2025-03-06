package cu.redcuba.output;

import cu.redcuba.model.JsItem;

import java.util.List;

public class MinifiedJSOutput extends Output {

    private boolean has;

    private List<JsItem> jsItems;

    private float percent;

    public boolean has() {
        return has;
    }

    public void setHas(boolean has) {
        this.has = has;
    }

    public List<JsItem> getJsItems() {
        return jsItems;
    }

    public void setJsItems(List<JsItem> jsItems) {
        this.jsItems = jsItems;
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }
}
