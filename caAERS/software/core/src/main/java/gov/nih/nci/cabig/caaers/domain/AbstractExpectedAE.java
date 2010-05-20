package gov.nih.nci.cabig.caaers.domain;

import gov.nih.nci.cabig.ctms.domain.AbstractMutableDomainObject;
import gov.nih.nci.cabig.ctms.domain.DomainObject;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.beans.BeanUtils;

/**
 * @author Ion C. Olaru
 */

@Entity
@Table(name = "expected_aes")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = {@Parameter(name = "sequence", value = "seq_expected_aes_id")})
@DiscriminatorColumn(name = "term_type", discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class AbstractExpectedAE<T extends DomainObject> extends AbstractMutableDomainObject {
    private T term;
    private Study study;
    private String otherToxicity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", insertable = false, updatable = false)
    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    @Transient
    public T getTerm() {
        return term;
    }

    public void setTerm(T term) {
        this.term = term;
    }

    @Transient
    public abstract boolean isOtherRequired();
    
    @Transient
    public abstract boolean isMedDRA();

    @Transient
    public abstract String getFullName();

    public AbstractExpectedAE copy() {
        AbstractExpectedAE abstractAdverseEventTerm = (AbstractExpectedAE) BeanUtils.instantiateClass(getClass());
        BeanUtils.copyProperties(this, abstractAdverseEventTerm, new String[]{"id", "gridId", "version", "study"});
        return abstractAdverseEventTerm;
    }

    public String getOtherToxicity() {
        return otherToxicity;
    }

    public void setOtherToxicity(String otherToxicity) {
        this.otherToxicity = otherToxicity;
    }

}