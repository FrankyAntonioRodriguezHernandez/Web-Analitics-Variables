/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cu.redcuba.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
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
@Table(name = "d_request_header")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RequestHeader.findAll", query = "SELECT r FROM RequestHeader r")
    , @NamedQuery(name = "RequestHeader.findByRequestId", query = "SELECT r FROM RequestHeader r WHERE r.requestHeaderPK.requestId = :requestId")
    , @NamedQuery(name = "RequestHeader.findByHeaderName", query = "SELECT r FROM RequestHeader r WHERE r.requestHeaderPK.headerName = :headerName")
})
public class RequestHeader implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected RequestHeaderPK requestHeaderPK;
    @Basic(optional = false)
    @Lob
    @Column(name = "header_value")
    private String headerValue;

    public RequestHeader() {
    }

    public RequestHeader(RequestHeaderPK requestHeaderPK) {
        this.requestHeaderPK = requestHeaderPK;
    }

    public RequestHeader(RequestHeaderPK requestHeaderPK, String headerValue) {
        this.requestHeaderPK = requestHeaderPK;
        this.headerValue = headerValue;
    }

    public RequestHeader(long requestId, String headerName) {
        this.requestHeaderPK = new RequestHeaderPK(requestId, headerName);
    }

    public RequestHeaderPK getRequestHeaderPK() {
        return requestHeaderPK;
    }

    public void setRequestHeaderPK(RequestHeaderPK requestHeaderPK) {
        this.requestHeaderPK = requestHeaderPK;
    }

    public String getHeaderValue() {
        return headerValue;
    }

    public void setHeaderValue(String headerValue) {
        this.headerValue = headerValue;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (requestHeaderPK != null ? requestHeaderPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RequestHeader)) {
            return false;
        }
        RequestHeader other = (RequestHeader) object;
        if ((this.requestHeaderPK == null && other.requestHeaderPK != null) || (this.requestHeaderPK != null && !this.requestHeaderPK.equals(other.requestHeaderPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "rc.websites.evaluator.entity.RequestHeader[ requestHeaderPK=" + requestHeaderPK + " ]";
    }
    
}
