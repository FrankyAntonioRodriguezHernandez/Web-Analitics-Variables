package cu.redcuba.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import cu.redcuba.controller.api.views.ApiView;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "d_variable")
public class Variable implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @Column(name = "slug")
    private String slug;
    @Basic(optional = true)
    @Column(name = "informative")
    private Boolean informative;
    @Basic(optional = true)
    @Column(name = "data")
    private Boolean data;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @Column(name = "description")
    private String description;
    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @Column(name = "possition")
    private Short possition;
    @JoinColumn(name = "complexity_id", referencedColumnName = "id")
    @ManyToOne
    private VariableComplexity complexity;
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    @ManyToOne
    private VariableGroup group;
    @JoinColumn(name = "impact_id", referencedColumnName = "id")
    @ManyToOne
    private VariableImpact impact;
    @OneToMany(mappedBy = "variable")
    private List<VariableIndicator> indicators;

    public Variable() {
    }

    public Variable(Long id) {
        this.id = id;
    }

    public Variable(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("slug")
    @JsonView(ApiView.class)
    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    @JsonProperty("name")
    @JsonView(ApiView.class)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("description")
    @JsonView(ApiView.class)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Short getPossition() {
        return possition;
    }

    public void setPossition(Short possition) {
        this.possition = possition;
    }

    @JsonView(ApiView.class)
    public VariableComplexity getComplexity() {
        return complexity;
    }

    public String getComplexityString() {
        return complexity != null ? complexity.getComplexity() : null;
    }

    public void setComplexityId(VariableComplexity complexity) {
        this.complexity = complexity;
    }

    public VariableGroup getGroup() {
        return group;
    }

    @JsonProperty("group_id")
    @JsonView(ApiView.class)
    public Long getGroupId() {
        return group != null ? group.getId() : null;
    }

    public void setGroup(VariableGroup group) {
        this.group = group;
    }

    @JsonView(ApiView.class)
    public VariableImpact getImpact() {
        return impact;
    }

    public String getImpactString() {
        return impact != null ? impact.getImpact() : null;
    }

    public void setImpact(VariableImpact impact) {
        this.impact = impact;
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
        if (!(object instanceof Variable)) {
            return false;
        }
        Variable other = (Variable) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @JsonProperty("informative")
    @JsonView(ApiView.class)
    public Boolean getInformative() {
        return informative;
    }

    public void setInformative(Boolean informative) {
        this.informative = informative;
    }

    @JsonProperty("data")
    @JsonView(ApiView.class)
    public Boolean getData() {
        return data;
    }

    public Variable setData(Boolean data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return "cu.redcuba.entity.Variable[ id=" + id + " ]";
    }

    @JsonProperty("indicators")
    @JsonView(ApiView.class)
    public List<VariableIndicator> getIndicators() {
        return indicators;
    }

    public void setIndicators(List<VariableIndicator> indicators) {
        this.indicators = indicators;
    }

}
