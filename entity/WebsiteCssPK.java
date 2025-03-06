package cu.redcuba.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class WebsiteCssPK implements Serializable {
    private long websiteId;
    private Date day;

    @Column(name = "website_id", nullable = false)
    @Id
    public long getWebsiteId() {
        return websiteId;
    }

    public void setWebsiteId(long websiteId) {
        this.websiteId = websiteId;
    }

    @Column(name = "day", nullable = false)
    @Id
    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebsiteCssPK that = (WebsiteCssPK) o;
        return Objects.equals(websiteId, that.websiteId) &&
                Objects.equals(day, that.day);
    }

    @Override
    public int hashCode() {
        return Objects.hash(websiteId, day);
    }
}
