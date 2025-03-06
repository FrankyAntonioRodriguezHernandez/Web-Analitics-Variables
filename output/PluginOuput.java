package cu.redcuba.output;

public class PluginOuput extends Output {

    private boolean noExistFlash;
    private boolean noExistJava;
    private boolean noExistSilverlight;
    private boolean validFlash;

    public boolean isNoExistJava() {
        return noExistJava;
    }

    public void setNoExistJava(boolean noExistJava) {
        this.noExistJava = noExistJava;
    }

    public boolean isNoExistFlash() {
        return noExistFlash;
    }

    public boolean isNoExistSilverlight() {
        return noExistSilverlight;
    }

    public void setNoExistSilverlight(boolean noExistSilverlight) {
        this.noExistSilverlight = noExistSilverlight;
    }

    public void setNoExistFlash(boolean noExistFlash) {
        this.noExistFlash = noExistFlash;
    }

    public boolean validFlash() {
        return validFlash;
    }

    public void setValidFlash(boolean validFlash) {
        this.validFlash = validFlash;
    }
}
