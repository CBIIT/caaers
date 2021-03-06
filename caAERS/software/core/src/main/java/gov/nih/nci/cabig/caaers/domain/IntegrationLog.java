/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.domain;

import gov.nih.nci.cabig.ctms.domain.AbstractMutableDomainObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

@Entity
@Table(name="integration_logs")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence", value = "seq_integration_logs_id") })
public class IntegrationLog extends AbstractMutableDomainObject{
	
	// time the event is logged
	private Date loggedOn = new Date();
	
	// universal unique identifier to identify each request/update
	private String correlationId;
	
	// entity type
	private String entity;
	
	// operation name
	private String operation;
	
	// progress made by synch request
	private SynchStatus synchStatus;
	
	// details 
	private String notes;
	
	private List<IntegrationLogDetail> integrationLogDetails = new ArrayList<IntegrationLogDetail>();

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    public String getNotes() {
		return notes;
	}

	@OneToMany (mappedBy = "integrationLog", fetch = FetchType.LAZY)
	@Cascade({CascadeType.PERSIST, CascadeType.SAVE_UPDATE})
	public List<IntegrationLogDetail> getIntegrationLogDetails() {
		return integrationLogDetails;
	}

	public void setIntegrationLogDetails(
			List<IntegrationLogDetail> integrationLogDetails) {
		this.integrationLogDetails = integrationLogDetails;
	}

    public void addIntegrationLogDetails(IntegrationLogDetail logDetails){
        if(logDetails != null){
            logDetails.setIntegrationLog(this);
        }
        integrationLogDetails.add(logDetails);
    }

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Date getLoggedOn() {
		return loggedOn;
	}

	public void setLoggedOn(Date loggedOn) {
		this.loggedOn = loggedOn;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	public String getEntity() {
		return entity;
	}


	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	@Enumerated(EnumType.STRING)
	public SynchStatus getSynchStatus() {
		return synchStatus;
	}

	public void setSynchStatus(SynchStatus synchStatus) {
		this.synchStatus = synchStatus;
	}
	
	@Transient
	public String getIfSuccess(){
		if(synchStatus == SynchStatus.REQUST_PROCESSING_ERROR){
			return "Failed";
		}
		return "Success";
	}

}
