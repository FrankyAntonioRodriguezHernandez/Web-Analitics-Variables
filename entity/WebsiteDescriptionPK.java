package cu.redcuba.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Embeddable
public class WebsiteDescriptionPK implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "website_id", nullable = false)
        protected Long websiteId;

        @Column(name = "day", nullable = false)
        protected String day;

    public WebsiteDescriptionPK() {}

    public WebsiteDescriptionPK(Long websiteId, String day) {
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
        if (!(object instanceof WebsiteDescriptionPK)) {
            return false;
        }
        WebsiteDescriptionPK other = (WebsiteDescriptionPK) object;
        return (this.websiteId != null || other.websiteId == null) &&
                (this.websiteId == null || this.websiteId.equals(other.websiteId)) &&
                (this.day != null || other.day == null) &&
                (this.day == null || this.day.equals(other.day));
    }

    
}
