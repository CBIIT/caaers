package gov.nih.nci.cabig.caaers.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import gov.nih.nci.cabig.ctms.domain.AbstractMutableDomainObject;

/**
 * This class represents the Agent domain object associated with the Adverse event report.
 * 
 * @author Krikor Krumlian
 * 
 */
@Entity
@Table(name = "agents")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence", value = "seq_agents_id") })
public class Agent extends AbstractMutableDomainObject {

    private String name;

    private String description;

    private String nscNumber;

    private List<StudyAgent> studyAgents = new ArrayList<StudyAgent>();

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy = "agent", fetch = FetchType.LAZY)
    @Cascade(value = { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
    public List<StudyAgent> getStudyAgents() {
        return studyAgents;
    }

    public void setStudyAgents(List<StudyAgent> studyAgents) {
        this.studyAgents = studyAgents;
    }

    @Column(name = "nsc")
    public String getNscNumber() {
        return nscNumber;
    }

    public void setNscNumber(String nsc) {
        this.nscNumber = nsc;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Agent that = (Agent) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (nscNumber != null ? !nscNumber.equals(that.nscNumber) : that.nscNumber != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (name != null ? name.hashCode() : 0);
        result = 29 * result + (description != null ? description.hashCode() : 0);
        result = 29 * result + (nscNumber != null ? nscNumber.hashCode() : 0);
        return result;
    }

}
