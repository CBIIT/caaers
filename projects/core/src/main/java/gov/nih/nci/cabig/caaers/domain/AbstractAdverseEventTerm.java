package gov.nih.nci.cabig.caaers.domain;

import gov.nih.nci.cabig.ctms.domain.AbstractMutableDomainObject;
import gov.nih.nci.cabig.ctms.domain.DomainObject;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * @author Krikor Krumlian
 */

@Entity
@Table(name = "ae_terms")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name = "term_type",
    discriminatorType = DiscriminatorType.STRING
)
@DiscriminatorValue("ABSTRACT_TERM") // should be ignored
@GenericGenerator(name = "id-generator", strategy = "native",
    parameters = {
        @Parameter(name = "sequence", value = "seq_ae_terms_id")
    }
)
public abstract class AbstractAdverseEventTerm<T extends DomainObject> extends AbstractMutableDomainObject {
    private T term;  
    private AdverseEvent adverseEvent;

    ////// BEAN PROPERTIES
	
	@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adverse_event_id")
    public AdverseEvent getAdverseEvent() {
        return adverseEvent;
    }

    public void setAdverseEvent(AdverseEvent adverseEvent) {
        this.adverseEvent = adverseEvent;
    }
    
    @Transient
    public abstract String getUniversalTerm();
    
    
    
    @Transient
    /* this is only transient here -- subclasses need to override it and specify what it refers to
       This should work:
    @ManyToOne
    @JoinColumn(name = "cause_id", nullable = false)
     */
    public T getTerm() {
        return term;
    }

    public void setTerm(T term) {
        this.term = term;
    }
}
