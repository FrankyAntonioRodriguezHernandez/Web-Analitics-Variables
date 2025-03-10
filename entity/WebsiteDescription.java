import com.fasterxml.jackson.annotation.JsonView;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import cu.redcuba.controller.api.views.ApiView;
import org.hibernate.annotations.*;
import org.hibernate.type.StandardBasicTypes;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "d_website_description")
@TypeDef(name = "json", typeClass = JsonStringType.class)
@DynamicInsert
@DynamicUpdate
@NamedQueries({
    @NamedQuery(name = "WebsiteDescription.findAll", query = "SELECT wd FROM WebsiteDescription wd")
})
public class WebsiteDescription implements Serializable {

private static final long serialVersionUID = 1L;

@EmbeddedId
protected WebsiteDescriptionPK pk;

@Basic(optional = false)
@Type(type = "json")
@Column(name = "description", columnDefinition = "json")
private String description;

@Basic(optional = false)
@Column(name = "length")
private int length;

@Column(name = "created", insertable = false, updatable = false)
@Temporal(TemporalType.TIMESTAMP)
private Date created;

public WebsiteDescription() {}

public WebsiteDescription(WebsiteDescriptionPK pk) {
    this.pk = pk;
}

public WebsiteDescription(WebsiteDescriptionPK pk, String description, int length) {
    this.pk = pk;
    this.description = description;
    this.length = description.length();
}

public WebsiteDescription(Long websiteId, String day, String description, int length) {
    this.pk = new WebsiteDescriptionPK(websiteId, day);
    this.description = description;
    this.length = description.length();
}

public String getDay() {
    return pk.getDay();
}

public WebsiteDescriptionPK getPk() {
    return pk;
}

public void setPk(WebsiteDescriptionPK pk) {
    this.pk = pk;
}
public String getDescription() {
    return description;
}
public void setDescription(String description) {
    this.description = description;
    this.length = description.length();
}
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
    if (!(object instanceof WebsiteDescription)) {
        return false;
    }
    WebsiteDescription other = (WebsiteDescription) object;
    return (this.pk != null || other.pk == null) &&
            (this.pk == null || this.pk.equals(other.pk));
}

@Override
public String toString() {
    return "cu.redcuba.entity.WebsiteDescription[ pk=" + pk + " ]";
}
}