package cu.redcuba.output;

public class SubresourceIntegrityOutput extends Output{

    private int countExtScript;
    private boolean notHasIntegrityScript;

    public int getCountExtScript(){
        return countExtScript;
    }

    public void setCountExtScript(int countExtScript){
        this.countExtScript = countExtScript;
    }

    public boolean notHasIntegrityScript(){
        return notHasIntegrityScript;
    }

    public void setNotHasIntegrityScript(boolean notHasIntegrityScript) {
        this.notHasIntegrityScript = notHasIntegrityScript;
    }
    

    @Override
    public String toString() {
        return "SubresourceIntegrityOutput{" +
                "countExtScript=" + countExtScript +
                ", notHasIntegrityScript=" + notHasIntegrityScript +
                '}';
    }
}


