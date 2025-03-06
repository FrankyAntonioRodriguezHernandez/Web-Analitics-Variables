package cu.redcuba.output;

public class ScreenResolutionOutput extends Output {

    private boolean existMobileResolution;
    private boolean existTabletResolution;
    private boolean existBigResolution;

    public boolean existMobileResolution() {
        return existMobileResolution;
    }

    public void setExistMobileResolution(boolean existMobileResolution) {
        this.existMobileResolution = existMobileResolution;
    }

    public boolean existTabletResolution() {
        return existTabletResolution;
    }

    public void setExistTabletResolution(boolean existTabletResolution) {
        this.existTabletResolution = existTabletResolution;
    }

    public boolean existBigResolution() {
        return existBigResolution;
    }

    public void setExistBigResolution(boolean existBigResolution) {
        this.existBigResolution = existBigResolution;
    }

}
