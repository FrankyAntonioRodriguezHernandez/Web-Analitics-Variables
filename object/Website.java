package cu.redcuba.object;

import java.io.Serializable;

public class Website implements Serializable {

    private Long id;
    private String hostname;

    public Website() {
    }

    public Website(Long id, String hostname) {
        this.id = id;
        this.hostname = hostname;
    }

    public Long getId() {
        return id;
    }

    public String getHostname() {
        return hostname;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public boolean isInternational() {
        return !hostname.endsWith(".cu");
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Website)) {
            return false;
        }
        Website other = (Website) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cu.redcuba.object.Website[ id=" + id + " ]";
    }

}
