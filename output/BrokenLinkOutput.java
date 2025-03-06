package cu.redcuba.output;

import java.util.List;

public class BrokenLinkOutput extends Output{


    private List<String> brokenLinks; //lista para guardar los enlaces rotos
    private float percent; //atributo para almacenar el porcentaje de enlaces rotos
    private boolean noBrokenLink; //atributo booleano para indicar si no hay enlaces rotos.


    public List<String> getBrokenLinks() {
        return brokenLinks;
    }

    public void setBrokenLinks(List<String> brokenLinks) {
        this.brokenLinks = brokenLinks;
    }


    public float getPercent(){
       return percent;
   }

   public void setPercent(float percent){

       this.percent= percent;
   }

    public boolean hasNoBrokenLink(){
        return noBrokenLink;
    }

    public void setNoBrokenLink(boolean hasBrokenLink) {
        this.noBrokenLink = hasBrokenLink;
    }

    @Override
    public String toString() {
        return "BrokenLinkOutput{" +
                "percent=" + percent +
                ", hasNoBrokenLink=" + noBrokenLink +
                '}';
    }
}
