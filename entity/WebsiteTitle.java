package cu.redcuba.entity;

import com.fasterxml.jackson.annotation.JsonView;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import cu.redcuba.controller.api.views.ApiView;
import org.hibernate.annotations.*;
import org.hibernate.type.StandardBasicTypes;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "d_website_title")
@TypeDef(name = "json", typeClass = JsonStringType.class)
@DynamicInsert
@DynamicUpdate
@NamedQueries({
        @NamedQuery(name = "WebsiteTitle.findAll", query = "SELECT wd FROM WebsiteTitle wd")
})
public class WebsiteTitle implements  Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected WebsiteTitlePK pk;

    @Basic(optional = false)
    @Type(type = "json")
    @Column(name = "title", columnDefinition = "json")
    private String title;

    @Basic(optional = false)
    @Column(name = "length")
    private int length;

    @Column(name = "created", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    public WebsiteTitle() {
    }

    public WebsiteTitle(WebsiteTitlePK pk) {
        this.pk = pk;
    }

    public WebsiteTitle(WebsiteTitlePK pk, String title, int length) {
        this.pk = pk;
        this.title = title;
        this.length = title.length();
    }

    public WebsiteTitle(Long websiteId, String day, String title, int length) {
        this.pk = new WebsiteTitlePK(websiteId, day);
        this.title = title;
        this.length = title.length();
    }

    @JsonView(ApiView.class)
    public String getDay() {
        return pk.getDay();
    }

    public WebsiteTitlePK getPk() {
        return pk;
    }

    public void setPk(WebsiteTitlePK pk) {
        this.pk = pk;
    }

    @JsonView(ApiView.class)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.length = title.length();
    }

    @JsonView(ApiView.class)
    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (pk != null ? pk.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof WebsiteTitle)) {
            return false;
        }
        WebsiteTitle other = (WebsiteTitle) object;
        return (this.pk != null || other.pk == null) &&
                (this.pk == null || this.pk.equals(other.pk));
    }

    @Override
    public String toString() {
        return "cu.redcuba.entity.WebsiteTitle[ pk=" + pk + " ]";
    }
}