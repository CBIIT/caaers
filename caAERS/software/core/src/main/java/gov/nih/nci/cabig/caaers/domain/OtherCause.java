package gov.nih.nci.cabig.caaers.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.beans.BeanUtils;

 
/**
 * This class represents the OtherCause domain object associated with the Adverse event report.
 *
 * @author Rhett Sutphin
 */
@Entity
@Table(name = "other_causes")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = {@Parameter(name = "sequence", value = "seq_other_causes_id")})
public class OtherCause extends AbstractExpeditedReportCollectionElementChild {
    
    /** The text. */
    private String text;

    public OtherCause(){
        this(null);
    }

    public OtherCause(String text){
        this.text = text;
    }

    /**
     * Gets the text.
     *
     * @return the text
     */
    @Column(name = "cause_text")
    public String getText() {
        return text;
    }

    /**
     * Sets the text.
     *
     * @param text the new text
     */
    public void setText(String text) {
        this.text = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((text == null) ? 0 : text.hashCode());
        return result;
    }

    ///OBJECT METHODS
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final OtherCause other = (OtherCause) obj;
        if (text == null) {
            if (other.text != null)
                return false;
        } else if (!text.equals(other.text))
            return false;
        return true;
    }

    /**
     * Copy.
     *
     * @return the other cause
     */
    public OtherCause copy() {
        OtherCause otherCause = new OtherCause();
        BeanUtils.copyProperties(this, otherCause, new String[]{"id", "gridId",
                "version", "report"});

        return otherCause;

    }


}
