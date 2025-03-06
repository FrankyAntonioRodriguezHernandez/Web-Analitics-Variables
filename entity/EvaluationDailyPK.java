package cu.redcuba.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Embeddable
public class EvaluationDailyPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "website_id")
    private long websiteId;
    @Basic(optional = false)
    @Column(name = "variable_id")
    private long variableId;
    @Basic(optional = false)
    @Column(name = "indicator_id")
    private long indicatorId;
    @Basic(optional = false)
    @Column(name = "day")
    @Temporal(TemporalType.DATE)
    private Date day;

    public EvaluationDailyPK() {
    }

    public EvaluationDailyPK(long websiteId, long variableId, long indicatorId, Date day) {
        this.websiteId = websiteId;
        this.variableId = variableId;
        this.indicatorId = indicatorId;
        this.day = day;
    }

    public long getWebsiteId() {
        return websiteId;
    }

    public void setWebsiteId(long websiteId) {
        this.websiteId = websiteId;
    }

    public long getVariableId() {
        return variableId;
    }

    public void setVariableId(long variableId) {
        this.variableId = variableId;
    }
    
    public long getIndicatorId() {
        return indicatorId;
    }

    public void setIndicatorId(long indicatorId) {
        this.indicatorId = indicatorId;
    }

    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) websiteId;
        hash += (int) variableId;
        hash += (int) indicatorId;
        hash += (day != null ? day.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof EvaluationDailyPK)) {
            return false;
        }
        EvaluationDailyPK other = (EvaluationDailyPK) object;
        if (this.websiteId != other.websiteId) {
            return false;
        }
        if (this.variableId != other.variableId) {
            return false;
        }
        if (this.indicatorId != other.indicatorId) {
            return false;
        }
        return !((this.day == null && other.day != null) || (this.day != null && !this.day.equals(other.day)));
    }

    @Override
    public String toString() {
        return "cu.redcuba.entity.EvaluationDailyPK[ websiteId=" + websiteId + ", variableId=" + variableId + ", indicatorId=" + indicatorId + ", day=" + day + " ]";
    }

}
