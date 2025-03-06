package cu.redcuba.output;

public class FaviconOutput extends Output {

    private boolean existFavicon;
    
    private boolean validFavicon;
    
    public boolean ExistFavicon() {
        return existFavicon;
    }

    public void setExistFavicon(boolean existFavicon) {
        this.existFavicon = existFavicon;
    }

    public boolean ValidFavicon() {
        return validFavicon;
    }

    public void setValidFavicon(boolean validFavicon) {
        this.validFavicon = validFavicon;
    }

}
