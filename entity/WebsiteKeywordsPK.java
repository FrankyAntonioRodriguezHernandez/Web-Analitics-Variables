package cu.redcuba.entity;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
public class WebsiteKeywordsPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "website_id")
    private long websiteId;
    @Basic(optional = false)
    @Column(name = "day")
    private String day;

    public WebsiteKeywordsPK() {
    }

    public WebsiteKeywordsPK(long websiteId, String day) {
        this.websiteId = websiteId;
        this.day = day;
    }

    public long getWebsiteId() {
        return websiteId;
    }

    public void setWebsiteId(long websiteId) {
        this.websiteId = websiteId;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) websiteId;
        hash += (day != null ? day.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof WebsiteKeywordsPK)) {
            return false;
        }

        WebsiteKeywordsPK other = (WebsiteKeywordsPK) object;
        return this.websiteId == other.websiteId && (this.day != null || other.day == null) && (this.day == null || this.day.equals(other.day));

    }

    @Override
    public String toString() {
        return "cu.redcuba.entity.WebsiteKeywordsPK[ websiteId=" + websiteId + ", day=" + day +" ]";
    }

}
