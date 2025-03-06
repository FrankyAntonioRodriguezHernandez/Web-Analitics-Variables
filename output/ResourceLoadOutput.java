package cu.redcuba.output;

public class ResourceLoadOutput extends Output{

    private int countExtResourceLoad;
    private boolean notHasBadResourceLoad;

   public float getCountExtResourceLoad(){
       return countExtResourceLoad;
   }

   public void setCountExtResourceLoad(int countExtResourceLoad){
       this.countExtResourceLoad= countExtResourceLoad;
   }

    public boolean notHasBadResourceLoad(){
        return notHasBadResourceLoad;
    }

    public void setNotHasBadResourceLoad(boolean notHasBadResourceLoad) {
        this.notHasBadResourceLoad = notHasBadResourceLoad;
    }

    @Override
    public String toString() {
        return "ResoueceLoadOutput{" +
                "countExtResourceLoad=" + countExtResourceLoad +
                ", notHasBadResourceLoad=" + notHasBadResourceLoad +
                '}';
    }
}
