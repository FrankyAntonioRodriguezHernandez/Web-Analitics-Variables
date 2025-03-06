/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cu.redcuba.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author developer
 */
@Embeddable
public class RequestHeaderPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "request_id")
    private long requestId;
    @Basic(optional = false)
    @Column(name = "header_name")
    private String headerName;

    public RequestHeaderPK() {
    }

    public RequestHeaderPK(long requestId, String headerName) {
        this.requestId = requestId;
        this.headerName = headerName;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) requestId;
        hash += (headerName != null ? headerName.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RequestHeaderPK)) {
            return false;
        }
        RequestHeaderPK other = (RequestHeaderPK) object;
        if (this.requestId != other.requestId) {
            return false;
        }
        if ((this.headerName == null && other.headerName != null) || (this.headerName != null && !this.headerName.equals(other.headerName))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "rc.websites.evaluator.entity.RequestHeaderPK[ requestId=" + requestId + ", headerName=" + headerName + " ]";
    }
    
}
