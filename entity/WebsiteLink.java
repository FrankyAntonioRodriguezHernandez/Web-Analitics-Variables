package cu.redcuba.entity;

import com.fasterxml.jackson.annotation.JsonView;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import cu.redcuba.controller.api.views.ApiView;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "d_website_links")
@TypeDef(name = "json", typeClass = JsonStringType.class)
@DynamicInsert
@DynamicUpdate
@NamedQueries({
        @NamedQuery(name = "WebsiteLink.findAll", query = "SELECT wl FROM WebsiteLink wl")
})
public class WebsiteLink implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected WebsiteLinkPK pk;

    @Basic(optional = false)
    @Type(type = "json")
    @Column(name = "links", columnDefinition = "json")
    private List<String> links;

    @Column(name = "created", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    public WebsiteLink() {}

    public WebsiteLink(WebsiteLinkPK pk) {
        this.pk = pk;
    }

    public WebsiteLink(WebsiteLinkPK pk, List<String> links) {
        this.pk = pk;
        this.links = links;
    }

    public WebsiteLink(Long websiteId, String day, List<String> links) {
        this.pk = new WebsiteLinkPK(websiteId, day);
        this.links = links;
    }

    @JsonView(ApiView.class)
    public String getDay() {
        return pk.getDay();
    }

    public WebsiteLinkPK getPk() {
        return pk;
    }

    public void setPk(WebsiteLinkPK pk) {
        this.pk = pk;
    }

    @JsonView(ApiView.class)
    public List<String> getLinks() {
        return links;
    }

    public void setLinks(List<String> links) {
        this.links = links;
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
        if (!(object instanceof WebsiteLink)) {
            return false;
        }
        WebsiteLink other = (WebsiteLink) object;
        return (this.pk != null || other.pk == null) &&
                (this.pk == null || this.pk.equals(other.pk));
    }

    @Override
    public String toString() {
        return "cu.redcuba.entity.WebsiteLink[ pk=" + pk + " ]";
    }
}
