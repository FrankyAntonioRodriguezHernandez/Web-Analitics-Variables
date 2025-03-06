/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cu.redcuba.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * @author developer
 */
@Entity
@Table(name = "d_download")
@DynamicInsert
@DynamicUpdate
@NamedQueries({
    @NamedQuery(name = "Download.findAll", query = "SELECT d FROM Download d")
})
public class Download implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @Column(name = "downloaded")
    @Temporal(TemporalType.DATE)
    private Date downloaded;
    @Basic(optional = false)
    @Column(name = "url_hash")
    private String urlHash;
    @Basic(optional = false)
    @Lob
    @Column(name = "url")
    private String url;
    @Basic(optional = false)
    @Column(name = "hostname")
    private String hostname;
    @Basic(optional = false)
    @Column(name = "code")
    private short code;
    @Basic(optional = false)
    @Column(name = "elapsed_time")
    private long elapsedTime;
    @Column(name = "content_encoding")
    private String contentEncoding;
    @Column(name = "content_type")
    private String contentType;
    @Column(name = "content_length")
    private Long contentLength;
    @Column(name = "cache_control")
    private String cacheControl;
    @Column(name = "x_frame_options")
    private String xFrameOptions;
    @Column(name = "x_content_type_options")
    private String xContentTypeOptions;
    @Column(name = "x_xss_protection")
    private String xXssProtection;
    @Column(name = "created", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @Column(name = "modified", insertable = false, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;
    @Lob
    @Column(name = "content")
    private String content;
    @Lob
    @Column(name = "last_url")
    private String lastUrl;

    public Download() {
    }

    public Download(Long id) {
        this.id = id;
    }

    public Download(Long id, Date downloaded, String urlHash, String url, String hostname, short code) {
        this.id = id;
        this.downloaded = downloaded;
        this.urlHash = urlHash;
        this.url = url;
        this.hostname = hostname;
        this.code = code;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(Date downloaded) {
        this.downloaded = downloaded;
    }

    public String getUrlHash() {
        return urlHash;
    }

    public void setUrlHash(String urlHash) {
        this.urlHash = urlHash;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public short getCode() {
        return code;
    }

    public void setCode(short code) {
        this.code = code;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentEncoding() {
        return contentEncoding;
    }

    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getContentLength() {
        return contentLength;
    }

    public void setContentLength(Long contentLength) {
        this.contentLength = contentLength;
    }

    public String getCacheControl() {
        return cacheControl;
    }

    public void setCacheControl(String cacheControl) {
        this.cacheControl = cacheControl;
    }

    public String getXFrameOptions() {
        return xFrameOptions;
    }

    public void setXFrameOptions(String xFrameOptions) {
        this.xFrameOptions = xFrameOptions;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public String getLastUrl() {
        return lastUrl;
    }

    public void setLastUrl(String lastUrl) {
        this.lastUrl = lastUrl;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public String getXContentTypeOptions() { return xContentTypeOptions; }

    public void setXContentTypeOptions(String xContentTypeOptions) { this.xContentTypeOptions = xContentTypeOptions;}


    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Download)) {
            return false;
        }
        Download other = (Download) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "rc.websites.evaluator.entity.Download[ id=" + id + " ]";
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getxFrameOptions() {
        return xFrameOptions;
    }

    public void setxFrameOptions(String xFrameOptions) {
        this.xFrameOptions = xFrameOptions;
    }

    public String getxContentTypeOptions() {
        return xContentTypeOptions;
    }

    public void setxContentTypeOptions(String xContentTypeOptions) {
        this.xContentTypeOptions = xContentTypeOptions;
    }

    public String getxXssProtection() {
        return xXssProtection;
    }

    public void setxXssProtection(String xXssProtection) {
        this.xXssProtection = xXssProtection;
    }
}
