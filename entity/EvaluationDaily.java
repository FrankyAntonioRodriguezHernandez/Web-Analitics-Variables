package cu.redcuba.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import cu.redcuba.controller.api.views.ApiView;
import cu.redcuba.controller.api.views.NotifierApiView;
import cu.redcuba.object.EvaluationValueInterval;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.Date;

@SqlResultSetMapping(
        name = "evaluationValueInterval",
        classes = {
                @ConstructorResult(
                        targetClass = EvaluationValueInterval.class,
                        columns = {
                                @ColumnResult(name = "day_begin", type = Date.class),
                                @ColumnResult(name = "day_end", type = Date.class),
                                @ColumnResult(name = "evaluation", type = float.class),
                                @ColumnResult(name = "rounds", type = int.class),
                                @ColumnResult(name = "days", type = int.class)
                        }
                )
        }
)
@NamedNativeQuery(
        name = "EvaluationDaily.lastEvaluationValueInterval",
        query = "CALL last_evaluation_value_interval(:websiteId, :variableId, :indicatorId)",
        resultSetMapping = "evaluationValueInterval"
)
@Entity
@Table(name = "d_evaluation_daily")
@Transactional
@DynamicInsert
@DynamicUpdate
public class EvaluationDaily implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    private EvaluationDailyPK pk;
    @Column(name = "created", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @Basic(optional = false)
    @Column(name = "evaluation")
    private float evaluation;

    @Basic(optional = false)
    @Column(name = "international")
    private boolean international;

    @Transient
    @JsonProperty("evaluation_string")
    @JsonView(ApiView.class)
    private String evaluationAsString;

    public EvaluationDaily() {
    }

    public EvaluationDaily(EvaluationDailyPK evaluationDailyPK) {
        this.pk = evaluationDailyPK;
    }

    public EvaluationDaily(long websiteId, long variableId, long indicatorId, Date day) {
        this.pk = new EvaluationDailyPK(websiteId, variableId, indicatorId, day);
    }

    public EvaluationDailyPK getEvaluationDailyPK() {
        return pk;
    }

    public void setEvaluationDailyPK(EvaluationDailyPK evaluationDailyPK) {
        this.pk = evaluationDailyPK;
    }

    @JsonProperty("day")
    @JsonView(NotifierApiView.class)
    public Date getDay() {
        return this.pk.getDay();
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @JsonView({ApiView.class, NotifierApiView.class})
    public float getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(float evaluation) {
        this.evaluation = evaluation;
    }

    public void setEvaluationAsString(String evaluationAsString) {
        this.evaluationAsString = evaluationAsString;
    }

    public boolean isInternational() {
        return international;
    }

    public void setInternational(boolean international) {
        this.international = international;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (pk != null ? pk.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof EvaluationDaily)) {
            return false;
        }
        EvaluationDaily other = (EvaluationDaily) object;
        return !((this.pk == null && other.pk != null) || (this.pk != null && !this.pk.equals(other.pk)));
    }

    @Override
    public String toString() {
        return "cu.redcuba.entity.EvaluationDaily[ evaluationDailyPK=" + pk + " ]";
    }

}
