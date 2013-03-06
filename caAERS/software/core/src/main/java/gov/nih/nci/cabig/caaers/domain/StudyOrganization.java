/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.domain;

import gov.nih.nci.cabig.caaers.utils.DateUtils;
import gov.nih.nci.cabig.caaers.validation.annotation.UniqueObjectInCollection;
import gov.nih.nci.cabig.ctms.collections.LazyListHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * This class encapsulates all types of organizations associated with a Study
 * 
 * @author Ram Chilukuri
 * @author Biju Joseph
 * 
 */
@Entity
@Table(name = "study_organizations")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence", value = "seq_study_organizations_id") })
public abstract class StudyOrganization extends AbstractMutableRetireableDomainObject implements StudyChild {
	
    private Study study;
    private Organization organization;
    private LazyListHelper lazyListHelper;
    private Date startDate;
    private Date endDate;

    public StudyOrganization() {
        lazyListHelper = new LazyListHelper();
        lazyListHelper.add(StudyInvestigator.class, new StudyOrganizationChildInstantiateFactory<StudyInvestigator>(this, StudyInvestigator.class));
        lazyListHelper.add(StudyPersonnel.class, new StudyOrganizationChildInstantiateFactory<StudyPersonnel>(this, StudyPersonnel.class));
    }

    
    /**
     * This method will deactivate a {@link StudyOrganization}, by setting the termEndDate to a past date.
     */
    public void deactivate(){
    	this.endDate = DateUtils.yesterday();
    }
    
    /**
     *  This method will activate a {@link StudyOrganization}
     */
    public void activate(){
    	this.startDate = DateUtils.yesterday();
    	this.endDate = null;
    }

    
    public void addStudyPersonnel(StudyPersonnel studyPersonnel) {
        getStudyPersonnels().add(studyPersonnel);
        studyPersonnel.setStudyOrganization(this);
    }

    public void addStudyInvestigators(StudyInvestigator studyInvestigator) {
        getStudyInvestigators().add(studyInvestigator);
        studyInvestigator.setStudyOrganization(this);
    }
    
    
    @ManyToOne
    @JoinColumn(name = "site_id", nullable = false)
    @Cascade(value = { CascadeType.LOCK})
    public Organization getOrganization() {
        return organization;
    }

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    @Cascade(value = { CascadeType.EVICT})
    public Study getStudy() {
        return study;
    }

    @OneToMany(mappedBy = "studyOrganization")
    @Cascade(value = { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
    @UniqueObjectInCollection(message = "Duplicates found in StudyInvestigator list")
    public List<StudyInvestigator> getStudyInvestigatorsInternal() {
        return lazyListHelper.getInternalList(StudyInvestigator.class);
    }

    public void setStudyInvestigatorsInternal(List<StudyInvestigator> studyInvestigators) {
        lazyListHelper.setInternalList(StudyInvestigator.class, studyInvestigators);
    }
    
    /**
     * This method will return all {@link StudyInvestigator}s that are not retired
     * @return
     */
    @Transient
    public List<StudyInvestigator> getActiveStudyInvestigators(){
    	List<StudyInvestigator> investigators = new ArrayList<StudyInvestigator>();
    	for(StudyInvestigator si : getStudyInvestigators()){
    		if(!si.isRetired() && si.isActive()) investigators.add(si);
    	}
    	return investigators;
    }

    @OneToMany(mappedBy = "studyOrganization", fetch = FetchType.LAZY)
    @Cascade(value = { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
    @UniqueObjectInCollection(message = "Duplicates found in StudyPersonnel list")
    public List<StudyPersonnel> getStudyPersonnelsInternal() {
        return lazyListHelper.getInternalList(StudyPersonnel.class);
    }

    public void setStudyPersonnelsInternal(List<StudyPersonnel> studyPersonnels) {
        lazyListHelper.setInternalList(StudyPersonnel.class, studyPersonnels);
    }
    

    /**
     * This method will return all {@link StudyPersonnel}s that are not retired
     * @return
     */
    @Transient
    public List<StudyPersonnel> getActiveStudyPersonnel(){
    	List<StudyPersonnel> personnel = new ArrayList<StudyPersonnel>();
    	for(StudyPersonnel sp : getStudyPersonnels()){
    		if(sp.isActive() && !sp.isRetired()) personnel.add(sp);
    	}
    	return personnel;
    }
    
    //BJ : wrong spelling.
    @Transient
    public List<StudyPersonnel> getStudyPersonnels() {
        return lazyListHelper.getLazyList(StudyPersonnel.class);
    }

    public void setStudyPersonnels(List<StudyPersonnel> studyPersonnels) {
        setStudyPersonnelsInternal(studyPersonnels);
    }

    @Transient
    public List<StudyInvestigator> getStudyInvestigators() {
        return lazyListHelper.getLazyList(StudyInvestigator.class);
    }

    public void setStudyInvestigators(List<StudyInvestigator> studyInvestigators) {
        setStudyInvestigatorsInternal(studyInvestigators);
    }

    public void setOrganization(final Organization organization) {
        this.organization = organization;
    }

    public void setStudy(final Study study) {
        this.study = study;
    }

    @Transient
    public abstract String getRoleName();
    

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="start_date")
    public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date termStartDate) {
		this.startDate = termStartDate;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="end_date")
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date termEndDate) {
		this.endDate = termEndDate;
	}

	@Transient
    public boolean isActive(){
    	return (startDate != null && DateUtils.between(new Date(), startDate, endDate));
    }
    
   
    @Transient
    public boolean isInActive(){
    	return (startDate == null || !DateUtils.between(new Date(), startDate, endDate));
    }
    
    
    /**
     * This method will return the list of users having a specific role.
     * @param personRole
     * @return
     */
    @Transient
    public List<Person> findUsersByRole(PersonRole personRole){
    	List<Person> people = new ArrayList<Person>();
    	for(StudyInvestigator studyInvestigator : getStudyInvestigators()){
            if(!studyInvestigator.getSiteInvestigator().getInvestigator().isUser()) continue;
    		if(StringUtils.equals(studyInvestigator.getRoleCode(), personRole.getRoleCode()) || StringUtils.equals(studyInvestigator.getRoleCode(), personRole.getDisplayName())){
    			people.add(studyInvestigator.getSiteInvestigator().getInvestigator());
    		}
    	}
    	
    	for(StudyPersonnel studyPerson : getStudyPersonnels()){
            if(!studyPerson.getSiteResearchStaff().getResearchStaff().isUser())  continue;
    		if(StringUtils.equals(studyPerson.getRoleCode(), personRole.getRoleCode())|| StringUtils.equals(studyPerson.getRoleCode(), personRole.getDisplayName())){
    			people.add(studyPerson.getSiteResearchStaff().getResearchStaff());
    		}
    	}
    	return people;
    }
    
    /**
     * 
     * @param siteResearchStaff
     */
    public void syncStudyPersonnel(SiteResearchStaff siteResearchStaff){
		StudyPersonnel studyPersonnel =  findStudyPersonnel(siteResearchStaff);
		if(studyPersonnel != null){
			studyPersonnel.syncDates();
		}
    }
    
    /**
     * 
     * @param siteResearchStaffRole
     * @return
     */
    public StudyPersonnel findStudyPersonnel(SiteResearchStaff siteResearchStaff){
    	for(StudyPersonnel studyPersonnel : this.getStudyPersonnelsInternal()){
            if(studyPersonnel.getSiteResearchStaff() == null || siteResearchStaff == null) continue;
    		if(studyPersonnel.getSiteResearchStaff().equals(siteResearchStaff)){
				return studyPersonnel;
    		}
    	}
    	return null;
    }
    
    /**
     * This method will find the email address of people associated with the role. 
     * @param roleName
     * @return
     */
    public List<String> findEmailAddressByRole(String roleName){
    	List<String> emails = new ArrayList<String>();
    	
    	//check in personnel
    	for(StudyPersonnel personnel : getActiveStudyPersonnel()){
    		if(StringUtils.equals(personnel.getRoleCode(), roleName)){
    			emails.add(personnel.getEmailAddress());
    		}
    	}
    	
    	//check among investigators
    	for(StudyInvestigator investigator : getActiveStudyInvestigators()){
    		if(StringUtils.equals(investigator.getRoleCode(), roleName)){
    			emails.add(investigator.getEmailAddress());
    		}
    	}
    	
    	return emails;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (getOrganization() == null ? 0 : getOrganization().hashCode());
        result = prime * result + (getStudy() == null ? 0 : getStudy().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        boolean sameInstance = false;
        if(this instanceof StudySite && obj instanceof StudySite){
        	sameInstance = true;
        }else if(this instanceof StudyFundingSponsor && obj instanceof StudyFundingSponsor){
        	sameInstance = true;
        }else if(this instanceof StudyCoordinatingCenter && obj instanceof StudyCoordinatingCenter){
        	sameInstance = true;
        }
        
        final StudyOrganization other = (StudyOrganization) obj;
        boolean sameOrg = false;
        if (organization == null) {
            if (other.getOrganization() != null) {
                return false;
            }
        } else if (organization.equals(other.getOrganization())) {
            sameOrg = true;
        }
        
        boolean sameStudy = false;
        if (getStudy() == null) {
            if (other.getStudy() != null) {
                return false;
            }
        }else if (getStudy().equals(other.getStudy())) {
            sameStudy = true;
        }
        if(sameInstance && sameOrg && sameStudy){
        	return  true;
        }else{
        	return false;
        }
    }
    

}
