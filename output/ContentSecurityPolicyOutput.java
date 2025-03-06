package cu.redcuba.output;

public class ContentSecurityPolicyOutput extends Output{

    private boolean hasHeaderCSP;
    private boolean notHasParamUnsafe;
    private boolean hasParamNone;

    public boolean hasHeaderCSP(){
        return hasHeaderCSP;
    }

    public void setHasHeaderCSP(boolean hasHeaderCSP) {
        this.hasHeaderCSP = hasHeaderCSP;
    }

    public boolean notHasParamUnsafe(){
        return notHasParamUnsafe;
    }

    public void setNotHasParamUnsafe(boolean notHasParamUnsafe) {
        this.notHasParamUnsafe = notHasParamUnsafe;
    }
    
    public boolean hasParamNone(){
        return hasParamNone;
    }

    public void setHasParamNone(boolean hasParamNone) {
        this.hasParamNone = hasParamNone;
    }

    @Override
    public String toString() {
        return "ContentSecurityPolicyOutput{" +
                "hasHeaderCSP=" + hasHeaderCSP +
                ", notHasParamUnsafe=" + notHasParamUnsafe +
                "hasParamNone=" + hasParamNone +
                '}';
    }
}