package cu.redcuba.output;

public class FeedOutput extends Output {

    private boolean exist;

    private boolean valid;

    public boolean exist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

}
