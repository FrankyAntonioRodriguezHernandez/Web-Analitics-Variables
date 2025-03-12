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
public class WebsiteTitle implements  Serializable{
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

    public WebsiteTitle() {}

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
}
