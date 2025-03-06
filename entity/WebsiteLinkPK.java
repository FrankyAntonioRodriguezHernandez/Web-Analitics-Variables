package cu.redcuba.entity;

import com.fasterxml.jackson.annotation.JsonView;
import cu.redcuba.controller.api.views.ApiView;

import javax.persistence.Basic;
import javax.persistence.Column;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class WebsiteLinkPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "website_id")
    private long websiteId;
    @Basic(optional = false)
    @Column(name = "day")
    private String day;

    // Constructor público con argumentos
    public WebsiteLinkPK(Long websiteId, String day) {
        this.websiteId = websiteId;
        this.day = day;
    }

    public WebsiteLinkPK() {}

    @JsonView(ApiView.class)
    public Long getWebsiteId() {
        return websiteId;
    }

    public void setWebsiteId(Long websiteId) {
        this.websiteId = websiteId;
    }

    @JsonView(ApiView.class)
    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebsiteLinkPK that = (WebsiteLinkPK) o;
        return Objects.equals(websiteId, that.websiteId) &&
                Objects.equals(day, that.day);
    }

    @Override
    public int hashCode() {
        return Objects.hash(websiteId, day);
    }
}
