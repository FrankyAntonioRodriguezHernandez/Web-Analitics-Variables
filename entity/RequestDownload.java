/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cu.redcuba.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author developer
 */
@Entity
@Table(name = "d_request_download")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RequestDownload.findAll", query = "SELECT r FROM RequestDownload r") 
    , @NamedQuery(name = "RequestDownload.findByRequestId", query = "SELECT r FROM RequestDownload r WHERE r.requestId = :requestId")
})
public class RequestDownload implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "request_id")
    private Long requestId;
    @Basic(optional = false)
    @Lob
    @Column(name = "download_path")
    private String downloadPath;

    public RequestDownload() {
    }

    public RequestDownload(Long requestId) {
        this.requestId = requestId;
    }

    public RequestDownload(Long requestId, String downloadPath) {
        this.requestId = requestId;
        this.downloadPath = downloadPath;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (requestId != null ? requestId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RequestDownload)) {
            return false;
        }
        RequestDownload other = (RequestDownload) object;
        if ((this.requestId == null && other.requestId != null) || (this.requestId != null && !this.requestId.equals(other.requestId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "rc.websites.evaluator.entity.RequestDownload[ requestId=" + requestId + " ]";
    }
    
}
