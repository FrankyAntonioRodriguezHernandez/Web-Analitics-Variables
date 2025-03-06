package cu.redcuba.entity;

import com.fasterxml.jackson.annotation.JsonView;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import cu.redcuba.controller.api.views.ApiView;
import cu.redcuba.model.JsItem;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "d_website_js")
@TypeDef(name = "json", typeClass = JsonStringType.class)
@DynamicInsert
@DynamicUpdate
@IdClass(WebsiteJSPK.class)
public class WebsiteJS {
    private long websiteId;
    private Date day;
    private List<JsItem> js;
    private Date created;

    public WebsiteJS() {
    }

    public WebsiteJS(long websiteId, Date day, List<JsItem> js) {
        this.websiteId = websiteId;
        this.day = day;
        this.js = js;
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
    @Column(name = "js", nullable = false, columnDefinition = "json")
    @Convert(disableConversion = true)
    @JsonView(ApiView.class)
    public List<JsItem> getJs() {
        return js;
    }

    public void setJs(List<JsItem> js) {
        this.js = js;
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
        WebsiteJS that = (WebsiteJS) o;
        return Objects.equals(websiteId, that.websiteId) &&
                Objects.equals(day, that.day) &&
                Objects.equals(js, that.js) &&
                Objects.equals(created, that.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(websiteId, day, js, created);
    }
}
