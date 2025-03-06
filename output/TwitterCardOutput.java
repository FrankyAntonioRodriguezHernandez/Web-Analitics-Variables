package cu.redcuba.output;

public class TwitterCardOutput extends Output {

    private boolean hasTwitterCard;

    private boolean hasTwitterSite;

    private boolean hasTwitterTitle;

    private boolean hasTwitterDescription;

    private boolean hasTwitterImage;


    public boolean hasTwitterCard() {
        return hasTwitterCard;
    }

    public void setHasTwitterCard(boolean hasTwitterCard) {
        this.hasTwitterCard = hasTwitterCard;
    }

    public boolean hasTwitterSite() {
        return hasTwitterSite;
    }

    public void setHasTwitterSite(boolean hasTwitterSite) {
        this.hasTwitterSite = hasTwitterSite;
    }

    public boolean hasTwitterTitle() {
        return hasTwitterTitle;
    }

    public void setHasTwitterTitle(boolean hasTwitterTitle) {
        this.hasTwitterTitle = hasTwitterTitle;
    }

    public boolean hasTwitterDescription() {
        return hasTwitterDescription;
    }

    public void setHasTwitterDescription(boolean hasTwitterDescription) {
        this.hasTwitterDescription = hasTwitterDescription;
    }

    public boolean isHasTwitterImage() {
        return hasTwitterImage;
    }

    public void setHasTwitterImage(boolean hasTwitterImage) {
        this.hasTwitterImage = hasTwitterImage;
    }
}
