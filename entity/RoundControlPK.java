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
public class RoundControlPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "round")
    private String round;
    @Basic(optional = false)
    @Column(name = "type")
    private String type;

    public RoundControlPK() {
    }

    public RoundControlPK(String round, String type) {
        this.round = round;
        this.type = type;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (round != null ? round.hashCode() : 0);
        hash += (type != null ? type.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RoundControlPK)) {
            return false;
        }
        RoundControlPK other = (RoundControlPK) object;
        if ((this.round == null && other.round != null) || (this.round != null && !this.round.equals(other.round))) {
            return false;
        }
        if ((this.type == null && other.type != null) || (this.type != null && !this.type.equals(other.type))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "rc.websites.evaluator.entity.RoundControlPK[ round=" + round + ", type=" + type + " ]";
    }
    
}
