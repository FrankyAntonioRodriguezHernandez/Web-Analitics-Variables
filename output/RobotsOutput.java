package cu.redcuba.output;

import java.util.List;

public class RobotsOutput extends Output {

    private boolean exist;

    private boolean plainText;

    private List<String> sitemaps;

    public boolean exist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }

    public boolean isPlainText() {
        return plainText;
    }

    public void setPlainText(boolean plainText) {
        this.plainText = plainText;
    }

    public List<String> getSitemaps() {
        return sitemaps;
    }

    public void setSitemaps(List<String> sitemaps) {
        this.sitemaps = sitemaps;
    }

}
