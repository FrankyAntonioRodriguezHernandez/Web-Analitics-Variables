package cu.redcuba.entity;

import com.fasterxml.jackson.annotation.JsonView;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import cu.redcuba.controller.api.views.ApiView;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "d_website_emails")
@TypeDef(name = "json", typeClass = JsonStringType.class)
@DynamicInsert
@DynamicUpdate
@IdClass(WebsiteEmailsPK.class)
public class WebsiteEmails {
    private long websiteId;
    private Date day;
    private List<String> emails;
    private Date created;

    public WebsiteEmails() {
    }

    public WebsiteEmails(long websiteId, Date day, List<String> emails) {
        this.websiteId = websiteId;
        this.day = day;
        this.emails = emails;
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

    @Basic
    @Type(type = "json")
    @Column(name = "emails", nullable = false, columnDefinition = "json")
    @Convert(disableConversion = true)
    @JsonView(ApiView.class)
    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
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
        WebsiteEmails that = (WebsiteEmails) o;
        return Objects.equals(websiteId, that.websiteId) &&
                Objects.equals(day, that.day) &&
                Objects.equals(emails, that.emails) &&
                Objects.equals(created, that.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(websiteId, day, emails, created);
    }
}
