package cu.redcuba.output;

public class SecureProtocolOutput extends Output {

    private boolean answerHttps;


    private boolean redirectionHttpHttps;

    public boolean redirectionHttpHttps() {
        return redirectionHttpHttps;
    }

    public void setRedirectionHttpHttps(boolean redirectionHttpHttps) {
        this.redirectionHttpHttps = redirectionHttpHttps;
    }

    public boolean answerHttps() {

        return answerHttps;
    }

    public void setAnswerHttps(boolean answerHttps) {

        this.answerHttps = answerHttps;
    }
}
