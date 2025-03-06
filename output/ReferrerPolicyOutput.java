package cu.redcuba.output;

public class ReferrerPolicyOutput extends Output{

    private boolean hasHeaderRP = false;
    private boolean hasParamSameOrigin = false;
    private boolean hasParamNoReferrer = false;
    private boolean hasParamStrictOrigin = false;
    private boolean hasParamNoRWD = false;
    

    public boolean hasHeaderRP(){
        return hasHeaderRP;
    }

    public void setHasHeaderRP(boolean hasHeaderRP) {
        this.hasHeaderRP = hasHeaderRP;
    }

    public boolean hasParamSameOrigin(){
        return hasParamSameOrigin;
    }

    public void setHasParamSameOrigin(boolean hasParamSameOrigin) {
        this.hasParamSameOrigin = hasParamSameOrigin;
    }

    public boolean hasParamNoReferrer(){
        return hasParamNoReferrer;
    }

    public void setHasParamNoReferrer(boolean hasParamNoReferrer) {
        this.hasParamNoReferrer = hasParamNoReferrer;
    }
    
    public boolean hasParamStrictOrigin(){
        return hasParamStrictOrigin;
    }

    public void setHasParamStrictOrigin(boolean hasParamStrictOrigin) {
        this.hasParamStrictOrigin = hasParamStrictOrigin;
    }
    
    public boolean hasParamNoRWD(){
        return hasParamNoRWD;
    }

    public void setHasParamNoRWD(boolean hasParamNoRWD) {
        this.hasParamNoRWD = hasParamNoRWD;
    }

    @Override
    public String toString() {
        return "ReferrerPolicyOutput{" +
                "hasHeaderRP=" + hasHeaderRP +
                ", hasParamSameOrigin=" + hasParamSameOrigin +
                ", hasParamNoReferrer=" + hasParamNoReferrer +
                ", hasParamStrictOrigin=" + hasParamStrictOrigin +
                ", hasParamNoRWD=" + hasParamNoRWD +
                '}';
    }
}
