package cu.redcuba.output;

public class CanonicalURLOutput extends Output {

    private boolean exist;

    private boolean valid;

    private boolean absolute;

    public boolean exist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }

    public boolean isAbsolute() {
        return absolute;
    }

    public void setAbsolute(boolean absolute) {
        this.absolute = absolute;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

}
