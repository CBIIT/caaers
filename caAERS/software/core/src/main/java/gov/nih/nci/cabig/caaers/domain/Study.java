
package gov.nih.nci.cabig.caaers.domain;

import gov.nih.nci.cabig.caaers.CollectionUtil;
import gov.nih.nci.cabig.caaers.utils.ProjectedList;
import gov.nih.nci.cabig.caaers.validation.annotation.UniqueObjectInCollection;
import gov.nih.nci.cabig.ctms.collections.LazyListHelper;
import gov.nih.nci.cabig.ctms.domain.DomainObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections15.functors.InstantiateFactory;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.*;

/**
 * Domain object representing Study(Protocol)
 *
 * @author Sujith Vellat Thayyilthodi
 * @author Rhett Sutphin
 * @author Biju Joseph
 * @author Ion Olaru
 * @author Sameer Sawant
 * @author Monish Dombla
 * 
 */
@Entity
@Table(name = "studies")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = {@Parameter(name = "sequence", value = "seq_studies_id")})
@Where(clause = "load_status > 0")
public abstract class Study extends AbstractIdentifiableDomainObject implements Serializable {

	protected static final long serialVersionUID = 2524271609924679883L;
	public static final String STATUS_ADMINISTRATIVELY_COMPLETE = "Administratively Complete";
    public static final String STATUS_ACTIVE = "Active - Trial is open to accrual";

    protected String shortTitle;
    @Deprecated protected String longTitle;
    @Deprecated protected String description;
    @Deprecated protected String precis;
    protected String phaseCode;
    protected AeTerminology aeTerminology;
    protected DiseaseTerminology diseaseTerminology;
    @Deprecated String status;
    protected String otherTreatmentAssignment;

    // TODO: Remove
    protected Boolean blindedIndicator;
    @Deprecated Boolean multiInstitutionIndicator;
    @Deprecated protected Boolean adeersReporting;

    // TODO: Remove
    protected Boolean randomizedIndicator;

    // TODO: Remove
    protected String diseaseCode;

    // TODO: Remove
    protected String monitorCode;

    // TODO: Remove
    protected Integer targetAccrualNumber;
    
    protected MeddraVersion otherMeddra;
    protected List<StudyOrganization> studyOrganizations;

    protected List<CtepStudyDisease> ctepStudyDiseases = new ArrayList<CtepStudyDisease>();
    protected List<MeddraStudyDisease> meddraStudyDiseases = new ArrayList<MeddraStudyDisease>();
    protected List<StudyCondition> studyConditions = new ArrayList<StudyCondition>();

    protected List<AbstractExpectedAE> expectedAEs = new ArrayList<AbstractExpectedAE>();
    protected List<ExpectedAEMeddraLowLevelTerm> expectedAEMeddraTerms = new ArrayList<ExpectedAEMeddraLowLevelTerm>();
    protected List<ExpectedAECtcTerm> expectedAECTCTerms = new ArrayList<ExpectedAECtcTerm>();

    protected final LazyListHelper lazyListHelper;

    protected OrganizationAssignedIdentifier organizationAssignedIdentifier;
    protected List<StudyTherapy> studyTherapies = new ArrayList<StudyTherapy>();
    @Deprecated protected List<ReportFormat> reportFormats = new ArrayList<ReportFormat>();
    protected List<CtcCategory> ctcCategories = new ArrayList<CtcCategory>();
    protected List<OtherIntervention> otherInterventions = new ArrayList<OtherIntervention>();
    protected List<StudyDevice> studyDevices = new ArrayList<StudyDevice>();

    // TODO move into Command Object
    // Investigators page)

    protected Boolean drugAdministrationTherapyType = Boolean.FALSE;
    protected Boolean radiationTherapyType = Boolean.FALSE;
    protected Boolean deviceTherapyType = Boolean.FALSE;
    protected Boolean surgeryTherapyType = Boolean.FALSE;
    protected Boolean behavioralTherapyType = Boolean.FALSE;
    protected Integer loadStatus = LoadStatus.COMPLETE.getCode();

    // Used to facilitate import of a coordinating center / funding sponsor
    protected FundingSponsor fundingSponsor;
    protected CoordinatingCenter coordinatingCenter;

    // DCP specific properties
    @Deprecated protected Design design;

    protected List<Epoch> epochs=new ArrayList<Epoch>();
    
    protected Boolean dataEntryStatus;
    protected Boolean verbatimFirst;

  //Added for COPPA integration
    protected String externalId;
    

    public Study() {

        lazyListHelper = new LazyListHelper();

        // register with lazy list helper study site.
        lazyListHelper.add(StudySite.class, new StudyChildInstantiateFactory<StudySite>(this, StudySite.class));
        lazyListHelper.add(StudyFundingSponsor.class, new StudyChildInstantiateFactory<StudyFundingSponsor>(this, StudyFundingSponsor.class));
        lazyListHelper.add(Identifier.class, new InstantiateFactory<Identifier>(Identifier.class));
        lazyListHelper.add(StudyAgent.class, new StudyChildInstantiateFactory<StudyAgent>(this, StudyAgent.class));
        lazyListHelper.add(StudyDevice.class, new StudyChildInstantiateFactory<StudyDevice>(this, StudyDevice.class));
        lazyListHelper.add(OtherIntervention.class, new StudyChildInstantiateFactory<OtherIntervention>(this, OtherIntervention.class));
        lazyListHelper.add(TreatmentAssignment.class, new InstantiateFactory<TreatmentAssignment>(TreatmentAssignment.class));

        // mandatory, so that the lazy-projected list is created/managed properly.
        setStudyOrganizations(new ArrayList<StudyOrganization>());
        setStudyAgentsInternal(new ArrayList<StudyAgent>());
    }

    // / LOGIC
    public void addStudyOrganization(final StudyOrganization so) {
        getStudyOrganizations().add(so);
        so.setStudy(this);
    }

    public void addTreatmentAssignment(final TreatmentAssignment treatmentAssignment) {
        getTreatmentAssignments().add(treatmentAssignment);
        treatmentAssignment.setStudy(this);
    }

    public void removeStudyOrganization(final StudyOrganization so) {
        getStudyOrganizations().remove(so);
    }

    @Transient
    public List<StudySite> getStudySites() {
        return lazyListHelper.getLazyList(StudySite.class);
    }

    public void addStudySite(final StudySite studySite) {
        getStudySites().add(studySite);
        studySite.setStudy(this);
    }

    public void removeStudySite(final StudySite studySite) {
        getStudySites().remove(studySite);
    }

    public void addStudyFundingSponsor(final StudyFundingSponsor studyFundingSponsor) {
        getStudyFundingSponsors().add(studyFundingSponsor);
        studyFundingSponsor.setStudy(this);
    }

    @Transient
    public List<StudyFundingSponsor> getStudyFundingSponsors() {
        return lazyListHelper.getLazyList(StudyFundingSponsor.class);
    }

    @Transient
    public StudyFundingSponsor getPrimaryFundingSponsor() {
        for (StudyFundingSponsor sponsor : getStudyFundingSponsors()) {
            if (sponsor.isPrimary()) {
                return sponsor;
            }
        }
        return null;
    }

    @Transient
    public Organization getPrimaryFundingSponsorOrganization() {
        StudyFundingSponsor primarySponsor = getPrimaryFundingSponsor();
        if (primarySponsor == null) {
            return null;
        }
        return primarySponsor.getOrganization();
    }

    @Transient
    public void setPrimaryFundingSponsorOrganization(final Organization org) {
        // if already a primary funding sponsor exist, replace that sponor's organization
        StudyFundingSponsor xprimarySponsor = getPrimaryFundingSponsor();
        if (xprimarySponsor != null) {
            xprimarySponsor.setOrganization(org);
        } else {
            // no primary funding sponsor yet exist, so create one
            List<StudyFundingSponsor> sponsors = getStudyFundingSponsors();
            int size = sponsors.size();
            StudyFundingSponsor primarySponsor = sponsors.get(size);
            primarySponsor.setOrganization(org);
        }
    }

    /**
     * Will return the primary identifier associated to this study.
     */
    @Transient
    public Identifier getPrimaryIdentifier() {
        try {
			for (Identifier id : getIdentifiersLazy()) {
			    if (id.isPrimary()) return id;
			}
		} catch (Exception igonre) {
		}
        return null;
    }
    

    public void addStudyAgent(final StudyAgent studyAgent) {
        getStudyAgents().add(studyAgent);
        studyAgent.setStudy(this);
    }

    public void addCtepStudyDisease(final CtepStudyDisease ctepStudyDisease) {
        ctepStudyDisease.setStudy(this);
        ctepStudyDiseases.add(ctepStudyDisease);
    }

    public void addMeddraStudyDisease(final MeddraStudyDisease meddraStudyDisease) {
        meddraStudyDisease.setStudy(this);
        meddraStudyDiseases.add(meddraStudyDisease);
    }
    
    public void addStudyCondition(final StudyCondition studyCondition) {
        studyCondition.setStudy(this);
        studyConditions.add(studyCondition);
    }
    
    
    @Transient
    public List<StudyCoordinatingCenter> getStudyCoordinatingCenters() {
        return new ProjectedList<StudyCoordinatingCenter>(studyOrganizations, StudyCoordinatingCenter.class);
    }

    @Transient
    public StudyCoordinatingCenter getStudyCoordinatingCenter() {
        return getStudyCoordinatingCenters().isEmpty() ? null : getStudyCoordinatingCenters().get(0);
    }

    @Transient
    public OrganizationAssignedIdentifier getOrganizationAssignedIdentifier() {

        if (getId() != null) {
            for (Identifier identifier : getIdentifiers()) {

                if (identifier instanceof OrganizationAssignedIdentifier
                        && identifier.getType().equalsIgnoreCase(
                        "Co-ordinating Center Identifier")) {
                    organizationAssignedIdentifier = (OrganizationAssignedIdentifier) identifier;
                    return organizationAssignedIdentifier;
                }

            }
        }
        if (organizationAssignedIdentifier == null) {
            organizationAssignedIdentifier = new OrganizationAssignedIdentifier();
            organizationAssignedIdentifier.setType("Co-ordinating Center Identifier");
            organizationAssignedIdentifier.setPrimaryIndicator(Boolean.FALSE);
        }

        return organizationAssignedIdentifier;
    }
    
    @Transient
    public OrganizationAssignedIdentifier getNciAssignedIdentifier() {
        for (Identifier identifier : getIdentifiers()) {

            if (identifier instanceof OrganizationAssignedIdentifier
                    && identifier.getType().equalsIgnoreCase(
                    "NCI Assigned Identifier")) {
                organizationAssignedIdentifier = (OrganizationAssignedIdentifier) identifier;
                return organizationAssignedIdentifier;
            }
        }
        return organizationAssignedIdentifier;
    }
    
    @Transient
    public OrganizationAssignedIdentifier getFundingSponsorIdentifier() {
        for (Identifier identifier : getIdentifiers()) {

            if (identifier instanceof OrganizationAssignedIdentifier
                    && identifier.getType().equalsIgnoreCase(
                    		OrganizationAssignedIdentifier.SPONSOR_IDENTIFIER_TYPE)) {
                organizationAssignedIdentifier = (OrganizationAssignedIdentifier) identifier;
                return organizationAssignedIdentifier;
            }
        }
        return organizationAssignedIdentifier;
    }
    
    @Transient
    public OrganizationAssignedIdentifier getCoordinatingCenterIdentifier() {
        for (Identifier identifier : getIdentifiers()) {

            if (identifier instanceof OrganizationAssignedIdentifier
                    && identifier.getType().equalsIgnoreCase(
                    		OrganizationAssignedIdentifier.COORDINATING_CENTER_IDENTIFIER_TYPE)) {
                organizationAssignedIdentifier = (OrganizationAssignedIdentifier) identifier;
                return organizationAssignedIdentifier;
            }
        }
        return organizationAssignedIdentifier;
    }
    
    @Transient
    public String getFundingSponsorIdentifierValue() {
        return getFundingSponsorIdentifier().getValue();
    }
    
    @Transient
    public String getCoordinatingCenterIdentifierValue() {
        return getCoordinatingCenterIdentifier().getValue();
    }
    
    @Transient
    public String getIdentifierContaining(String text){
    	for (Identifier identifier : getIdentifiers()) {
    		if(StringUtils.containsIgnoreCase(identifier.getValue(), text)){
    			return identifier.getValue();
    		}
    	}
    	return getPrimaryIdentifierValue();
    }
    

    @Transient
    public void setOrganizationAssignedIdentifier(final OrganizationAssignedIdentifier organizationAssignedIdentifier) {
        this.organizationAssignedIdentifier = organizationAssignedIdentifier;
    }

    @Transient
    @UniqueObjectInCollection(message = "Duplicates found in Study Agents list")
    public List<StudyAgent> getStudyAgents() {
        return lazyListHelper.getLazyList(StudyAgent.class);
    }

    @Transient
    public void setStudyAgents(final List<StudyAgent> studyAgents) {
        setStudyAgentsInternal(studyAgents);
    }

    @OneToMany(mappedBy = "study", fetch = FetchType.LAZY)
    @Cascade(value = {CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    @OrderBy
    public List<StudyDevice> getStudyDevices() {
        return this.studyDevices;
    }

    public void setStudyDevices(final List<StudyDevice> studyDevices) {
        this.studyDevices = studyDevices;
    }

    public void addStudyDevice(StudyDevice sd) {
        this.getStudyDevices().add(sd);
        sd.setStudy(this);
    }

    @OneToMany(mappedBy = "study", fetch = FetchType.LAZY)
    @Cascade(value = {CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    @OrderBy
    public List<OtherIntervention> getOtherInterventions() {
        return this.otherInterventions;
    }

    public void addOtherIntervention(OtherIntervention oi) {
        this.getOtherInterventions().add(oi);
        oi.setStudy(this);
    }

    public void setOtherInterventions(final List<OtherIntervention> otherInterventions) {
        this.otherInterventions = otherInterventions;
    }

    /**
     * Will return the {@link StudyAgent}s that are not retired
     * @return
     */
    @Transient
    public List<StudyAgent> getActiveStudyAgents(){
    	List<StudyAgent> agents = new ArrayList<StudyAgent>();
    	for(StudyAgent sa : getStudyAgents()){
    		if(!sa.isRetired()) agents.add(sa);
    	}
    	return agents;
    }
    
    /**
     * Will return the {@link StudyDevice}s that are not retired
     * @return
     */
    @Transient
    public List<StudyDevice> getActiveStudyDevices() {
    	List<StudyDevice> devices = new ArrayList<StudyDevice>();
    	for(StudyDevice sd : getStudyDevices()){
    		if(!sd.isRetired()) devices.add(sd);
    	}
    	return devices;
    }

    @Transient
    public List<OtherIntervention> getActiveOtherInterventions() {
    	List<OtherIntervention> ois = new ArrayList<OtherIntervention>();
    	for(OtherIntervention oi : getOtherInterventions()){
    		if(!oi.isRetired()) ois.add(oi);
    	}
    	return ois;
    }

    /**
    * Will return the {@link StudyDevice}s that are not retired
    * @return
    */
   @Transient
   public List<OtherIntervention> getActiveStudyRadiations() {
       List<OtherIntervention> ois = new ArrayList<OtherIntervention>();
       for(OtherIntervention oi : getOtherInterventions()){
           if(!oi.isRetired() &&  oi.getStudyTherapyType().equals(StudyTherapyType.RADIATION)) ois.add(oi);
       }
       return ois;
   }


    /**
    * Will return the {@link StudyDevice}s that are not retired
    * @return
    */
   @Transient
   public List<OtherIntervention> getActiveStudySurgeries() {
       List<OtherIntervention> ois = new ArrayList<OtherIntervention>();
       for(OtherIntervention oi : getOtherInterventions()){
           if(!oi.isRetired() &&  oi.getStudyTherapyType().equals(StudyTherapyType.SURGERY)) ois.add(oi);
       }
       return ois;
   }
    
    /**
     * Will return the {@link StudySite}s that are not retired
     * @return
     */
    @Transient
    public List<StudySite> getActiveStudySites(){
    	List<StudySite> sites = new ArrayList<StudySite>();
    	for(StudySite site : getStudySites()){
    		if(!site.isRetired()) sites.add(site);
    	}
    	return sites;
    }
    
    /**
     * Will return the {@link StudyOrganization}s that are not retired.
     * @return
     */
    @Transient
    public List<StudyOrganization> getActiveStudyOrganizations(){
    	List<StudyOrganization> studyOrgs = new ArrayList<StudyOrganization>();
    	for(StudyOrganization studyOrg : getStudyOrganizations()){
    		if(!studyOrg.isRetired()) studyOrgs.add(studyOrg);
    	}
    	return studyOrgs;
    }
    
    /**
     * Will return the {@link TreatmentAssignment}s that are not retired.
     * @return
     */
    @Transient
    public List<TreatmentAssignment> getActiveTreatmentAssignments(){
    	List<TreatmentAssignment> tacs = new ArrayList<TreatmentAssignment>();
    	for(TreatmentAssignment tac : getTreatmentAssignments()){
    		if(!tac.isRetired()) tacs.add(tac);
    	}
    	return tacs;
    }
    
    /**
     * Will return the {@link AbstractStudyDisease}s that are not retired.
     * @return
     */
    @Transient
    public List<? extends AbstractStudyDisease<? extends DomainObject>> getActiveStudyDiseases(){

        if (diseaseTerminology != null && diseaseTerminology.getDiseaseCodeTerm() != null && diseaseTerminology.getDiseaseCodeTerm().equals(DiseaseCodeTerm.CTEP)) {
            List<CtepStudyDisease> diseases = new ArrayList<CtepStudyDisease>();
            if (CollectionUtils.isNotEmpty(ctepStudyDiseases)) {
                for (CtepStudyDisease disease : ctepStudyDiseases) {
                    if (!disease.isRetired()) diseases.add(disease);
                }
            }
            return diseases;
        }

        if (diseaseTerminology != null && diseaseTerminology.getDiseaseCodeTerm() != null && diseaseTerminology.getDiseaseCodeTerm().equals(DiseaseCodeTerm.MEDDRA)) {
            List<MeddraStudyDisease> diseases = new ArrayList<MeddraStudyDisease>();
            if (CollectionUtils.isNotEmpty(meddraStudyDiseases)) {
                for (MeddraStudyDisease disease : meddraStudyDiseases) {
                    if (!disease.isRetired()) diseases.add(disease);
                }
            }
            return diseases;
        }

        if (diseaseTerminology != null && diseaseTerminology.getDiseaseCodeTerm() != null && diseaseTerminology.getDiseaseCodeTerm().equals(DiseaseCodeTerm.OTHER)) {
            List<StudyCondition> diseases = new ArrayList<StudyCondition>();
            if (CollectionUtils.isNotEmpty(studyConditions)) {
                for (StudyCondition disease : studyConditions) {
                    if (!disease.isRetired()) diseases.add(disease);
                }
            }
            return diseases;
        }

        return null;

    }

    public boolean hasTherapyOfType(StudyTherapyType therapyType) {
        switch (therapyType.getCode().intValue()) {
            case 1: return getActiveStudyAgents().size() > 0;  
            case 4: return getActiveStudyDevices().size() > 0;
            default:
                for (OtherIntervention oi : getActiveOtherInterventions()) {
                    if (oi.getStudyTherapyType().equals(therapyType)) return true;
                }
        }
        return false;
    }
    
    /**
     * Will remove all the {@link StudyTherapy} of the specific {@link StudyTherapyType}
     * @param therapyType
     */
    @Deprecated
    public void removeTherapiesOfType(StudyTherapyType therapyType){
    	ArrayList<StudyTherapy> therapies = new ArrayList<StudyTherapy>(getStudyTherapies());
    	for(StudyTherapy therapy : therapies){
    		if(therapy.getStudyTherapyType() == therapyType) getStudyTherapies().remove(therapy);
    	}
    }

    @Transient
    public boolean isSurgeryPresent() {
        return this.hasTherapyOfType(StudyTherapyType.SURGERY);
    }

    @Transient
    public boolean isDevicePresent() {
        return this.hasTherapyOfType(StudyTherapyType.DEVICE);
    }

    @Transient
    public boolean isRadiationPresent() {
        return this.hasTherapyOfType(StudyTherapyType.RADIATION);
    }

    @Transient
    public boolean isBehavioralPresent() {
        return this.hasTherapyOfType(StudyTherapyType.BEHAVIORAL);
    }

    @Transient
    public boolean isDrugAdministrationPresent() {
        return this.hasTherapyOfType(StudyTherapyType.DRUG_ADMINISTRATION);
    }

    // / BEAN PROPERTIES

    // TODO: this stuff should really, really not be in here. It's web-view/entry specific.

    @Deprecated
    @Transient
    public Ctc getCtcVersion() {
        return getAeTerminology().getCtcVersion();
    }

    @Deprecated
    @Transient
    public void setCtcVersion(final Ctc ctcVersion) {
        AeTerminology t = getAeTerminology();
        t.setTerm(Term.CTC);
        t.setCtcVersion(ctcVersion);
    }

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "study")
    @Cascade(value = {CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    public DiseaseTerminology getDiseaseTerminology() {
        if (diseaseTerminology == null) {
            diseaseTerminology = new DiseaseTerminology();
            diseaseTerminology.setStudy(this);
        }
        return diseaseTerminology;
    }

    public void setDiseaseTerminology(final DiseaseTerminology diseaseTerminology) {
        this.diseaseTerminology = diseaseTerminology;
    }

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "study")
    @Cascade(value = {CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    public AeTerminology getAeTerminology() {
        if (aeTerminology == null) {
            aeTerminology = new AeTerminology();
            aeTerminology.setStudy(this);
        }
        return aeTerminology;
    }

    public void setAeTerminology(final AeTerminology aeTerminology) {
        this.aeTerminology = aeTerminology;
    }

    @Override
    @OneToMany
    @Cascade({CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "STU_ID")
    @OrderBy
    public List<Identifier> getIdentifiers() {
        return lazyListHelper.getInternalList(Identifier.class);
    }

    @Override
    public void setIdentifiers(final List<Identifier> identifiers) {
        lazyListHelper.setInternalList(Identifier.class, identifiers);
    }

    @Transient
    @UniqueObjectInCollection(message = "Duplicates found in Identifiers list")
    public List<Identifier> getIdentifiersLazy() {
        return lazyListHelper.getLazyList(Identifier.class);
    }

    @Transient
    public void setIdentifiersLazy(final List<Identifier> identifiers) {
        setIdentifiers(identifiers);
    }

    @OneToMany(mappedBy = "study", fetch = FetchType.LAZY)
    @Cascade(value = {CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    @OrderBy
    public List<StudyAgent> getStudyAgentsInternal() {
        return lazyListHelper.getInternalList(StudyAgent.class);
    }

    public void setStudyAgentsInternal(final List<StudyAgent> studyAgents) {
        lazyListHelper.setInternalList(StudyAgent.class, studyAgents);
    }

    @OneToMany
    @JoinColumn(name = "study_id", nullable = false)
    @Cascade(value = {CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    @Where(clause = "term_type = 'ctep'")
    @OrderBy
    @UniqueObjectInCollection(message = "Duplicates found in CtepStudyDiseases list")
    // it is pretty lame that this is necessary
    public List<CtepStudyDisease> getCtepStudyDiseases() {
        return ctepStudyDiseases;
    }

    @Transient
    public List<CtepStudyDisease> getActiveCtepStudyDiseases() {
        return CollectionUtil.getActiveObjects(getCtepStudyDiseases());
    }

    public void setCtepStudyDiseases(final List<CtepStudyDisease> ctepStudyDiseases) {
        this.ctepStudyDiseases = ctepStudyDiseases;
    }

    @OneToMany
    @JoinColumn(name = "study_id", nullable = false)
    @Cascade(value = {CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    @Where(clause = "term_type = 'meddra'")
    @OrderBy
    @UniqueObjectInCollection(message = "Duplicates found in MeddraStudyDiseases list")
    // it is pretty lame that this is necessary
    public List<MeddraStudyDisease> getMeddraStudyDiseases() {
        return meddraStudyDiseases;
    }

    @Transient
    public List<MeddraStudyDisease> getActiveMeddraStudyDiseases() {
        return CollectionUtil.getActiveObjects(getMeddraStudyDiseases());
    }

    public void setMeddraStudyDiseases(final List<MeddraStudyDisease> meddraStudyDiseases) {
        this.meddraStudyDiseases = meddraStudyDiseases;
    }

    public Boolean getBlindedIndicator() {
        return blindedIndicator;
    }

    public void setBlindedIndicator(final Boolean blindedIndicator) {
        this.blindedIndicator = blindedIndicator;
    }

    @Deprecated
    public Boolean getMultiInstitutionIndicator() {
        return multiInstitutionIndicator;
    }

    @Deprecated
    public void setMultiInstitutionIndicator(final Boolean multiInstitutionIndicator) {
        this.multiInstitutionIndicator = multiInstitutionIndicator;
    }

    public Boolean getRandomizedIndicator() {
        return randomizedIndicator;
    }

    public void setRandomizedIndicator(final Boolean randomizedIndicator) {
        this.randomizedIndicator = randomizedIndicator;
    }

    @Transient @Deprecated
    public String getDescription() {
        return description;
    }

    @Deprecated
    public void setDescription(final String descriptionText) {
        description = descriptionText;
    }

    public String getDiseaseCode() {
        return diseaseCode;
    }

    public void setDiseaseCode(final String diseaseCode) {
        this.diseaseCode = diseaseCode;
    }

    @Transient @Deprecated
    public String getLongTitle() {
        return longTitle;
    }

    @Deprecated
    public void setLongTitle(final String longTitleText) {
        longTitle = longTitleText;
    }

    public String getMonitorCode() {
        return monitorCode;
    }

    public void setMonitorCode(final String monitorCode) {
        this.monitorCode = monitorCode;
    }

    @Transient
    public String getPhaseCode() {
        return phaseCode;
    }

    public void setPhaseCode(final String phaseCode) {
        this.phaseCode = phaseCode;
    }

    @Deprecated
    public String getPrecis() {
        return precis;
    }

    @Deprecated
    public void setPrecis(final String precisText) {
        precis = precisText;
    }

    @Transient
    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(final String shortTitleText) {
        shortTitle = shortTitleText;
    }

    @Transient @Deprecated
    public String getStatus() {
        return status;
    }

    @Deprecated
    public void setStatus(final String status) {
        this.status = status;
    }

    public Integer getTargetAccrualNumber() {
        return targetAccrualNumber;
    }

    public void setTargetAccrualNumber(final Integer targetAccrualNumber) {
        this.targetAccrualNumber = targetAccrualNumber;
    }
    
    @Transient
    public String getExternalId() {
		return externalId;
	}
    
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

    @OneToMany(mappedBy = "study", fetch = FetchType.LAZY)
    @Cascade(value = {CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    @UniqueObjectInCollection(message = "Duplicates found in StudyOrganizations list")
    @OrderBy
    public List<StudyOrganization> getStudyOrganizations() {
        return studyOrganizations;
    }

    public void setStudyOrganizations(final List<StudyOrganization> studyOrganizations) {
        this.studyOrganizations = studyOrganizations;
        // initialize projected list for StudySite, StudyFundingSponsor and StudyCoordinatingCenter
        lazyListHelper.setInternalList(StudySite.class, new ProjectedList<StudySite>(
                this.studyOrganizations, StudySite.class));
        lazyListHelper.setInternalList(StudyFundingSponsor.class,
                new ProjectedList<StudyFundingSponsor>(this.studyOrganizations,
                        StudyFundingSponsor.class));
    }

    @OneToMany(mappedBy = "study", fetch = FetchType.LAZY)
    @Cascade(value = {CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    @OrderBy
    public List<TreatmentAssignment> getTreatmentAssignmentsInternal() {
        return lazyListHelper.getInternalList(TreatmentAssignment.class);
    }

    public void setTreatmentAssignmentsInternal(final List<TreatmentAssignment> treatmentAssignments) {
        lazyListHelper.setInternalList(TreatmentAssignment.class, treatmentAssignments);
    }

    @Transient
    @UniqueObjectInCollection(message = "Duplicates found in TreatmentAssignments list")
    public List<TreatmentAssignment> getTreatmentAssignments() {
        return lazyListHelper.getLazyList(TreatmentAssignment.class);
    }

    public void setTreatmentAssignments(final List<TreatmentAssignment> treatmentAssignments) {
        setTreatmentAssignmentsInternal(treatmentAssignments);
    }

    // TODO Why rules is still using primarySponsorCode... (check)
    @Transient
    public String getPrimarySponsorCode() {
        Organization sponsorOrg = getPrimaryFundingSponsorOrganization();
        if (sponsorOrg != null) {
            return sponsorOrg.getNciInstituteCode();
        }
        return null;
    }

    public Integer getLoadStatus() {
        return loadStatus;
    }

    public void setLoadStatus(Integer loadStatus) {
        this.loadStatus = loadStatus;
    }

    // ------------------------------------------------------------------------------------------------------------

    // TODO Below methods are to be removed.....

    // TODO check how to get rid of this???? (Admin module require this method)

    public void setPrimarySponsorCode(final String sponsorCode) {
        throw new UnsupportedOperationException(
                "'setPrimarySponsorCode', one should not access this method!");
    }

    // ToDo - this should be removed
    @Transient
    @Deprecated
    public List<StudyTherapy> getStudyTherapies() {

        List<StudyTherapy> therapies = new ArrayList<StudyTherapy>();
        
        if(isSurgeryPresent()) therapies.add(new StudyTherapy(this, StudyTherapyType.SURGERY));
        if(isDevicePresent()) therapies.add(new StudyTherapy(this, StudyTherapyType.DEVICE));
        if(isRadiationPresent()) therapies.add(new StudyTherapy(this, StudyTherapyType.RADIATION));
        if(isBehavioralPresent()) therapies.add(new StudyTherapy(this, StudyTherapyType.BEHAVIORAL));
        if(isDrugAdministrationPresent()) therapies.add(new StudyTherapy(this, StudyTherapyType.DRUG_ADMINISTRATION));
        if(hasTherapyOfType(StudyTherapyType.DIETARY_SUPPLEMENT)) therapies.add(new StudyTherapy(this, StudyTherapyType.DIETARY_SUPPLEMENT));
        if(hasTherapyOfType(StudyTherapyType.GENETIC)) therapies.add(new StudyTherapy(this, StudyTherapyType.GENETIC));
        if(hasTherapyOfType(StudyTherapyType.BIOLOGICAL_VACCINE)) therapies.add(new StudyTherapy(this, StudyTherapyType.BIOLOGICAL_VACCINE));
        if(hasTherapyOfType(StudyTherapyType.OTHER)) therapies.add(new StudyTherapy(this, StudyTherapyType.OTHER));
        
        return therapies;
    }


    @Transient
    @Deprecated
    public void addStudyTherapy(final StudyTherapy studyTherapy) {
        studyTherapies.add(studyTherapy);
    }

    public void removeIdentifier(final Identifier identifier) {
        getIdentifiers().remove(identifier);
    }

    @Transient
    @Deprecated
    public StudyTherapy getStudyTherapy(final StudyTherapyType studyTherapyType) {

        for (StudyTherapy studyTherapy : studyTherapies) {
            if (studyTherapy.getStudyTherapyType().equals(studyTherapyType)) {
                return studyTherapy;
            }

        }
        return null;
    }

    @Transient
    public List<SystemAssignedIdentifier> getSystemAssignedIdentifiers() {
        return new ProjectedList<SystemAssignedIdentifier>(getIdentifiersLazy(),
                SystemAssignedIdentifier.class);
    }

    @Transient
    public List<OrganizationAssignedIdentifier> getOrganizationAssignedIdentifiers() {
        return new ProjectedList<OrganizationAssignedIdentifier>(getIdentifiersLazy(),
                OrganizationAssignedIdentifier.class);
    }

    @Transient
    public CoordinatingCenter getCoordinatingCenter() {
        return coordinatingCenter;
    }

    @Transient
    public FundingSponsor getFundingSponsor() {
        return fundingSponsor;
    }

    public void setCoordinatingCenter(CoordinatingCenter coordinatingCenter) {
        this.coordinatingCenter = coordinatingCenter;
    }

    public void setFundingSponsor(FundingSponsor fundingSponsor) {
        this.fundingSponsor = fundingSponsor;
    }

    @Deprecated
    public Boolean getAdeersReporting() {
        return adeersReporting;
    }

    @Deprecated
    public void setAdeersReporting(Boolean adeersSubmission) {
        this.adeersReporting = adeersSubmission;
    }

    @Column(name = "design_code")
    @Type(type = "designCode")
    @Deprecated
    public Design getDesign() {
        return design;
    }

    @Deprecated
    public void setDesign(Design design) {
        this.design = design;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "study")
    @Cascade(value = {CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    @Deprecated
    public List<ReportFormat> getReportFormats() {
        return reportFormats;
    }

    @Deprecated
    public void setReportFormats(final List<ReportFormat> reportFormats) {
        this.reportFormats = reportFormats;
    }

    @Transient
    @Deprecated
    public void addReportFormat(final ReportFormat reportFormat) {
    	reportFormat.setStudy(this);
        reportFormats.add(reportFormat);
    }

    @Transient
    @Deprecated
    public void addReportFormatType(ReportFormatType reportFormatType) {
        ReportFormat rf = new ReportFormat();
        rf.setReportFormatType(reportFormatType);
        rf.setStudy(this);
        reportFormats.add(rf);
    }

    @Transient
    @Deprecated
    public ReportFormat getReportFormat(final ReportFormatType reportFormatType) {
        for (ReportFormat reportFormat : reportFormats) {
            if (reportFormat.getReportFormatType().equals(reportFormatType)) {
                return reportFormat;
            }

        }
        return null;
    }

    @Deprecated
    public void updateReportFormats(Boolean selected, ReportFormatType type) {
        if (selected == null) return;
        ReportFormat reportFormat = getReportFormat(type);
        if (selected && reportFormat == null) {
            ReportFormat adeersPDFReportFormat = new ReportFormat();
            adeersPDFReportFormat.setStudy(this);
            adeersPDFReportFormat.setReportFormatType(type);
            getReportFormats().add(adeersPDFReportFormat);
        } else if (!selected && reportFormat != null) {
            getReportFormats().remove(reportFormat);
        }
    }

    @Transient
    public Boolean getAdeersPDFType() {
        return getReportFormat(ReportFormatType.ADEERSPDF) != null;
    }

    @Deprecated
    public void setAdeersPDFType(final Boolean value) {
        updateReportFormats(value, ReportFormatType.ADEERSPDF);
    }

    @Transient @Deprecated
    public Boolean getCaaersXMLType() {
        return getReportFormat(ReportFormatType.CAAERSXML) != null;
    }

    @Deprecated
    public void setCaaersXMLType(final Boolean value) {
    	updateReportFormats(value, ReportFormatType.CAAERSXML);
    }

    @Transient @Deprecated
    public Boolean getCiomsPDFType() {
        return getReportFormat(ReportFormatType.CIOMSFORM) != null;
    }

    @Deprecated
    public void setCiomsPDFType(final Boolean value) {
        updateReportFormats(value, ReportFormatType.CIOMSFORM);
    }

    @Transient @Deprecated
    public Boolean getCiomsSaePDFType() {
        return getReportFormat(ReportFormatType.CIOMSSAEFORM) != null;
    }

    @Deprecated
    public void setCiomsSaePDFType(final Boolean value) {
        updateReportFormats(value, ReportFormatType.CIOMSSAEFORM);
    }

    @Transient @Deprecated
    public Boolean getDcpSAEPDFType() {
        return getReportFormat(ReportFormatType.DCPSAEFORM) != null;
    }

    @Deprecated
    public void setDcpSAEPDFType(final Boolean dcpSAEPDFType) {
        updateReportFormats(dcpSAEPDFType, ReportFormatType.DCPSAEFORM);
    }

    @Transient @Deprecated
    public Boolean getMedwatchPDFType() {
        return getReportFormat(ReportFormatType.MEDWATCHPDF) != null;
    }

    @Deprecated
    public void setMedwatchPDFType(final Boolean medwatchPDFType) {
        updateReportFormats(medwatchPDFType, ReportFormatType.MEDWATCHPDF);
    }

    @Transient
    public void addStudyTherapy(final StudyTherapyType studyTherapyType) {
        StudyTherapy studyTherapy = new StudyTherapy();
        studyTherapy.setStudy(this);
        studyTherapy.setStudyTherapyType(studyTherapyType);
        this.addStudyTherapy(studyTherapy);
    }

    /**
     * This method will find the email address of people associated with the role. 
     * @param roleName
     * @return
     */
    public List<String> findEmailAddressByRole(String roleName){
        List<String> emails = new ArrayList<String>();
        if(CollectionUtils.isNotEmpty(getActiveStudyOrganizations())){
        	for(StudyOrganization studyOrg : studyOrganizations){
            	emails.addAll(studyOrg.findEmailAddressByRole(roleName));
            }
        }
        return emails;
    }

    
    @OneToMany(fetch = FetchType.LAZY)
    @Cascade(value = {CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    @JoinColumn(name="study_id", nullable = false)
    @OrderBy("epochOrder")
    public List<Epoch> getEpochs() {
		return epochs;
	}
    
	public void setEpochs(List<Epoch> epochs) {
		this.epochs = epochs;
	}
	
	public boolean addEpoch(Epoch epoch){
		  return epochs.add(epoch);
	}	
		
	public boolean removeEpoch(Epoch epoch){
		  return epochs.remove(epoch);
	}
	
	/**
	 * This method will list all the {@link Epoch}s that are not retired. 
	 * @return
	 */
	@Transient
	public List<Epoch> getActiveEpochs(){
		List<Epoch> epochs = new ArrayList<Epoch>();
		List<Epoch> allEpochs = getEpochs();
		if(allEpochs != null ){
			for(Epoch epoch : allEpochs){
				if(epoch.isRetired()) continue;
				epochs.add(epoch);
			}
		}
		return epochs;
	}
	
	//this method is added to satisfy the UI requirements, so to be moved to command classs
	@Transient
	public Integer getTermCode(){
		return null;
	}
	//this method is added to satisfy the UI requirements, so to be moved to the command class
	public void setTermCode(Integer ignore){}
	
	public boolean containsSolicitedAE(Integer termID)
	{
        for(Epoch epoch : getEpochs())
        {
        	List<SolicitedAdverseEvent> listOfSolicitedAEs = epoch.getArms().get(0).getSolicitedAdverseEvents();
        	
        	for(SolicitedAdverseEvent solicitedAE : listOfSolicitedAEs)
        	{
        		if( solicitedAE.getCtcterm() != null)
        		{
        			
        			if( solicitedAE.getCtcterm().getId().equals( termID) )
        				return true;
        		}
        		else
        		{
        			if( solicitedAE.getLowLevelTerm().getId().equals( termID) )
        				return true;
        		}
        		
        	}
        		
        	
        }
		
		return false;
		
	}
	
	@Transient
	public List<CtcCategory> getCtcCategories(){
		if(ctcCategories.size() != 0)
			return ctcCategories;
		else
			if(aeTerminology != null && aeTerminology.getCtcVersion() != null)
				setCtcCategories(aeTerminology.getCtcVersion().getCategories());
		return ctcCategories;
	}
	
	public void setCtcCategories(List<CtcCategory> ctcCategories) {
		this.ctcCategories = ctcCategories;
	}
	
	@OneToOne
    @JoinColumn(name = "other_meddra_id")
	public MeddraVersion getOtherMeddra() {
		return otherMeddra;
	}
	
	public void setOtherMeddra(MeddraVersion otherMeddra) {
		this.otherMeddra = otherMeddra;
	}

    @OneToMany
    @JoinColumn(name = "study_id", nullable = false)
    @Cascade(value = {CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    @Where(clause = "term_type = 'dcp'")
    @OrderBy
    @UniqueObjectInCollection(message = "Duplicate - Same condition is associated to the study more than ones")
    public List<StudyCondition> getStudyConditions() {
        return studyConditions;
    }

    @Transient
    public List<StudyCondition> getActiveStudyConditions() {
        return CollectionUtil.getActiveObjects(getStudyConditions());
    }

    public void setStudyConditions(List<StudyCondition> studyConditions) {
        this.studyConditions = studyConditions;
    }

    @OneToMany
    @Fetch(value = org.hibernate.annotations.FetchMode.SUBSELECT)
    @JoinColumn(name = "study_id", nullable = false)
    @Cascade(value = {CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    @Where(clause = "term_type = 'ctep'")
    public List<ExpectedAECtcTerm> getExpectedAECtcTerms() {
        return expectedAECTCTerms;
    }

    public void setExpectedAECtcTerms(List<ExpectedAECtcTerm> expectedAECTCTerms) {
        this.expectedAECTCTerms = expectedAECTCTerms;
    }

    public void addExpectedAECtcTerm(final ExpectedAECtcTerm expectedAECtcTerm) {
        expectedAECtcTerm.setStudy(this);
        expectedAECTCTerms.add(expectedAECtcTerm);
    }

    @OneToMany
    @Fetch(value = org.hibernate.annotations.FetchMode.SUBSELECT)
    @JoinColumn(name = "study_id", nullable = false)
    @Cascade(value = {CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    @UniqueObjectInCollection(message = "Duplicate - Same term is associated to the study more than once")
    @Where(clause = "term_type = 'meddra'")    
    public List<ExpectedAEMeddraLowLevelTerm> getExpectedAEMeddraLowLevelTerms() {
        return expectedAEMeddraTerms;
    }

    public void setExpectedAEMeddraLowLevelTerms(List<ExpectedAEMeddraLowLevelTerm> expectedAEMeddraTerms) {
        this.expectedAEMeddraTerms = expectedAEMeddraTerms;
    }
    
    public void addExpectedAEMeddraLowLevelTerm(final ExpectedAEMeddraLowLevelTerm expectedAEMeddraLowLevelTerm) {
        expectedAEMeddraLowLevelTerm.setStudy(this);
        expectedAEMeddraTerms.add(expectedAEMeddraLowLevelTerm);
    }
    
    @Column(name="data_entry_status")
    public Boolean getDataEntryStatus(){
    	return dataEntryStatus;
    }
    public void setDataEntryStatus(Boolean dataEntryStatus) {
		this.dataEntryStatus = dataEntryStatus;
	}
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (shortTitle == null ? 0 : shortTitle.hashCode());
        result = prime * result + (longTitle == null ? 0 : longTitle.hashCode());
        result = prime * result + (description == null ? 0 : description.hashCode());
        result = prime * result + (precis == null ? 0 : precis.hashCode());
        result = prime * result + (getId() == null ? 0 : getId().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
    	boolean found = false;
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        final Study other = (Study) obj;
        if (getIdentifiers() == null) {
            if (other.getIdentifiers() != null) {
                return false;
            }
        } else {
        	for(Identifier identifier : getIdentifiers()){
        		for(Identifier otherIdentifier : other.getIdentifiers()){
        			if(identifier.equals(otherIdentifier)){
        				found = true;
        				break;
        			}
        		}
        	}
        	return found;
        }
        	
        return true;
    }
    
    /**
     * This method checks against the ASAEL, and tells whether the AE term is 
     * expected.
     * @param aeTerm
     * @return true , if expected term, false otherwise.
     */
    public boolean isExpectedAdverseEventTerm(DomainObject aeTerm) {
    	//CTC terminology is only supported currently
    	if(aeTerm != null && aeTerm instanceof CtcTerm){
    		List<ExpectedAECtcTerm> expectedAECtcTerms = this.getExpectedAECtcTerms();
            for (ExpectedAECtcTerm expectedAECtcTerm : expectedAECtcTerms) {
            	if(expectedAECtcTerm.getTerm().getId().equals(aeTerm.getId())) return true;
            }
    	}
    	//not expected.
    	return false; 
    }

    @SuppressWarnings("unchecked")
	public AbstractExpectedAE checkExpectedAEUniqueness() {
        List expectedAEs = null;
        if (this.getAeTerminology().getMeddraVersion() != null) expectedAEs = this.getExpectedAEMeddraLowLevelTerms();
        else if (this.getAeTerminology().getCtcVersion() != null) expectedAEs = this.getExpectedAECtcTerms();

        if (expectedAEs == null || expectedAEs.size() == 0) return null;

        Iterator it = expectedAEs.iterator();
        List aes = new ArrayList();
        while (it.hasNext()) {
            AbstractExpectedAE expectedAE = (AbstractExpectedAE)it.next();
            StringBuffer key = new StringBuffer(expectedAE.getTerm().getId().toString());
            if (expectedAE.isOtherRequired()) {
                if (((ExpectedAECtcTerm)expectedAE).getOtherMeddraTerm() == null) continue;
                key.append(((ExpectedAECtcTerm)expectedAE).getOtherMeddraTerm().getId().toString());
            }
            if (aes.contains(key.toString())) return expectedAE;
            aes.add(key.toString());
        }

        return null;
    }
    
    
    @Transient
    public List<StudyOrganization> getUniqueStudyOrganizations() {
        Set<Organization> set = new HashSet<Organization>();
        List<StudyOrganization> list = new ArrayList<StudyOrganization>();
        
        for (StudyOrganization so : getStudyOrganizations()) {
            if (set.add(so.getOrganization())) {
                list.add(so);
            }
        }
        return list;
    }

    @Transient
    /*
    *
    * @author Ion C. Olaru
    * This methods retrieves the ASAEL and update/add it to Study Expected AE list.
    * This is NOT removing terms from Study Expected AE list,
    * since the user may have Expected AEs added from the Study flow UI.  
    *
    * */
    public void synchronizeExpectedAEs() {
        // todo
    }

    public Boolean getVerbatimFirst() {
        return verbatimFirst;
    }

    public void setVerbatimFirst(Boolean verbatimFirst) {
        this.verbatimFirst = verbatimFirst;
    }

    public String getOtherTreatmentAssignment() {
        return otherTreatmentAssignment;
    }

    public void setOtherTreatmentAssignment(String otherTreatmentAssignment) {
        this.otherTreatmentAssignment = otherTreatmentAssignment;
    }
}
