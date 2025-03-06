package cu.redcuba.output;

public class OpenGraphOutput extends Output {

    private boolean hasType;

    private boolean hasUrl;

    private boolean hasTitle;

    private boolean hasDescription;

    private boolean hasImage;

    public boolean hasType() {
        return hasType;
    }

    public void setHasType(boolean hasType) {
        this.hasType = hasType;
    }

    public boolean hasUrl() {
        return hasUrl;
    }

    public void setHasUrl(boolean hasUrl) {
        this.hasUrl = hasUrl;
    }

    public boolean hasTitle() {
        return hasTitle;
    }

    public void setHasTitle(boolean hasTitle) {
        this.hasTitle = hasTitle;
    }

    public boolean hasDescription() {
        return hasDescription;
    }

    public void setHasDescription(boolean hasDescription) {
        this.hasDescription = hasDescription;
    }

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }
}
