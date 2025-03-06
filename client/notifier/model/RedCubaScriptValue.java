package cu.redcuba.client.notifier.model;

import java.util.Date;

public class RedCubaScriptValue {

    private Date day;
    private long websiteId;
    private String websiteHost;
    private float evaluation;

    public RedCubaScriptValue(Date day, long websiteId, String websiteHost, float evaluation) {
        this.day = day;
        this.websiteId = websiteId;
        this.websiteHost = websiteHost;
        this.evaluation = evaluation;
    }

    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    public long getWebsiteId() {
        return websiteId;
    }

    public void setWebsiteId(long websiteId) {
        this.websiteId = websiteId;
    }

    public String getWebsiteHost() {
        return websiteHost;
    }

    public void setWebsiteHost(String websiteHost) {
        this.websiteHost = websiteHost;
    }

    public float getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(float evaluation) {
        this.evaluation = evaluation;
    }

}
