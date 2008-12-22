package gov.nih.nci.cabig.caaers.domain.workflow;

import java.util.Date;

import gov.nih.nci.cabig.ctms.domain.AbstractMutableDomainObject;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
/**
 * This class represents a review comment added by the user
 * @author biju
 *
 */
@Entity
@Table(name = "wf_review_comments")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("dtype")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = {@Parameter(name = "sequence", value = "seq_wf_review_comments_id")})

public class ReviewComment extends AbstractMutableDomainObject{
	
	private String comment;
	private Date date;
	private String autoGeneratedText;
	
	@Column(name="user_comment")
	public String getUserComment() {
		return comment;
	}
	public void setUserComment(String comment) {
		this.comment = comment;
	}
	@Column(name="created_date")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreatedDate() {
		return date;
	}
	
	public void setCreatedDate(Date date) {
		this.date = date;
	}
	
	@Column(name="auto_text")
	public String getAutoGeneratedText() {
		return autoGeneratedText;
	}
	
	public void setAutoGeneratedText(String autoGeneratedText) {
		this.autoGeneratedText = autoGeneratedText;
	}
	
	@Transient
	public String getFullComment() {
		StringBuilder sb = new StringBuilder();
		if(autoGeneratedText != null) sb.append(autoGeneratedText);
		if(comment != null) sb.append(" : " +  comment);
		return sb.toString();
		
	}

}
