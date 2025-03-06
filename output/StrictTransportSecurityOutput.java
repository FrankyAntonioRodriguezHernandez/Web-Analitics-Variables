package cu.redcuba.output;

public class StrictTransportSecurityOutput extends Output{

    private boolean hasHeaderHSTS = false;
    private boolean hasParamPreload = false;
    private boolean hasParamMaxAge = false;
    private boolean hasCorrectMaxAge = false;

    public boolean hasHeaderHSTS(){
        return hasHeaderHSTS;
    }

    public void setHasHeaderHSTS(boolean hasHeaderHSTS) {
        this.hasHeaderHSTS = hasHeaderHSTS;
    }

    public boolean hasParamPreload(){
        return hasParamPreload;
    }

    public void setHasParamPreload(boolean hasParamPreload) {
        this.hasParamPreload = hasParamPreload;
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
        return "StrictTransportSecurityOutput{" +
                "hasHeaderHSTS=" + hasHeaderHSTS +
                ", hasParamPreload=" + hasParamPreload +
                ", hasParamMaxAge=" + hasParamMaxAge +
                ", hasCorrectMaxAge=" + hasCorrectMaxAge +
                '}';
    }
}
