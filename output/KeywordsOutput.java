package cu.redcuba.output;

import java.util.List;

public class KeywordsOutput extends Output {

    private boolean exist;

    private List<String> keywords;

    private boolean notExceedsMaximum;

    private boolean reachMinimum;

    private boolean notHasStopwords;

    public boolean exist() {
        return exist;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public boolean notExceedsMaximum() {
        return notExceedsMaximum;
    }

    public boolean reachMinimum() {
        return reachMinimum;
    }

    public boolean notHasStopwords() {
        return notHasStopwords;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public void setNotExceedsMaximum(boolean notExceedsMaximum) {
        this.notExceedsMaximum = notExceedsMaximum;
    }

    public void setReachMinimum(boolean reachMinimum) {
        this.reachMinimum = reachMinimum;
    }

    public void setNotHasStopwords(boolean notHasStopwords) {
        this.notHasStopwords = notHasStopwords;
    }

}
