package cu.redcuba.output;

public class CharsetOutput extends Output {

    private boolean exist;

    private boolean valid;

    private boolean recommended;

//    private boolean existMetaHttpEquivContentType;
//
//    private boolean validMetaHttpEquivContentType;

    public boolean exist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isRecommended() {
        return recommended;
    }

    public void setRecommended(boolean recommended) {
        this.recommended = recommended;
    }

//    public boolean existMetaHttpEquivContentType() {
//        return existMetaHttpEquivContentType;
//    }
//
//    public void setExistMetaHttpEquivContentType(boolean existMetaHttpEquivContentType) {
//        this.existMetaHttpEquivContentType = existMetaHttpEquivContentType;
//    }
//
//    public boolean isValidMetaHttpEquivContentType() {
//        return validMetaHttpEquivContentType;
//    }
//
//    public void setValidMetaHttpEquivContentType(boolean validMetaHttpEquivContentType) {
//        this.validMetaHttpEquivContentType = validMetaHttpEquivContentType;
//    }
}
