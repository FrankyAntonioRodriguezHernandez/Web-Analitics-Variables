/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cu.redcuba.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import cu.redcuba.controller.api.views.ApiView;

import java.io.Serializable;
import java.math.BigInteger;
import javax.persistence.*;

/**
 *
 * @author developer
 */
@Entity
@Table(name = "d_variable_indicator")
@NamedQueries({
    @NamedQuery(name = "VariableIndicator.findAll", query = "SELECT vi FROM VariableIndicator vi")
})
public class VariableIndicator implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @Column(name = "slug")
    private String slug;
    @Basic(optional = false)
    @Column(name = "indicator")
    private String indicator;
    @Basic(optional = false)
    @Column(name = "description")
    private String description;

    @JoinColumn(name = "variable_id", referencedColumnName = "id")
    @ManyToOne
    private Variable variable;

    public VariableIndicator() {
    }

    public VariableIndicator(Long id) {
        this.id = id;
    }

    public VariableIndicator(Long id, String slug, String indicator, String description) {
        this.id = id;
        this.slug = slug;
        this.indicator = indicator;
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
    public String getIndicator() {
        return indicator;
    }

    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        if (!(object instanceof VariableIndicator)) {
            return false;
        }
        VariableIndicator other = (VariableIndicator) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cu.redcuba.entity.VariableIndicator[ id=" + id + " ]";
    }

    public Variable getVariable() {
        return variable;
    }

    public void setVariable(Variable variable) {
        this.variable = variable;
    }

}
