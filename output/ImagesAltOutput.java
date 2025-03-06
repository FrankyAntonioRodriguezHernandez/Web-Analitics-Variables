package cu.redcuba.output;

import cu.redcuba.model.ImageItem;

import java.util.List;

public class ImagesAltOutput extends Output {
    private List<ImageItem> imageItems;

    private float percent;

    private boolean has;

    public List<ImageItem> getImageItems() {
        return imageItems;
    }

    public void setImageItems(List<ImageItem> imageItems) {
        this.imageItems = imageItems;
    }

    public void setHas(boolean has) {
        this.has = has;
    }

    public boolean has() {
        return has;
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }
}
