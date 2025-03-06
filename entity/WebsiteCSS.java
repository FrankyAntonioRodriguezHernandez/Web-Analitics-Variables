package cu.redcuba.entity;

import com.fasterxml.jackson.annotation.JsonView;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import cu.redcuba.controller.api.views.ApiView;
import cu.redcuba.model.CssItem;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "d_website_css")
@TypeDef(name = "json", typeClass = JsonStringType.class)
@DynamicInsert
@DynamicUpdate
@IdClass(WebsiteCssPK.class)
public class WebsiteCSS {
    private long websiteId;
    private Date day;
    private List<CssItem> css;
    private Date created;

    public WebsiteCSS() {
    }

    public WebsiteCSS(long websiteId, Date day, List<CssItem> css) {
        this.websiteId = websiteId;
        this.day = day;
        this.css = css;
    }

    @Id
    @Column(name = "website_id", nullable = false)
    public long getWebsiteId() {
        return websiteId;
    }

    public void setWebsiteId(long websiteId) {
        this.websiteId = websiteId;
    }

    @Id
    @Column(name = "day", nullable = false)
    @JsonView(ApiView.class)
    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    @Basic(optional = false)
    @Type(type = "json")
    @Column(name = "css", nullable = false, columnDefinition = "json")
    @Convert(disableConversion = true)
    @JsonView(ApiView.class)
    public List<CssItem> getCss() {
        return css;
    }

    public void setCss(List<CssItem> css) {
        this.css = css;
    }

    @Basic
    @Column(name = "created", nullable = false, insertable = false, updatable = false)
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebsiteCSS that = (WebsiteCSS) o;
        return Objects.equals(websiteId, that.websiteId) &&
                Objects.equals(day, that.day) &&
                Objects.equals(css, that.css) &&
                Objects.equals(created, that.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(websiteId, day, css, created);
    }
}
