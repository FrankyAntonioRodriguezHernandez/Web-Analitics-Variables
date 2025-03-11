package cu.redcuba.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

public class WebsiteTitlePK implements Serializable{
    private static final long serialVersionUID = 1L;

    @Column(name = "website_id", nullable = false)
    protected Long websiteId;

    @Column(name = "day", nullable = false)
    protected String day;

    public WebsiteTitlePK() {}

    public WebsiteTitlePK(Long websiteId, String day) {
        this.websiteId = websiteId;
        this.day = day;
    }

    public Long getWebsiteId() {
        return websiteId;
    }

    public void setWebsiteId(Long websiteId) {
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
        hash += (websiteId != null ? websiteId.hashCode() : 0);
        hash += (day != null ? day.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof WebsiteTitlePK)) {
            return false;
        }
        WebsiteTitlePK other = (WebsiteTitlePK) object;
        return (this.websiteId != null || other.websiteId == null) &&
                (this.websiteId == null || this.websiteId.equals(other.websiteId)) &&
                (this.day != null || other.day == null) &&
                (this.day == null || this.day.equals(other.day));
    }

    @Override
    public String toString() {
        return "WebsiteTitlePK[ websiteId=" + websiteId + ", day=" + day + " ]";
    }
}
