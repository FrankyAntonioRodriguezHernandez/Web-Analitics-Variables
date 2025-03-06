package cu.redcuba.output;

public class TitleOutput extends Output {

    private boolean exist;

    private boolean notExceedsMaximum;

    private boolean reachMinimum;

    private boolean hasKeywords;

    public boolean exist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }

    public boolean notExceedsMaximum() {
        return notExceedsMaximum;
    }

    public boolean reachMinimum() {
        return reachMinimum;
    }

    public boolean hasKeywords() {
        return hasKeywords;
    }

    public void setNotExceedsMaximum(boolean notExceedsMaximum) {
        this.notExceedsMaximum = notExceedsMaximum;
    }

    public void setReachMinimum(boolean reachMinimum) {
        this.reachMinimum = reachMinimum;
    }

    public void setHasKeywords(boolean hasKeywords) {
        this.hasKeywords = hasKeywords;
    }

}
