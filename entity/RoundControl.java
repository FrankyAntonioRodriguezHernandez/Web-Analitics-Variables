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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 *
 * @author developer
 */
@Entity
@Table(name = "d_round_control")
@DynamicInsert
@DynamicUpdate
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RoundControl.findAll", query = "SELECT r FROM RoundControl r")
    , @NamedQuery(name = "RoundControl.findByRound", query = "SELECT r FROM RoundControl r WHERE r.roundControlPK.round = :round")
    , @NamedQuery(name = "RoundControl.findByType", query = "SELECT r FROM RoundControl r WHERE r.roundControlPK.type = :type")
    , @NamedQuery(name = "RoundControl.findByItemsSize", query = "SELECT r FROM RoundControl r WHERE r.itemsSize = :itemsSize")
    , @NamedQuery(name = "RoundControl.findByItemsProcessed", query = "SELECT r FROM RoundControl r WHERE r.itemsProcessed = :itemsProcessed")
    , @NamedQuery(name = "RoundControl.findByCreated", query = "SELECT r FROM RoundControl r WHERE r.created = :created")
    , @NamedQuery(name = "RoundControl.findByModified", query = "SELECT r FROM RoundControl r WHERE r.modified = :modified")
})
public class RoundControl implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected RoundControlPK roundControlPK;
    @Basic(optional = false)
    @Column(name = "items_size")
    private int itemsSize;
    @Basic(optional = false)
    @Column(name = "items_processed")
    private int itemsProcessed;
    @Column(name = "created", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @Column(name = "modified", insertable = false, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    public RoundControl() {
    }

    public RoundControl(RoundControlPK roundControlPK) {
        this.roundControlPK = roundControlPK;
    }

    public RoundControl(RoundControlPK roundControlPK, int itemsSize, int itemsProcessed) {
        this.roundControlPK = roundControlPK;
        this.itemsSize = itemsSize;
        this.itemsProcessed = itemsProcessed;
    }

    public RoundControl(String round, String type) {
        this.roundControlPK = new RoundControlPK(round, type);
    }
    
    public RoundControl(String round, String type, int itemsSize, int itemsProcessed) {
        this.roundControlPK = new RoundControlPK(round, type);
        this.itemsSize = itemsSize;
        this.itemsProcessed = itemsProcessed;
    }

    public RoundControlPK getRoundControlPK() {
        return roundControlPK;
    }

    public void setRoundControlPK(RoundControlPK roundControlPK) {
        this.roundControlPK = roundControlPK;
    }

    public int getItemsSize() {
        return itemsSize;
    }

    public void setItemsSize(int itemsSize) {
        this.itemsSize = itemsSize;
    }

    public int getItemsProcessed() {
        return itemsProcessed;
    }

    public void setItemsProcessed(int itemsProcessed) {
        this.itemsProcessed = itemsProcessed;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roundControlPK != null ? roundControlPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RoundControl)) {
            return false;
        }
        RoundControl other = (RoundControl) object;
        if ((this.roundControlPK == null && other.roundControlPK != null) || (this.roundControlPK != null && !this.roundControlPK.equals(other.roundControlPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "rc.websites.evaluator.entity.RoundControl[ roundControlPK=" + roundControlPK + " ]";
    }
    
}
