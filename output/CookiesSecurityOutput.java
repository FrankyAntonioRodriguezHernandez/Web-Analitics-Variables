package cu.redcuba.output;

public class CookiesSecurityOutput extends Output{

    private boolean hasHeaderSetCookies;
    private boolean hasParamSecure;
    private boolean hasParamHttpOnly;
    private boolean hasParamMaxAge = false;
    private boolean hasCorrectMaxAge = false;

    public boolean hasHeaderSetCookies(){
        return hasHeaderSetCookies;
    }

    public void setHasHeaderSetCookies(boolean hasHeaderSetCookies) {
        this.hasHeaderSetCookies = hasHeaderSetCookies;
    }

    public boolean hasParamSecure(){
        return hasParamSecure;
    }

    public void setHasParamSecure(boolean hasParamSecure) {
        this.hasParamSecure = hasParamSecure;
    }

    public boolean hasParamHttpOnly(){
        return hasParamHttpOnly;
    }

    public void setHasParamHttpOnly(boolean hasParamHttpOnly) {
        this.hasParamHttpOnly = hasParamHttpOnly;
    }
    
    public boolean hasParamMaxAge(){
        return hasParamMaxAge;
    }

    public void setHasParamMaxAge(boolean hasParamMaxAge) {
        this.hasParamMaxAge = hasParamMaxAge;
    }
    
    public boolean hasCorrectMaxAge(){
        return hasParamMaxAge;
    }

    public void setHasCorrectMaxAge(boolean hasCorrectMaxAge) {
        this.hasCorrectMaxAge = hasCorrectMaxAge;
    }

    @Override
    public String toString() {
        return "SeguridadCookiesOutput{" +
                "hasHeaderSetCookies=" + hasHeaderSetCookies +
                ", hasParamSecure=" + hasParamSecure +
                ", hasParamHttpOnly=" + hasParamHttpOnly +
                ", hasParamMaxAge=" + hasParamMaxAge +
                ", hasCorrectMaxAge=" + hasCorrectMaxAge +
                '}';
    }
}

