package cu.redcuba.output;

public class LanguageOutput extends Output {

    private String declaredLanguage;

    private String detectedLanguage;

    private boolean exist;

    private boolean valid;

    private boolean matchesDetected;

    public String getDeclaredLanguage() {
        return declaredLanguage;
    }

    public void setDeclaredLanguage(String declaredLanguage) {
        this.declaredLanguage = declaredLanguage;
    }

    public String getDetectedLanguage() {
        return detectedLanguage;
    }

    public void setDetectedLanguage(String detectedLanguage) {
        this.detectedLanguage = detectedLanguage;
    }

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

    public boolean matchesDetected() {
        return matchesDetected;
    }

    public void setMatchesDetected(boolean matchesDetected) {
        this.matchesDetected = matchesDetected;
    }
}
