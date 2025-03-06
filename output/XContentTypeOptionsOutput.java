package cu.redcuba.output;

public class XContentTypeOptionsOutput extends Output {

    private boolean exist;

    private boolean valid;

    public boolean exist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }

    public boolean valid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}


