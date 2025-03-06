package cu.redcuba.output;

public class HTML5Output extends Output {

    private boolean hasDocType;

    private boolean includeSemanticTags;

    public boolean hasDoctype() {
        return hasDocType;
    }

    public void setHasDocType(boolean hasDocType) {
        this.hasDocType = hasDocType;
    }

    public boolean includeTags() {
        return includeSemanticTags;
    }

    public void setIncludeSemanticTags(boolean includeSemanticTags) {
        this.includeSemanticTags = includeSemanticTags;
    }

}
