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
@Table(name = "d_website_keywords")
@TypeDef(name = "json", typeClass = JsonStringType.class)
@DynamicInsert
@DynamicUpdate
@NamedQueries({
    @NamedQuery(name = "WebsiteKeywords.findAll", query = "SELECT wk FROM WebsiteKeywords wk")
})
public class WebsiteKeywords implements Serializable {


    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected WebsiteKeywordsPK pk;

    @Basic(optional = false)
    @Type( type = "json" )
    @Column(name = "keywords", columnDefinition = "json" )
    private List<String> keywords;

    @Column(name = "created", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    public WebsiteKeywords() {
    }

    public WebsiteKeywords(WebsiteKeywordsPK pk) {
        this.pk = pk;
    }

    public WebsiteKeywords(WebsiteKeywordsPK pk, List<String> keywords) {
        this.pk = pk;
        this.keywords = keywords;
    }

    public WebsiteKeywords(long websiteId, String day) {
        this.pk = new WebsiteKeywordsPK(websiteId, day);
    }

    public WebsiteKeywords(long websiteId, String day, List<String> keywords) {
        this.pk = new WebsiteKeywordsPK(websiteId, day);
        this.keywords = keywords;
    }

    @JsonView(ApiView.class)
    public String getDay() {
        return pk.getDay();
    }

    public WebsiteKeywordsPK getPk() {
        return pk;
    }

    public void setPk(WebsiteKeywordsPK pk) {
        this.pk = pk;
    }

    @JsonView(ApiView.class)
    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
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
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WebsiteKeywords)) {
            return false;
        }
        WebsiteKeywords other = (WebsiteKeywords) object;
        return (this.pk != null || other.pk == null) && (this.pk == null || this.pk.equals(other.pk));
    }

    @Override
    public String toString() {
        return "cu.redcuba.entity.WebsiteKeywords[ pk=" + pk + " ]";
    }

}
