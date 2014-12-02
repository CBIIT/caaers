/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.api;

import gov.nih.nci.cabig.caaers.domain.*;
import gov.nih.nci.cabig.caaers.domain.ParticipantHistory.Measure;
import gov.nih.nci.cabig.caaers.domain.attribution.OtherCauseAttribution;
import gov.nih.nci.cabig.caaers.domain.meddra.LowLevelTerm;
import gov.nih.nci.cabig.caaers.domain.meddra.PreferredTerm;
import gov.nih.nci.cabig.caaers.domain.report.*;
import gov.nih.nci.cabig.caaers.utils.CaaersSerializerUtil;
import gov.nih.nci.cabig.caaers.utils.XmlMarshaller;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
* @author Srini
* @author Ion C. Olaru 
*
* */

public class AdverseEventReportSerializer {

	   //TODO:
	   // Added TreatmentAssignment in TreatmentInformation
	   // Removed TreatmentInformation.treatmentAssignmentCode

	   // Added StartDate and endDate in AdverseEvent
	   // Removed detection date from expedited adverse event report
	   //Removed getTreatmentAssignmentCode, from TreatmentInformation

	   //TO-DO set in spring config
	   private String mappingFile = "xml-mapping/ae-report-xml-mapping.xml";
	   

	   
	   public synchronized String serializeWithdrawXML (ExpeditedAdverseEventReport adverseEventReportDataObject,Report rpt) throws Exception{
		    String xml = "";
			XmlMarshaller marshaller = new XmlMarshaller();
			ExpeditedAdverseEventReport aer = new ExpeditedAdverseEventReport();
			aer.setId(adverseEventReportDataObject.getId());
			AdverseEventReportingPeriod reportingPeriod = new AdverseEventReportingPeriod();
		    aer.setReportingPeriod(reportingPeriod);
			
			List<Report> reports = adverseEventReportDataObject.getReports();
			int reportId = rpt == null ? 0 : rpt.getId();
	    	for (Report report: reports) {
	    		if (reportId > 0 ) {
	    			// generate report data only for selected report (when submitting to AdEERS)
	    			if (reportId == report.getId()) {
	    				   
	    				//Report latestReport = getReport(report);
	    				
	    				   Report latestReport = new Report();
	    				   latestReport.setId(report.getId());
	    				   latestReport.setAssignedIdentifer(report.getAssignedIdentifer());
	    				
	    				
	    				ReportVersion reportVersion = report.getLastVersion();
	    				ReportVersion latestVersion = latestReport.getLastVersion();
	    				latestVersion.setReportVersionId(reportVersion.getReportVersionId());
	    				aer.addReport(latestReport);
	    			}
	    		} 

	    	}
	    	
	    	StudyParticipantAssignment studyParticipantAssignment = new StudyParticipantAssignment();
	    	
	    	StudySite studySite = new StudySite();
	    	Study s = new LocalStudy();
		    	
	    	List<Identifier> ids = adverseEventReportDataObject.getAssignment().getStudySite().getStudy().getIdentifiers();
	    	//s.setIdentifiers(new ArrayList<Identifier>());
	    	for (Identifier id:ids) {
	    		s.addIdentifier(getIdentifier(id));
	    	}
	    	studySite.setStudy(s);
	    	
	    	studyParticipantAssignment.setStudySite(studySite);
		    studyParticipantAssignment.setStudySubjectIdentifier(adverseEventReportDataObject.getAssignment().getStudySubjectIdentifier());
	    	
		    aer.setAssignment(studyParticipantAssignment);
			xml = marshaller.toXML(aer,"xml-mapping/ae-report-withdraw-xml-mapping.xml");
		
			return xml;		   
	   }

    /**
     * Generates the XML representation of data collection, in the context of a specific report.
     * @param adverseEventReportDataObject - A data collection
     * @param report - A report
     * @return
     * @throws Exception
     */
	   public synchronized String serialize(ExpeditedAdverseEventReport adverseEventReportDataObject, Report report) throws Exception{
		   int reportId = report == null ? 0 : report.getId();
		   List<String> notApplicableFieldPaths = new ArrayList<String>();

		   if (report != null ) {
               notApplicableFieldPaths = report.getPathOfNotApplicableFields();
		   }

           String xml = "";
           XmlMarshaller marshaller = new XmlMarshaller();
           ExpeditedAdverseEventReport aer = this.getAdverseEventReport(adverseEventReportDataObject, reportId, notApplicableFieldPaths);
           xml = marshaller.toXML(aer, getMappingFile());

           return xml;
       }
	   
	  

	   /**
	    * @param hibernateAdverseEventReport
	    * @return This method is supposed to filter the AEReport based on the fields applicabilities from ReportDefinition
	    * @throws Exception
        * 
	    */
	   private ExpeditedAdverseEventReport getAdverseEventReport(ExpeditedAdverseEventReport hibernateAdverseEventReport, int reportId, List<String> notApplicableFieldPaths) throws Exception {

		    ExpeditedAdverseEventReport aer = new ExpeditedAdverseEventReport();
		    AdverseEventReportingPeriod reportingPeriod = new AdverseEventReportingPeriod();
		    aer.setReportingPeriod(reportingPeriod);
		    
	    	aer.setCreatedAt(hibernateAdverseEventReport.getCreatedAt());
	    	aer.setId(hibernateAdverseEventReport.getId());
	    	
	    	//aer.setStatus(hibernateAdverseEventReport.getStatus());	    	

	    	//build Reporter
	    	aer.setReporter(getReporter(hibernateAdverseEventReport.getReporter()));

	    	//build Physician
	    	aer.setPhysician(getPhysician(hibernateAdverseEventReport.getPhysician()));
	    	
	    	//build reviewer
	    	aer.setReviewer(getReviewer(hibernateAdverseEventReport.getReviewer()));

	    	//build AdverseEventResponseDescription
	    	aer.setResponseDescription(getAdverseEventResponseDescription(hibernateAdverseEventReport.getResponseDescription(), notApplicableFieldPaths));

	    	//build DiseaseHistory
	    	aer.setDiseaseHistory(getDiseaseHistory(hibernateAdverseEventReport.getDiseaseHistory(),notApplicableFieldPaths));

	    	//build Participant history
	    	aer.setParticipantHistory(getParticipantHistory(hibernateAdverseEventReport.getParticipantHistory()));

	    	//build StudyParticipantAssignment
	    	aer.setAssignment(getStudyParticipantAssignment(hibernateAdverseEventReport.getAssignment()));

	    	//build treatment info
	    	aer.setTreatmentInformation(getTreatmentInformation(hibernateAdverseEventReport.getTreatmentInformation(),notApplicableFieldPaths));

            if(!notApplicableFieldPaths.contains("investigationalDeviceAdministered")) {
                aer.setInvestigationalDeviceAdministered(hibernateAdverseEventReport.getInvestigationalDeviceAdministered());
            }
	    	//build MedicalDevices
	    	List<MedicalDevice> medicalDeviceList = CaaersSerializerUtil.filter(hibernateAdverseEventReport.getMedicalDevices());

	    	for (MedicalDevice medicalDevice: medicalDeviceList) {
                MedicalDevice md = getMedicalDevice(medicalDevice);
	    		if (md != null) aer.addMedicalDevice(md);
	    	}
	    	
	    	//	build RadiationInterventions
	    	List<RadiationIntervention> radiationInterventionList = CaaersSerializerUtil.filter(hibernateAdverseEventReport.getRadiationInterventions());

	    	for (RadiationIntervention radiationIntervention: radiationInterventionList) {
                RadiationIntervention ri = getRadiationIntervention(radiationIntervention);
	    		if (ri != null) aer.addRadiationIntervention(ri);
	    	}
	   	
	    	//	build SurgeryInterventions
	    	List<SurgeryIntervention> surgeryInterventionList = CaaersSerializerUtil.filter(hibernateAdverseEventReport.getSurgeryInterventions());

	    	for (SurgeryIntervention surgeryIntervention: surgeryInterventionList) {
                SurgeryIntervention si = getSurgeryIntervention(surgeryIntervention);
                if (si != null) aer.addSurgeryIntervention(getSurgeryIntervention(surgeryIntervention));
	    	}

            //behavioural
            List<BehavioralIntervention> behavioralInterventions = CaaersSerializerUtil.filter(hibernateAdverseEventReport.getBehavioralInterventions());
            if(behavioralInterventions != null){
              for(BehavioralIntervention bi : behavioralInterventions){
                if(bi != null && bi.getStudyIntervention() != null){
                    BehavioralIntervention biCopy = new BehavioralIntervention();
                    bi.copy(biCopy);
                    aer.addBehavioralIntervention(biCopy);
                }
              }
            }

            //biological
            List<BiologicalIntervention> biologicalInterventions = CaaersSerializerUtil.filter(hibernateAdverseEventReport.getBiologicalInterventions());
            if(biologicalInterventions != null){
                for(BiologicalIntervention bi : biologicalInterventions){
                    if(bi != null && bi.getStudyIntervention() != null){
                        BiologicalIntervention biCopy = new BiologicalIntervention();
                        bi.copy(biCopy);
                        aer.addBilogicalIntervention(biCopy);
                    }
                }
            }

            //genetic
            List<GeneticIntervention> geneticInterventions = CaaersSerializerUtil.filter(hibernateAdverseEventReport.getGeneticInterventions());
            if(geneticInterventions != null){
                for(GeneticIntervention gi : geneticInterventions){
                    if(gi != null && gi.getStudyIntervention() != null){
                        GeneticIntervention giCopy = new GeneticIntervention();
                        gi.copy(giCopy);
                        aer.addGeneticIntervention(giCopy);
                    }
                }
            }
            //dietaries
            List<DietarySupplementIntervention> dietarySupplementInterventions = CaaersSerializerUtil.filter(hibernateAdverseEventReport.getDietaryInterventions());
            if(dietarySupplementInterventions != null){
                for(DietarySupplementIntervention di : dietarySupplementInterventions){
                    DietarySupplementIntervention diCopy = new DietarySupplementIntervention();
                    di.copy(diCopy);
                    aer.addDietarySupplementalIntervention(diCopy);
                }
            }
            //other
            List<OtherAEIntervention> otherInterventions = CaaersSerializerUtil.filter(hibernateAdverseEventReport.getOtherAEInterventions());
            if(otherInterventions != null){
                for(OtherAEIntervention oi : otherInterventions){
                    OtherAEIntervention oiCopy = new OtherAEIntervention();
                    oi.copy(oiCopy);
                    aer.addOtherAEIntervention(oiCopy);
                }
            }

	    	aer.setAdditionalInformation(getAdditionalInformation(hibernateAdverseEventReport.getAdditionalInformation()));

	    	//build medications
	    	List<ConcomitantMedication> conMedList = CaaersSerializerUtil.filter(hibernateAdverseEventReport.getConcomitantMedications());

	    	for (ConcomitantMedication medication: conMedList) {
	    		aer.addConcomitantMedication(getConcomitantMedication(medication));
	    	}


	    	//build Labs
	    	List<Lab> labList = CaaersSerializerUtil.filter(hibernateAdverseEventReport.getLabs());

	    	for (Lab lab: labList) {
	    		aer.addLab(getLab(lab));
	    	}

	    	// build AEs
	    	List<AdverseEvent> aeList = CaaersSerializerUtil.filter(hibernateAdverseEventReport.getAdverseEvents());

	    	for (int i=0; i<aeList.size(); i++) {
	    		AdverseEvent ae = aeList.get(i);
	    		aer.addAdverseEvent(getAdverseEvent(ae,i));
	    	}

	    	//build therapies
	    	List<SAEReportPriorTherapy> thList = CaaersSerializerUtil.filter(hibernateAdverseEventReport.getSaeReportPriorTherapies());

	    	for (SAEReportPriorTherapy therapy: thList) {
	    		aer.addSaeReportPriorTherapies(getSAEReportPriorTherapy(therapy));
	    	}

	    	//Build pre existing conditions
	    	List<SAEReportPreExistingCondition> peList = CaaersSerializerUtil.filter(hibernateAdverseEventReport.getSaeReportPreExistingConditions());

	    	for (SAEReportPreExistingCondition pe: peList) {
	    		aer.addSaeReportPreExistingCondition(getSAEReportPreExistingCondition(pe));
	    	}

	    	//Build other causes
	    	List<OtherCause> ocList = CaaersSerializerUtil.filter(hibernateAdverseEventReport.getOtherCauses());

	    	for (OtherCause oc: ocList) {
	    		aer.addOtherCause(getOtherCause(oc));
	    	}
	    	
	    	//Build reports
	    	List<Report> reports = CaaersSerializerUtil.filter(hibernateAdverseEventReport.getReports());
	    	for (Report report: reports) {
	    		if (reportId > 0 ) {
	    			// generate report data only for selected report (when submitting to AdEERS)
	    			if (reportId == report.getId()) {
	    				Report latestReport = getReport(report);
	    				ReportVersion reportVersion = report.getLastVersion();
	    				ReportVersion latestVersion = latestReport.getLastVersion();
	    				latestVersion.setReportVersionId(reportVersion.getReportVersionId());
	    				aer.addReport(latestReport);
	    			}
	    		} 
	    		//else {
	    			//aer.addReport(getReport(report));
	    		//}
	    	}	    	
	    	
	    	/*
			BJ:FIXME
	    	List<Outcome> outcomes = hibernateAdverseEventReport.getOutcomes();
	    	
	    	for (Outcome oc: outcomes) {
	    		aer.addOutcomes(getOutcome(oc));
	    	}*/


	    	return aer;
	   }
/*
* 
* */
	   private Report getReport(Report report) throws Exception {
		   Report r = new Report();
		   r.setId(report.getId());
		   r.setSubmissionMessage(report.getSubmissionMessage());
           r.setSubmittedOn(report.getSubmittedOn());           
           r.setSubmitter(getSubmitter(report.getSubmitter()));
           r.setStatus(report.getStatus());
		   r.setAdeersReportTypeIndicator(report.deriveAdeersReportTypeIndicator());
		   r.setAssignedIdentifer(report.getAssignedIdentifer());
		   r.setReportDefinition(getReportDefinition(report, report.getReportDefinition()));
		   r.setEmailAddresses(report.getEmailRecipients());
           r.setMandatoryFields(report.getMandatoryFields());
           r.setCaseNumber(report.getCaseNumber());
           r.setMetaData(report.getMetaData());
           if(report.getReportDeliveries() != null)   {
              for(ReportDelivery rd : report.getReportDeliveries()){
                r.addReportDelivery(ReportDelivery.copy(rd));
              } 
           }

           // determine the FDA delivery
           if (report.getReportDefinition().getGroup().getCode().equals("RT_FDA")) {
               for (ReportDelivery rd : report.getReportDeliveries()) {
                   if (rd.getDeliveryStatus().equals(DeliveryStatus.DELIVERED)) {
                        r.setSubmittedToFDA("Yes");
                   }
               }
           }

		   return r;
	   }

	   private ReportDefinition getReportDefinition(Report report, ReportDefinition rd) throws Exception {
		   ReportDefinition reportDefinition = new ReportDefinition();
		   reportDefinition.setId(rd.getId());
		   reportDefinition.setDuration(rd.getDuration());
		   reportDefinition.setDescription(rd.getDescription());
           reportDefinition.setName(rd.getName());
		   reportDefinition.setLabel(rd.getLabel());
		   reportDefinition.setHeader(rd.getHeader());
		   reportDefinition.setFooter(rd.getFooter());
		   reportDefinition.setTimeScaleUnitType(rd.getTimeScaleUnitType());
		   reportDefinition.setGroup(rd.getGroup());
           reportDefinition.setDeliveryDefinitionsInternal(adjustDeliveryDefinitions(report, rd.getDeliveryDefinitions()));
		   return reportDefinition;
	   }

        /**
         * This method adds the deliveryStatus to every Delivery definition
         * The delivery status is computed from Report.deliveries.deliveryStatus
         * with delivery.reportDeliveryDefinition = current ReportDeliveryDefinition
         *
         * */
        private List<ReportDeliveryDefinition> adjustDeliveryDefinitions(Report report, List<ReportDeliveryDefinition> ddl) {
            if (report == null) return ddl;
            List<ReportDeliveryDefinition> rddList = new ArrayList<ReportDeliveryDefinition>();
            for (ReportDeliveryDefinition rdd : ddl) {
                for (ReportDelivery rd : report.getReportDeliveries()) {
                    if (rd.getReportDeliveryDefinition().getId().equals(rdd.getId())) {
                        rdd.setStatus(rd.getDeliveryStatus().getName());
                    }
                }
                rddList.add(ReportDeliveryDefinition.copy(rdd));
            }
            return rddList;
        }

	   private AdditionalInformation getAdditionalInformation (AdditionalInformation additionalInformation) throws Exception {
		   
		   AdditionalInformation a = new AdditionalInformation();
		   a.setId(additionalInformation.getId());
		   a.setOtherInformation(additionalInformation.getOtherInformation());
		   a.setAutopsyReport(additionalInformation.getAutopsyReport());
		   a.setConsults(additionalInformation.getConsults());
		   a.setDischargeSummary(additionalInformation.getDischargeSummary());
		   a.setFlowCharts(additionalInformation.getFlowCharts());
		   a.setLabReports(additionalInformation.getLabReports());
		   a.setObaForm(additionalInformation.getObaForm());
		   a.setOther(additionalInformation.getOther());
		   a.setPathologyReport(additionalInformation.getPathologyReport());
		   a.setProgressNotes(additionalInformation.getProgressNotes());
		   a.setRadiologyReports(additionalInformation.getRadiologyReports());
		   a.setReferralLetters(additionalInformation.getReferralLetters());
		   a.setIrbReport(additionalInformation.getIrbReport());
		   
		   
		   return a;
	   }
	   
	   private Outcome getOutcome(Outcome outcome) throws Exception {
		   Outcome o = new Outcome();
		   
		   o.setId(outcome.getId());
		   o.setOther(outcome.getOther());
		   o.setOutcomeType(outcome.getOutcomeType());
		   o.setDate(outcome.getDate());
		   
		   return o;
	   }
	   
	   private SAEReportPreExistingCondition getSAEReportPreExistingCondition(SAEReportPreExistingCondition saeReportPreExistingCondition) throws Exception {
		   
		   SAEReportPreExistingCondition s = new SAEReportPreExistingCondition();
		   s.setId(saeReportPreExistingCondition.getId());
		   s.setOther(saeReportPreExistingCondition.getOther());
		   s.setPreExistingCondition(saeReportPreExistingCondition.getPreExistingCondition());
		   
		   return s;
	   }
	   
	   private SAEReportPriorTherapy getSAEReportPriorTherapy(SAEReportPriorTherapy saeReportPriorTherapy) throws Exception {
		   
		   SAEReportPriorTherapy s = new SAEReportPriorTherapy();
		   s.setId(saeReportPriorTherapy.getId());
		   s.setPriorTherapy(saeReportPriorTherapy.getPriorTherapy());
		   s.setStartDate(saeReportPriorTherapy.getStartDate());
		   s.setEndDate(saeReportPriorTherapy.getEndDate());
		   s.setOther(saeReportPriorTherapy.getOther());
		   
		   List<PriorTherapyAgent> agents = saeReportPriorTherapy.getPriorTherapyAgents();
		   
		   for (PriorTherapyAgent agent : agents) {
			   PriorTherapyAgent pta = new PriorTherapyAgent();
			   pta.setId(agent.getId());
			   pta.setChemoAgent(agent.getChemoAgent());
               pta.setAgent(getAgent(agent.getAgent()));
			   s.addPriorTherapyAgent(pta);
		   }
		   
		   return s;
		   
	   }
	   private Lab getLab(Lab lab) throws Exception {
		   Lab l = new Lab();
		   l.setId(lab.getId());
		   l.setLabTerm(lab.getLabTerm());
		   l.setOther(lab.getOther());

		   l.setBaseline(lab.getBaseline());
		   l.setNadir(lab.getNadir());
		   l.setRecovery(lab.getRecovery());
		   l.setLabDate(lab.getLabDate());
		   l.setSite(lab.getSite());
           l.setNormalRange(lab.getNormalRange());
		   l.setInfectiousAgent(lab.getInfectiousAgent());
           //quick fix for
           if(lab.getNadir().getValue() != null || lab.getRecovery().getValue() != null || lab.getBaseline().getValue() != null || lab.getNormalRange()!= null  ){
               l.setUnits(lab.getUnits());
           }

		   
		   return l;
	   }
	   private ConcomitantMedication getConcomitantMedication (ConcomitantMedication concomitantMedication) throws Exception {
		   ConcomitantMedication c = new ConcomitantMedication();
		   c.setAgentName(concomitantMedication.getAgentName());
		   c.setStartDate(concomitantMedication.getStartDate());
		   c.setEndDate(concomitantMedication.getEndDate());
		   c.setStillTakingMedications(concomitantMedication.getStillTakingMedications());
		   return c;
		   
	   }
	   
	   private SurgeryIntervention getSurgeryIntervention(SurgeryIntervention surgeryIntervention) throws Exception{
           SurgeryIntervention s = new SurgeryIntervention();
           if (surgeryIntervention.getStudySurgery() != null){
               OtherIntervention oi = new OtherIntervention();
               oi.setId(surgeryIntervention.getStudySurgery().getId());
               oi.setName(surgeryIntervention.getStudySurgery().getName());
               oi.setDescription(surgeryIntervention.getStudySurgery().getDescription());
               s.setStudySurgery(oi);
           }
		   s.setId(surgeryIntervention.getId());
		   s.setTreatmentArm(surgeryIntervention.getTreatmentArm());
		   s.setDescription(surgeryIntervention.getDescription());
		   s.setInterventionDate(surgeryIntervention.getInterventionDate());
		   s.setInterventionSite(surgeryIntervention.getInterventionSite());
		   
		   return s;
		   
	   }



	   
	   private MedicalDevice getMedicalDevice(MedicalDevice medicalDevice) throws Exception {
           StudyDevice studyDevice = null;
           if (medicalDevice.getStudyDevice() != null){
               studyDevice = new StudyDevice();
               if (!medicalDevice.getStudyDevice().isOtherDevice()) {
                   Device device = new Device();
                   device.setBrandName(medicalDevice.getBrandName());
                   device.setCommonName(medicalDevice.getCommonName());
                   device.setType(medicalDevice.getDeviceType());
                   studyDevice.setDevice(device);
               } else {
                   studyDevice.setOtherBrandName(medicalDevice.getBrandName());
                   studyDevice.setOtherCommonName(medicalDevice.getCommonName());
                   studyDevice.setOtherDeviceType(medicalDevice.getDeviceType());
               }


               studyDevice.setManufacturerName(medicalDevice.getManufacturerName());
               studyDevice.setManufacturerCity(medicalDevice.getManufacturerCity());
               studyDevice.setManufacturerState(medicalDevice.getManufacturerState());
               studyDevice.setModelNumber(medicalDevice.getModelNumber());
               studyDevice.setCatalogNumber(medicalDevice.getCatalogNumber());
           }


           MedicalDevice m = new MedicalDevice(studyDevice);
           m.setId(medicalDevice.getId());
           m.setBrandName(medicalDevice.getBrandName());
		   m.setCommonName(medicalDevice.getCommonName());
		   m.setDeviceType(medicalDevice.getDeviceType());
		   m.setManufacturerName(medicalDevice.getManufacturerName());
		   m.setManufacturerCity(medicalDevice.getManufacturerCity());
		   m.setManufacturerState(medicalDevice.getManufacturerState());
		   m.setModelNumber(medicalDevice.getModelNumber());
		   m.setLotNumber(medicalDevice.getLotNumber());
		   m.setCatalogNumber(medicalDevice.getCatalogNumber());
		   m.setExpirationDate(medicalDevice.getExpirationDate());
		   m.setSerialNumber(medicalDevice.getSerialNumber());
		   m.setOtherNumber(medicalDevice.getOtherNumber());
		   m.setOtherDeviceOperator(medicalDevice.getOtherDeviceOperator());
		   m.setImplantedDate(medicalDevice.getImplantedDate());
		   m.setExplantedDate(medicalDevice.getExplantedDate());
		   m.setReprocessorName(medicalDevice.getReprocessorName());
		   m.setReprocessorAddress(medicalDevice.getReprocessorAddress());
		   m.setReturnedDate(medicalDevice.getReturnedDate());
		   m.setDeviceOperator(medicalDevice.getDeviceOperator());
		   m.setDeviceReprocessed(medicalDevice.getDeviceReprocessed());
		   m.setEvaluationAvailability(medicalDevice.getEvaluationAvailability());
		   return m;
	   }
	   
	   private RadiationIntervention getRadiationIntervention(RadiationIntervention ri) throws Exception {
           RadiationIntervention radiationIntervention = new RadiationIntervention();
           if (ri.getStudyRadiation() != null){
                OtherIntervention oi = new OtherIntervention();
                oi.setId(ri.getStudyRadiation().getId());
                oi.setName(ri.getStudyRadiation().getName());
                oi.setDescription(ri.getStudyRadiation().getDescription());
                radiationIntervention.setStudyRadiation(oi);
           }

		   try {
			   radiationIntervention.setId(ri.getId());
			   radiationIntervention.setDosage(ri.getDosage());
			   radiationIntervention.setDosageUnit(ri.getDosageUnit());
			   radiationIntervention.setLastTreatmentDate(ri.getLastTreatmentDate());
			   radiationIntervention.setFractionNumber(ri.getFractionNumber());
			   radiationIntervention.setDaysElapsed(ri.getDaysElapsed());
			   radiationIntervention.setAdjustment(ri.getAdjustment());
			   radiationIntervention.setAdministration(ri.getAdministration());
	    	} catch (Exception e) {
	    		throw new Exception ("Error building getRadiationIntervention() " + e.getMessage() , e);
	    	}
		    return radiationIntervention;
	   }
    
	   private Reporter getReporter(Reporter rptr) throws Exception {
	    	Reporter reporter = new Reporter();
	    	if(rptr == null) return reporter;
	    	try {
		    	reporter.setFirstName(rptr.getFirstName());
		    	reporter.setLastName(rptr.getLastName());
		    	reporter.setTitle(rptr.getTitle());
		    	reporter.setAddress(rptr.getAddress());
		    	reporter.setContactMechanisms(rptr.getContactMechanisms());
	    	} catch (Exception e) {
	    		throw new Exception ("Error building getReporter() "+e.getMessage() , e);
	    	}


	    	return reporter;
	    }

	    private Physician getPhysician(Physician psn) throws Exception {
	    	Physician physician = new Physician();
	    	if(psn == null) return physician;
	    	try {
	    		physician.setFirstName(psn.getFirstName());
	    		physician.setLastName(psn.getLastName());
	    		physician.setTitle(psn.getTitle());
	    		physician.setAddress(psn.getAddress());
	    		physician.setContactMechanisms(psn.getContactMechanisms());
	    	} catch (Exception e) {
	    		throw new Exception ("Error building getPhysician() "+e.getMessage() , e);
	    	}

	    	return physician;
	    }
	    
	    private Reporter getReviewer(Reporter psn) throws Exception {
	    	Reporter reviewer = new Reporter();
	    	if(psn == null) return reviewer;
	    	try {
	    		reviewer.setFirstName(psn.getFirstName());
	    		reviewer.setLastName(psn.getLastName());
	    		reviewer.setMiddleName(psn.getMiddleName());
	    		reviewer.setTitle(psn.getTitle());
	    		reviewer.setPhoneNumber(psn.getPhoneNumber());
	    		reviewer.setAddress(psn.getAddress());
	    		reviewer.setEmailAddress(psn.getEmailAddress());
	    		reviewer.setAlternateEmailAddress(psn.getAlternateEmailAddress());
	    	} catch (Exception e) {
	    		throw new Exception ("Error building getReviewer() "+e.getMessage() , e);
	    	}

	    	return reviewer;
	    }
	    
	    private Submitter getSubmitter(Submitter sbmtr) throws Exception {
	    	Submitter submitter = new Submitter();
	    	if(sbmtr == null) return submitter;
	    	try {
	    		submitter.setFirstName(sbmtr.getFirstName());
	    		submitter.setLastName(sbmtr.getLastName());
	    		submitter.setMiddleName(sbmtr.getMiddleName());
	    		submitter.setTitle(sbmtr.getTitle());
	    		submitter.setAddress(sbmtr.getAddress());
	    		submitter.setContactMechanisms(sbmtr.getContactMechanisms());
	    	} catch (Exception e) {
	    		throw new Exception ("Error building getSubmitter() "+e.getMessage() , e);
	    	}


	    	return submitter;
	    }

	    private ParticipantHistory getParticipantHistory(ParticipantHistory ph) throws Exception {
	    	ParticipantHistory participantHistory = new ParticipantHistory();
	    	if(ph == null) return participantHistory;
	    	try {
		    	participantHistory.getHeight().setQuantity(ph.getHeight().getQuantity());
		    	participantHistory.getHeight().setUnit(ph.getHeight().getUnit());
		    	participantHistory.getWeight().setQuantity(ph.getWeight().getQuantity());
		    	participantHistory.getWeight().setUnit(ph.getWeight().getUnit());
		    	participantHistory.setBaselinePerformanceStatus(ph.getBaselinePerformanceStatus());
		    	Double bsa = ph.getBodySurfaceArea();
		    	participantHistory.setBsa(bsa+"");
	    	} catch (Exception e) {
	    		throw new Exception ("Error building getParticipantHistory() "+e.getMessage() , e);
	    	}
	    	return participantHistory;
	    }

	    private AdverseEventResponseDescription getAdverseEventResponseDescription(AdverseEventResponseDescription aerd,List<String> notApplicableFieldPaths) throws Exception {
	    	AdverseEventResponseDescription adverseEventResponseDescription = new AdverseEventResponseDescription();
	    	if(aerd == null) {
	    		adverseEventResponseDescription.setEventAbate(EventStatus.NA);
	    		adverseEventResponseDescription.setEventReappear(EventStatus.NA);
	    		return adverseEventResponseDescription;
	    	}
	    	
	    	try {
		    	adverseEventResponseDescription.setEventDescription(aerd.getEventDescription());
		    	if (!notApplicableFieldPaths.contains("responseDescription.dateRemovedFromProtocol")) {
		    		adverseEventResponseDescription.setDateRemovedFromProtocol(aerd.getDateRemovedFromProtocol());
		    	} else {
		    		DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd hh:mm");
					try {
						Date someNotPossibleDate = dfm.parse("1678-01-01 00:00:00");
						adverseEventResponseDescription.setDateRemovedFromProtocol(someNotPossibleDate);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    		
		    	}
		    	adverseEventResponseDescription.setPresentStatus(aerd.getPresentStatus());

                if (!notApplicableFieldPaths.contains("responseDescription.recoveryDate"))
                    adverseEventResponseDescription.setRecoveryDate(aerd.getRecoveryDate());

                if (!notApplicableFieldPaths.contains("responseDescription.retreated"))
		    	    adverseEventResponseDescription.setRetreated(aerd.getRetreated());
		    	
		    	adverseEventResponseDescription.setBlindBroken(aerd.getBlindBroken());
		    	adverseEventResponseDescription.setStudyDrugInterrupted(aerd.getStudyDrugInterrupted());
		    	adverseEventResponseDescription.setReducedDose(aerd.getReducedDose());
		    	adverseEventResponseDescription.setReducedDate(aerd.getReducedDate());
		    	adverseEventResponseDescription.setDaysNotGiven(aerd.getDaysNotGiven());
		    	if (!notApplicableFieldPaths.contains("responseDescription.eventAbate")) {
		    		adverseEventResponseDescription.setEventAbate(aerd.getEventAbate());
		    	} else {
		    		adverseEventResponseDescription.setEventAbate(EventStatus.NA);
		    	}
		    	if (!notApplicableFieldPaths.contains("responseDescription.eventReappear")) {
		    		adverseEventResponseDescription.setEventReappear(aerd.getEventReappear());
		    	} else {
		    		adverseEventResponseDescription.setEventReappear(EventStatus.NA);
		    	}
		    	
		    	adverseEventResponseDescription.setAutopsyPerformed(aerd.getAutopsyPerformed());
		    	adverseEventResponseDescription.setCauseOfDeath(aerd.getCauseOfDeath());
		    	
		    	adverseEventResponseDescription.setPrimaryTreatment(aerd.getPrimaryTreatment());
		    	adverseEventResponseDescription.setPrimaryTreatmentApproximateTime(aerd.getPrimaryTreatmentApproximateTime());
		    	
		    	
	    	} catch (Exception e) {
	    		throw new Exception ("Error building getAdverseEventResponseDescription() "+e.getMessage() , e);
	    	}

	    	return adverseEventResponseDescription;

	    }

	    private DiseaseHistory getDiseaseHistory(DiseaseHistory dh,List<String> notApplicableFieldPaths) throws Exception {
	    	DiseaseHistory diseaseHistory = new DiseaseHistory();
	    	if(dh == null) return diseaseHistory;
	    	try {

                diseaseHistory.setOtherPrimaryDisease(dh.getOtherPrimaryDisease());
		    	diseaseHistory.setOtherPrimaryDiseaseSite(dh.getOtherPrimaryDiseaseSite());

                if (!notApplicableFieldPaths.contains("diseaseHistory.diagnosisDate.year")) {
		    		diseaseHistory.setDiagnosisDate(dh.getDiagnosisDate());
		    	}

		    	if (dh.getCodedPrimaryDiseaseSite() != null && !notApplicableFieldPaths.contains("diseaseHistory.codedPrimaryDiseaseSite")) {
		    		diseaseHistory.setCodedPrimaryDiseaseSite(getAnatomicSite(dh.getCodedPrimaryDiseaseSite()));
		    	}

                if (!notApplicableFieldPaths.contains("diseaseHistory.abstractStudyDisease"))
		    	    diseaseHistory.setAbstractStudyDisease(dh.getAbstractStudyDisease());
                
		    	List<MetastaticDiseaseSite> mdsList = CaaersSerializerUtil.filter(dh.getMetastaticDiseaseSites());
	
		    	for (MetastaticDiseaseSite site: mdsList) {
		    		diseaseHistory.addMetastaticDiseaseSite(site);
		    	}
	    	} catch (Exception e) {
	    		throw new Exception ("Error building getDiseaseHistory() "+e.getMessage() , e);
	    	}
	    	return diseaseHistory;
	    }
	    private AnatomicSite getAnatomicSite(AnatomicSite ash) {
	    	AnatomicSite site = new AnatomicSite();
	    	if(ash == null) return site;
	    	site.setId(ash.getId());
	    	site.setName(ash.getName());
	    	site.setCategory(ash.getCategory());
	    	return site;	    	
	    }
	    
	    private StudyParticipantAssignment getStudyParticipantAssignment(StudyParticipantAssignment spa) throws Exception {
	    	StudyParticipantAssignment studyParticipantAssignment = new StudyParticipantAssignment();
	    	if(spa == null) return spa; 
	    	try {
		    	studyParticipantAssignment.setParticipant(getParticipant(spa.getParticipant()));
		    	studyParticipantAssignment.setDateOfEnrollment(spa.getDateOfEnrollment());
		    	studyParticipantAssignment.setStudySite(getStudySite(spa.getStudySite()));
		    	studyParticipantAssignment.setStudySubjectIdentifier(spa.getStudySubjectIdentifier());

	    	} catch (Exception e) {
	    		throw new Exception ("Error building getStudyParticipantAssignment() "+e.getMessage() , e);
	    	}
	    	return studyParticipantAssignment;
	    }

	    private Participant getParticipant(Participant p) throws Exception {
	    	Participant participant = new Participant();
	    	if(p == null) return participant;
	    	try {
		    	participant.setInstitutionalPatientNumber(p.getInstitutionalPatientNumber());
		    	participant.setInstitution(p.getInstitution());
		    	participant.setFirstName(p.getFirstName());
		    	participant.setMaidenName(p.getMaidenName());
		    	participant.setMiddleName(p.getMiddleName());
		    	participant.setLastName(p.getLastName());
		    	participant.setDateOfBirth(p.getDateOfBirth());
		    	participant.setGender(p.getGender());
		    	participant.setRace(p.getRace());
		    	participant.setEthnicity(p.getEthnicity());
		    	//participant.setIdentifiers(p.getIdentifiers())
		    	for (Identifier id:p.getIdentifiers()) {
		    		participant.addIdentifier(getIdentifier(id));
		    	}
	    	} catch (Exception e) {
	    		throw new Exception ("Error building getParticipant() "+e.getMessage() , e);
	    	}
	    	return participant;
	    }
	    private Identifier getIdentifier(Identifier id) {	    	
	    	Identifier identifier = new Identifier();
	    	if(id == null) return identifier;
	    	identifier.setPrimaryIndicator(id.getPrimaryIndicator());
	    	identifier.setType(id.getType());
	    	identifier.setValue(id.getValue());
	    	return identifier;
	    }
	    private StudySite getStudySite(StudySite ss) throws Exception {
	    	StudySite studySite = new StudySite();
	    	
	    	if(ss == null) return studySite;
	    	//studySite.setIrbApprovalDate(ss.getIrbApprovalDate());
	    	//studySite.setRoleCode(ss.getRoleCode());
	    	try { 
		    	studySite.setStartDate(ss.getStartDate());
		    	studySite.setEndDate(ss.getEndDate());
		    	//buld identifiers , to resolve sesion error 
		    	Study hibernateStudy = ss.getStudy();
		    	
		    	Study s = new LocalStudy();
		    	s.setId(hibernateStudy.getId());
		    	s.setBlindedIndicator(hibernateStudy.getBlindedIndicator());
		    	s.setMultiInstitutionIndicator(hibernateStudy.getMultiInstitutionIndicator());
		    	s.setRandomizedIndicator(hibernateStudy.getRandomizedIndicator());
		    	s.setShortTitle(hibernateStudy.getShortTitle());
                s.setStudyPurpose(hibernateStudy.getStudyPurpose());
		    	s.setLongTitle(hibernateStudy.getLongTitle());
		    	s.setDescription(hibernateStudy.getDescription());
		    	s.setPrecis(hibernateStudy.getPrecis());
		    	s.setDiseaseCode(hibernateStudy.getDiseaseCode());
		    	s.setMonitorCode(hibernateStudy.getMonitorCode());
		    	s.setPhaseCode(hibernateStudy.getPhaseCode());
		    	s.setPrimaryFundingSponsorOrganization(hibernateStudy.getPrimaryFundingSponsorOrganization());
		    	s.setStatus(hibernateStudy.getStatus());
		    	s.setTargetAccrualNumber(hibernateStudy.getTargetAccrualNumber());
		    	s.setCtcVersion(hibernateStudy.getCtcVersion());
		    	s.setDesign(hibernateStudy.getDesign());

                if (hibernateStudy.getDiseaseTerminology().getDiseaseCodeTerm() == DiseaseCodeTerm.CTEP) {
                    List<CtepStudyDisease> dl = hibernateStudy.getCtepStudyDiseases();
                    for (CtepStudyDisease dis : dl) {
                        s.addCtepStudyDisease(getCtepStudyDisease(dis));
                    }
                }

                if (hibernateStudy.getDiseaseTerminology().getDiseaseCodeTerm() == DiseaseCodeTerm.MEDDRA) {
                    List<MeddraStudyDisease> dl = hibernateStudy.getMeddraStudyDiseases();
                    for (MeddraStudyDisease dis : dl) {
                        s.addMeddraStudyDisease(getMeddraStudyDisease(dis));
                    }
                }

                if (hibernateStudy.getDiseaseTerminology().getDiseaseCodeTerm() == DiseaseCodeTerm.OTHER) {
                    List<StudyCondition> dl = hibernateStudy.getStudyConditions();
                    for (StudyCondition dis : dl) {
                        s.addStudyCondition(getStudyCondition(dis));
                    }
                }

                List<StudyAgent> sas = hibernateStudy.getStudyAgents();
		    	for (StudyAgent sa:sas) {
		    		s.addStudyAgent(getStudyAgent(sa));
		    	}
		    	
		    	List<Identifier> ids = hibernateStudy.getIdentifiers();
		    	//s.setIdentifiers(new ArrayList<Identifier>());
		    	for (Identifier id:ids) {
		    		s.addIdentifier(getIdentifier(id));
		    	}
		    	studySite.setStudy(s);
		    	//studySite.setOrganization(ss.getOrganization());
                if(ss.getOrganization().isRetired() && ss.getOrganization().getMergedOrganization() != null){
                    studySite.setOrganization(getOrganization(ss.getOrganization().getMergedOrganization()));
                } else{
                    studySite.setOrganization(getOrganization(ss.getOrganization()));
                }
		    	
		    	
		    	studySite.setStudyInvestigators(ss.getStudyInvestigators());
	    	} catch (Exception e) {
	    		throw new Exception ("Error building getStudySite() "+e.getMessage() , e);
	    	}
	    	//System.out.println("STUDY INVES INTERNAL >>>>>>>>>>>>>>> " + ss.getStudyInvestigatorsInternal().size());
	    	return studySite;
	    }


        private CtepStudyDisease getCtepStudyDisease(CtepStudyDisease hcsd){
        	CtepStudyDisease csd = new CtepStudyDisease();
        	if(hcsd == null) return csd;
        	
            DiseaseTerm diseaseTerm = new DiseaseTerm();
            
            diseaseTerm.setCtepTerm(hcsd.getTerm().getCtepTerm());
            diseaseTerm.setId(hcsd.getTerm().getId());
            diseaseTerm.setMeddraCode(hcsd.getTerm().getMeddraCode());
            diseaseTerm.setTerm(hcsd.getTerm().getTerm());
            DiseaseCategory dc = new DiseaseCategory();
            dc.setName(hcsd.getTerm().getCategory().getName());
            diseaseTerm.setCategory(dc);
            
            csd.setDiseaseTerm(diseaseTerm);
            csd.setLeadDisease(hcsd.getLeadDisease());
            csd.setId(hcsd.getId());
            return csd;
        }
        private MeddraStudyDisease getMeddraStudyDisease(MeddraStudyDisease hmsd){    
        	MeddraStudyDisease md = new MeddraStudyDisease();
        	if(hmsd == null) return md;
        	
            LowLevelTerm l = new LowLevelTerm();            
            l.setId(hmsd.getId());
            l.setMeddraCode(hmsd.getTerm().getMeddraCode());
            l.setMeddraTerm(hmsd.getTerm().getMeddraTerm());
            MeddraVersion mv = new MeddraVersion();
            mv.setName(hmsd.getTerm().getMeddraVersion().getName());
            mv.setId(hmsd.getTerm().getMeddraVersion().getId());
            l.setMeddraVersion(mv);

            
            md.setTerm(l);
            md.setLeadDisease(hmsd.getLeadDisease());
            md.setId(hmsd.getId());
            return md;

        }
        private StudyCondition getStudyCondition(StudyCondition hsc){
        	StudyCondition sc = new StudyCondition();
        	if(hsc == null) return sc;
        	
        	Condition c = new Condition();
            
            c.setConditionName(hsc.getTerm().getConditionName());
            c.setId(hsc.getTerm().getId());
                        
            sc.setTerm(c);
            sc.setLeadDisease(hsc.getLeadDisease());
            sc.setId(hsc.getId());
            return sc;
        }
    
	    private StudyAgent getStudyAgent(StudyAgent sa) {
	    	StudyAgent studyAgent = new StudyAgent();
	    	if(sa == null) return studyAgent;
	    	
	    	studyAgent.setIndType(sa.getIndType());
	    	studyAgent.setAgent(getAgent(sa.getAgent()));
	    	studyAgent.setAgentAsString(sa.getAgentAsString());
	    	studyAgent.setOtherAgent(sa.getOtherAgent());
	    	return studyAgent;
	    }
    
        private Agent getAgent(Agent agent){
            if(agent == null) return  null;
            Agent a = new Agent();
            a.setName(agent.getName());
            a.setNscNumber(agent.getNscNumber());
            a.setDescription(agent.getDescription());
            a.setDisplayName(agent.getDisplayName());
            a.setRetiredIndicator(agent.getRetiredIndicator());
            return a;
        }
	    private Organization getOrganization(Organization org) {
	    	Organization organization = new LocalOrganization();
	    	if(org == null) return organization;
	    	
	    	organization.setId(org.getId());
	    	organization.setName(org.getName());
	    	organization.setNciInstituteCode(org.getNciInstituteCode());
	    	organization.setDescriptionText(org.getDescriptionText());
	    	
	    	List<SiteInvestigator> siList = new ArrayList<SiteInvestigator>();
	    	
	    	for (SiteInvestigator si:org.getSiteInvestigators()) {
	    		siList.add(si);
	    	}
	    	organization.setSiteInvestigators(siList);
	    	
	    	return organization;
	    }
	    protected AdverseEvent getAdverseEvent(AdverseEvent ae , int seq) throws Exception {
	    	AdverseEvent adverseEvent = new AdverseEvent();
	    	if(ae == null) return adverseEvent;
	    	try {
	    		adverseEvent.setExternalId(ae.getExternalId());
		    	adverseEvent.setDetailsForOther(ae.getDetailsForOther());
		    	adverseEvent.setExpected(ae.getExpected());
		    	adverseEvent.setComments(ae.getComments());
		    	adverseEvent.setStartDate(ae.getStartDate());
		    	adverseEvent.setEndDate(ae.getEndDate());
		    	adverseEvent.setConcomitantMedicationAttributions(ae.getConcomitantMedicationAttributions());
		    	adverseEvent.setOtherSpecify(ae.getOtherSpecify());
	
		    	List<OtherCauseAttribution> otList = new ArrayList<OtherCauseAttribution>();
	
		    	for (OtherCauseAttribution ot : ae.getOtherCauseAttributions()) {
		    		otList.add(getOtherCauseAttribution(ot));
		    	}
	
		    	adverseEvent.setOtherCauseAttributions(otList);
		    	adverseEvent.setCourseAgentAttributions(ae.getCourseAgentAttributions());
	
		    	
		    	adverseEvent.setDiseaseAttributions(ae.getDiseaseAttributions());
		    	adverseEvent.setSurgeryAttributions(ae.getSurgeryAttributions());
		    	adverseEvent.setRadiationAttributions(ae.getRadiationAttributions());
		    	adverseEvent.setDeviceAttributions(ae.getDeviceAttributions());
                adverseEvent.setOtherInterventionAttributions(ae.getOtherInterventionAttributions());
                adverseEvent.setBehavioralInterventionAttributions(ae.getBehavioralInterventionAttributions());
                adverseEvent.setBiologicalInterventionAttributions(ae.getBiologicalInterventionAttributions());
                adverseEvent.setGeneticInterventionAttributions(ae.getGeneticInterventionAttributions());
                adverseEvent.setDietarySupplementInterventionAttributions(ae.getDietarySupplementInterventionAttributions());

                AbstractAdverseEventTerm aeTerm = ae.getAdverseEventTerm();
                
                if(aeTerm instanceof  AdverseEventMeddraLowLevelTerm){
                    adverseEvent.getAdverseEventMeddraLowLevelTerm().setLowLevelTerm(getLowLevelTerm(ae.getAdverseEventMeddraLowLevelTerm().getLowLevelTerm()));
                } else {
                    adverseEvent.getAdverseEventCtcTerm().setCtcTerm(getCtcTerm(ae.getAdverseEventCtcTerm().getCtcTerm()));
                }

				adverseEvent.setLowLevelTerm(getLowLevelTerm(ae.getLowLevelTerm()));
	
				
		    	adverseEvent.setHospitalization(ae.getHospitalization());
		    	adverseEvent.setGrade(ae.getGrade());
		    	adverseEvent.setAttributionSummary(ae.getAttributionSummary());
		    	adverseEvent.setExpected(ae.getExpected());

                /**
                 *  The field gridId is used to store both the primary and the gridId value
                 *  since gridId is not used in any of the XMLs nor XSLTs files to generate exports.
                 * */
		    	if (seq == 0 ) {
		    		adverseEvent.setGridId("PRY"+ae.getGridId());
		    	} else {
		    		adverseEvent.setGridId("PRN"+ae.getGridId());
		    	}
		    	adverseEvent.setEventApproximateTime(ae.getEventApproximateTime());
		    	adverseEvent.setEventLocation(ae.getEventLocation());
		    	adverseEvent.setParticipantAtRisk(ae.getParticipantAtRisk());

		    	List<Outcome> outcomes = ae.getOutcomes();
		    	
		    	for (Outcome oc: outcomes) {
		    		adverseEvent.addOutcome(getOutcome(oc));
		    	}
		    	
	    	} catch (Exception e) {
	    		throw new Exception ("Error building getAdverseEvent() "+e.getMessage() , e);
	    	}

	    	return adverseEvent;
	    }
	    /**
	     * This method will return a copy of the CTCTerm from the given term
	     * @param ctcTerm
	     * @return
	     */
	    private CtcTerm getCtcTerm(CtcTerm ctcTerm){
	    	CtcTerm term = new CtcTerm();
	    	if(ctcTerm == null) return term;
	    	
            CtcCategory category = new CtcCategory();
            term.setCategory(category);
            if(ctcTerm != null){
                term.setId(ctcTerm.getId());
                term.setTerm(ctcTerm.getTerm());
                term.setCtepTerm(ctcTerm.getCtepTerm());
                term.setCtepCode(ctcTerm.getCtepCode());
                term.setSelect(ctcTerm.getSelect());
                term.setOtherRequired(ctcTerm.isOtherRequired());

                category.setId(ctcTerm.getCategory().getId());
                category.setName(ctcTerm.getCategory().getName());
            }
	    	return term;
	    }
    
        private LowLevelTerm getLowLevelTerm(LowLevelTerm llt){
           LowLevelTerm term = new LowLevelTerm();
           if(llt != null){
               term.setMeddraCode(llt.getMeddraCode());
               term.setPreferredTerm(getPreferredTerm(llt.getPreferredTerm()));
               term.setMeddraVersion(getMeddraVersion(llt.getMeddraVersion()));
           }
           return term;
        }
    
        private PreferredTerm getPreferredTerm(PreferredTerm pt){
            PreferredTerm term = new PreferredTerm();
            if(pt != null){
                term.setMeddraCode(pt.getMeddraCode());
                term.setMeddraTerm(pt.getMeddraTerm());
                term.setHighLevelTerms(pt.getHighLevelTerms());
                term.setMeddraVersion(getMeddraVersion(pt.getMeddraVersion()));
            }
            return term;
            
        }
    
        private MeddraVersion getMeddraVersion(MeddraVersion mv){
            MeddraVersion v = new MeddraVersion();
            if(mv != null){
                v.setName(mv.getName());
                v.setId(mv.getId());
            }
            return v;
        }

	    private OtherCauseAttribution getOtherCauseAttribution(OtherCauseAttribution oca) throws Exception {
	    	OtherCauseAttribution otherCauseAttribution = new OtherCauseAttribution();
	    	
	    	if(oca == null) return otherCauseAttribution;
	    	try {
	    		otherCauseAttribution.setAttribution(oca.getAttribution());
	    		otherCauseAttribution.setCause(getOtherCause(oca.getCause()));
	    	} catch (Exception e) {
	    		throw new Exception ("Error building getOtherCauseAttribution() "+e.getMessage() , e);
	    	}
	    	return otherCauseAttribution;
	    }


	    private OtherCause getOtherCause(OtherCause oc) throws Exception {
	    	OtherCause otherCause = new OtherCause();
	    	if(oc == null) return otherCause;
		    try {
		    	otherCause.setText(oc.getText());
	    	} catch (Exception e) {
	    		throw new Exception ("Error building getOtherCause() "+e.getMessage() , e);
	    	}
	    	return otherCause;

	    }

	    private TreatmentInformation getTreatmentInformation(TreatmentInformation trtInf,List<String> notApplicableFieldPaths) throws Exception {
	    	TreatmentInformation treatmentInformation = new TreatmentInformation();
	    	// String field = "treatmentInformation";
	    	
	    	if(trtInf == null) return treatmentInformation;
	    	
	    	try {

                if (!notApplicableFieldPaths.contains("treatmentInformation.firstCourseDate"))
	    		    treatmentInformation.setFirstCourseDate(trtInf.getFirstCourseDate());

                if (!notApplicableFieldPaths.contains("treatmentInformation.adverseEventCourse.date"))
		    	    treatmentInformation.setAdverseEventCourse(trtInf.getAdverseEventCourse());

                if (!notApplicableFieldPaths.contains("treatmentInformation.totalCourses"))
		    	    treatmentInformation.setTotalCourses(trtInf.getTotalCourses());

		    	treatmentInformation.setTreatmentDescription(trtInf.getTreatmentDescription());

	    		if (!notApplicableFieldPaths.contains("treatmentInformation.investigationalAgentAdministered")) {	
	    			treatmentInformation.setInvestigationalAgentAdministered(trtInf.getInvestigationalAgentAdministered());
	    		}
		    	TreatmentAssignment ta = trtInf.getTreatmentAssignment();
		    	
		    	if (ta != null ) {
			    	TreatmentAssignment taNew = new TreatmentAssignment();
			    	taNew.setCode(ta.getCode());
			    	taNew.setDoseLevelOrder(ta.getDoseLevelOrder());
			    	taNew.setDescription(ta.getDescription());
			    	taNew.setComments(ta.getComments());
			    	taNew.setStudy(ta.getStudy());
			    	
			    	treatmentInformation.setTreatmentAssignment(taNew);
		    	}
	
		    	List<CourseAgent> caList = CaaersSerializerUtil.filter(trtInf.getCourseAgents());
	
		    	for (CourseAgent ca: caList) {
		    		CourseAgent ca1 = new CourseAgent();
		    		ca1.setId(ca.getId());

                    if (!notApplicableFieldPaths.contains("treatmentInformation.courseAgents[].firstAdministeredDate")) {
                        ca1.setFirstAdministeredDate(ca.getFirstAdministeredDate());
                    }

                    if (!notApplicableFieldPaths.contains("treatmentInformation.courseAgents[].lastAdministeredDate")) {
                        ca1.setLastAdministeredDate(ca.getLastAdministeredDate());
                    }
                    
		    		if (!notApplicableFieldPaths.contains("treatmentInformation.courseAgents[].administrationDelayAmount")) {
		    			ca1.setAdministrationDelayAmount(ca.getAdministrationDelayAmount());
		    		} else {
		    			ca1.setAdministrationDelayAmount(new BigDecimal(-1));
		    		}

                    if (!notApplicableFieldPaths.contains("treatmentInformation.courseAgents[].administrationDelayUnits")) {
		    		    ca1.setAdministrationDelayUnits(ca.getAdministrationDelayUnits());
                    }
                    
		    		ca1.setDose(ca.getDose());
		    		//ca1.setModifiedDose(ca.getModifiedDose());

                    if (!notApplicableFieldPaths.contains("treatmentInformation.courseAgents[].agentAdjustment")) {
                        ca1.setAgentAdjustment(ca.getAgentAdjustment());
                    }

		    		if (ca.getStudyAgent() != null) {
		    			ca1.setStudyAgent(getStudyAgent(ca.getStudyAgent()));
		    		}
                    
		    		ca1.setTotalDoseAdministeredThisCourse(ca.getTotalDoseAdministeredThisCourse());

                    ca1.setFormulation(ca.getFormulation());

                    if (!notApplicableFieldPaths.contains("treatmentInformation.courseAgents[].lotNumber")) {
		    		    ca1.setLotNumber(ca.getLotNumber());
                    }

		    		treatmentInformation.addCourseAgent(ca1);
		    	}
		    	
	    	} catch (Exception e) {
	    		throw new Exception ("Error building getTreatmentInformation() "+e.getMessage() , e);
	    	}

	    	return treatmentInformation;
	    }

		public String getMappingFile() {
			return mappingFile;
		}



}
