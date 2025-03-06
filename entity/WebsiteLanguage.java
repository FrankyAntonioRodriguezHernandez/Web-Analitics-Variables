package cu.redcuba.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "d_website_language")
@DynamicInsert
@DynamicUpdate
@IdClass(WebsiteLanguagePK.class)
public class WebsiteLanguage {
    private long websiteId;
    private Date day;
    private String declaredLanguage;
    private String detectedLanguage;
    private Timestamp created;


    public WebsiteLanguage() {
    }

    public WebsiteLanguage(long websiteId, Date day, String declaredLanguage, String detectedLanguage) {
        this.websiteId = websiteId;
        this.day = day;
        this.declaredLanguage = declaredLanguage;
        this.detectedLanguage = detectedLanguage;
        this.created = new Timestamp(System.currentTimeMillis());;
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
    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    @Basic
    @Column(name = "declared_language", nullable = true, length = 54)
    public String getDeclaredLanguage() {
        return declaredLanguage;
    }

    public void setDeclaredLanguage(String declaredLanguage) {
        this.declaredLanguage = declaredLanguage;
    }

    @Basic
    @Column(name = "detected_language", nullable = true, length = 54)
    public String getDetectedLanguage() {
        return detectedLanguage;
    }

    public void setDetectedLanguage(String detectedLanguage) {
        this.detectedLanguage = detectedLanguage;
    }

    @Basic
    @Column(name = "created", nullable = false)
    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebsiteLanguage that = (WebsiteLanguage) o;
        return Objects.equals(websiteId, that.websiteId) &&
                Objects.equals(day, that.day) &&
                Objects.equals(declaredLanguage, that.declaredLanguage) &&
                Objects.equals(detectedLanguage, that.detectedLanguage) &&
                Objects.equals(created, that.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(websiteId, day, declaredLanguage, detectedLanguage, created);
    }
}
