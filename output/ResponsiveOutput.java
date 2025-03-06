package cu.redcuba.output;

public class ResponsiveOutput extends Output {

    private boolean existViewport;

    private boolean validViewport;

    public boolean existViewport() {
        return existViewport;
    }

    public void setExistViewport(boolean existViewport) {
        this.existViewport = existViewport;
    }

    public boolean validViewport() {
        return validViewport;
    }

    public void setValidViewport(boolean validViewport) {
        this.validViewport = validViewport;
    }
}
