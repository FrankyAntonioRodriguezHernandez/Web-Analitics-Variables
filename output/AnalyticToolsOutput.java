package cu.redcuba.output;

/**
 * Removed hasGoogleAnalyticImage attribute, getter and setter.
 */
public class AnalyticToolsOutput extends Output {

    private boolean hasGoogleAnalyticsScript;

    private boolean hasPiwikScript;

    private boolean hasPiwikImage;

    private boolean hasMatomoScript;

    private boolean hasMatomoImage;

    private boolean hasGoogleTagScript;

    private String gaTrackingIdScriptGtag;

    private boolean hasRedCubaScript;

    public boolean hasGoogleAnalyticsScript() {
        return hasGoogleAnalyticsScript;
    }

    public void setHasGoogleAnalyticsScript(boolean hasGoogleAnalyticsScript) {
        this.hasGoogleAnalyticsScript = hasGoogleAnalyticsScript;
    }

    public boolean hasPiwikScript() {
        return hasPiwikScript;
    }

    public void setHasPiwikScript(boolean hasPiwikScript) {
        this.hasPiwikScript = hasPiwikScript;
    }

    public boolean hasPiwikImage() {
        return hasPiwikImage;
    }

    public void setHasPiwikImage(boolean hasPiwikImage) {
        this.hasPiwikImage = hasPiwikImage;
    }

    public boolean hasMatomoScript() {
        return hasMatomoScript;
    }

    public void setHasMatomoScript(boolean hasMatomoScript) {
        this.hasMatomoScript = hasMatomoScript;
    }

    public boolean hasMatomoImage() {
        return hasMatomoImage;
    }

    public void setHasMatomoImage(boolean hasMatomoImage) {
        this.hasMatomoImage = hasMatomoImage;
    }

    public boolean hasGoogleTagScript() {
        return hasGoogleTagScript;
    }

    public void setHasGoogleTagScript(boolean hasGoogleTagScript) {
        this.hasGoogleTagScript = hasGoogleTagScript;
    }

    public String getGaTrackingIdScriptGtag() {
        return gaTrackingIdScriptGtag;
    }

    public void setGaTrackingIdScriptGtag(String gaTrackingIdScriptGtag) {
        this.gaTrackingIdScriptGtag = gaTrackingIdScriptGtag;
    }

    public boolean isHasRedCubaScript() {
        return hasRedCubaScript;
    }

    public void setHasRedCubaScript(boolean hasRedCubaScript) {
        this.hasRedCubaScript = hasRedCubaScript;
    }
}
