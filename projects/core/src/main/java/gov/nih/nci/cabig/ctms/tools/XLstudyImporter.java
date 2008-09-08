package gov.nih.nci.cabig.ctms.tools;

import gov.nih.nci.cabig.caaers.CaaersSystemException;
import gov.nih.nci.cabig.caaers.dao.*;
import gov.nih.nci.cabig.caaers.dao.query.InvestigatorQuery;
import gov.nih.nci.cabig.caaers.domain.*;
import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome;
import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome.Message;
import gov.nih.nci.cabig.caaers.service.StudyImportServiceImpl;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class XLstudyImporter {


    POIFSFileSystem poifs;

    HSSFWorkbook wb;

    HSSFSheet studyInfoSheet;

    HSSFSheet agentInfoSheet;

    HSSFSheet diseaseInfoSheet;

    HSSFSheet tacInfoSheet;

    HSSFSheet orgInfoSheet;

    HSSFSheet investigatorInfoSheet;

    HSSFSheet therapyInfoSheet;

    // Study related objects
    Study study;

    OrganizationDao orgdao;

    CtcDao ctcdao;

    StudyDao studydao;

    AgentDao agentdao;

    InvestigationalNewDrugDao investigationalnewdrugdao;

    DiseaseTermDao diseasetermdao;

    InvestigatorDao investigatordao;

    StudyImportServiceImpl studyImportService;

    Organization primaryFundingSponsorOrganization;

    String primaryIdentifierString;

    Identifier primaryIdentifier;

    String localDocumentNumber;

    String studyTitle;

    String phaseCode;

    List<Identifier> identifiers;

    List<StudyOrganization> studyOrganizations;

    StudyFundingSponsor primaryStudyFundingSponsorOrganization;

    DiseaseTerminology diseaseTerminology;

    AeTerminology aeTerminology;

    DomainObjectImportOutcome<Study> studyImportOutcome;

    int rowCount = 0;

    public final String STUDY_SHEET_NAME = "admin info";

    public final String AGENT_SHEET_NAME = "agent info";

    public final String DISEASE_SHEET_NAME = "disease info";

    public final String TAC_SHEET_NAME = "TAC info";

    public final String ORG_SHEET_NAME = "organizations";

    public final String INVESTIGATOR_SHEET_NAME = "investigators";

    public final String THERAPY_SHEET_NAME = "therapies";

    public XLstudyImporter() {


    }


    public void importXLstudy(File inputFile) throws Exception {
        bootstrap(inputFile);
        syncInvestigators();
        boolean hasMoreStudies = true;

        while (rowCount > 0) {
            study = new Study();
            initializeStudy(study);
            setStudyAgents(study);
            setStudyDiseases(study);
            setStudyTreatmentAssignments(study);
            setStudyOrganizations(study);
            setStudyInvestigators(study);
            setStudyTherapies(study);
            validateStudy(study);
            rowCount--;
            System.out.println(rowCount);
        }
        System.out.println("\n Study import finished.");
    }

    private void bootstrap(File inputFile) throws Exception {

        poifs = new POIFSFileSystem(new FileInputStream(inputFile));

        // create a workbook out of the input stream
        wb = new HSSFWorkbook(poifs);
        studyInfoSheet = getSheet(STUDY_SHEET_NAME);
        agentInfoSheet = getSheet(AGENT_SHEET_NAME);
        diseaseInfoSheet = getSheet(DISEASE_SHEET_NAME);
        tacInfoSheet = getSheet(TAC_SHEET_NAME);
        orgInfoSheet = getSheet(ORG_SHEET_NAME);
        investigatorInfoSheet = getSheet(INVESTIGATOR_SHEET_NAME);
        therapyInfoSheet = getSheet(THERAPY_SHEET_NAME);
        rowCount = studyInfoSheet.getLastRowNum();
        rowCount--;

    }

    private void initializeStudy(Study study) {

    	primaryIdentifierString = getCellData(STUDY_SHEET_NAME, rowCount,
                studyInfoSheet.getRow(rowCount).getCell((short) 0));

        localDocumentNumber = getCellData(STUDY_SHEET_NAME, rowCount,
                studyInfoSheet.getRow(rowCount).getCell((short) 1));
        studyTitle = getCellData(STUDY_SHEET_NAME, rowCount, studyInfoSheet
                .getRow(rowCount).getCell((short) 2));
        phaseCode = getCellData(STUDY_SHEET_NAME, rowCount, studyInfoSheet
                .getRow(rowCount).getCell((short) 3));

        FundingSponsor fs = new FundingSponsor();
        StudyFundingSponsor sfs = new StudyFundingSponsor();
        Organization fo = new Organization();
        fo.setName("Cancer Therapy Evaluation Program");
        sfs.setOrganization(fo);
        fs.setStudyFundingSponsor(sfs);
        OrganizationAssignedIdentifier sfi = new OrganizationAssignedIdentifier();
        sfi.setValue(getCellData(STUDY_SHEET_NAME, rowCount, studyInfoSheet
                .getRow(rowCount).getCell((short) 0)));
        sfi.setPrimaryIndicator(true);
        fs.setOrganizationAssignedIdentifier(sfi);
        study.setFundingSponsor(fs);

        study.setShortTitle(localDocumentNumber);
        study.setLongTitle(studyTitle);
        aeTerminology = study.getAeTerminology();
        Ctc ctc = new Ctc();
        ctc.setName(getCellData(STUDY_SHEET_NAME, rowCount, studyInfoSheet
                .getRow(rowCount).getCell((short) 4)));
        aeTerminology.setCtcVersion(ctc);
        study.setAeTerminology(aeTerminology);
        study.setDescription(studyTitle);
        diseaseTerminology = study.getDiseaseTerminology();
        diseaseTerminology.setDiseaseCodeTerm(DiseaseCodeTerm.getByCode(1));
        study.setDiseaseTerminology(diseaseTerminology);
        study.setMultiInstitutionIndicator(true);
        study.setStatus(gov.nih.nci.cabig.caaers.domain.Study.STATUS_ACTIVE);
        study.setAdeersReporting(Boolean.TRUE);
        study.setPhaseCode(phaseCode);

    }

    private void setStudyAgents(Study study) {
        int agentRows = agentInfoSheet.getLastRowNum();
        List<StudyAgent> studyAgents = new ArrayList<StudyAgent>();
        StudyAgent studyAgent = null;
        INDType indType;
        String indNumber;
        List<StudyAgentINDAssociation> studyAgentINDAssociations;
        StudyAgentINDAssociation studyAgentINDAssociation;
        for (int agentRow = 1; agentRow <= agentRows; agentRow++) {
            if (primaryIdentifierString.equalsIgnoreCase(getCellData(
                    AGENT_SHEET_NAME, agentRow, agentInfoSheet.getRow(agentRow)
                            .getCell((short) 0)))) {
                studyAgent = new StudyAgent(agentdao
                        .getByNscNumber(getCellData(AGENT_SHEET_NAME, agentRow,
                        agentInfoSheet.getRow(agentRow).getCell(
                                (short) 2))));
                studyAgent.setStudy(study);
                indType = getCellData(AGENT_SHEET_NAME, agentRow,
                        agentInfoSheet.getRow(agentRow).getCell((short) 4))
                        .equalsIgnoreCase("Investigational") ? INDType.CTEP_IND
                        : INDType.NA_COMMERCIAL;
                studyAgent.setIndType(indType);

                studyAgent.setPartOfLeadIND(getCellData(AGENT_SHEET_NAME,
                        agentRow,
                        (agentInfoSheet.getRow(agentRow).getCell((short) 3)))
                        .equalsIgnoreCase("Yes") ? true : false);
                indNumber = getCellData(AGENT_SHEET_NAME, agentRow,
                        agentInfoSheet.getRow(agentRow).getCell((short) 6));
                if (!(indNumber.equalsIgnoreCase("null"))) {
                    studyAgentINDAssociation = new StudyAgentINDAssociation();
                    studyAgentINDAssociation.setStudyAgent(studyAgent);
                    studyAgentINDAssociation
                            .setInvestigationalNewDrug(investigationalnewdrugdao
                                    .fetchCtepInd());
                    studyAgentINDAssociations = new ArrayList<StudyAgentINDAssociation>();
                    studyAgentINDAssociations.add(studyAgentINDAssociation);
                    studyAgent
                            .setStudyAgentINDAssociations(studyAgentINDAssociations);

                }

                studyAgents.add(studyAgent);

            }
        }
        study.setStudyAgents(studyAgents);
    }

    private void setStudyDiseases(Study study) {
        int diseaseRows = diseaseInfoSheet.getLastRowNum();
        List<CtepStudyDisease> ctepStudyDiseases = new ArrayList<CtepStudyDisease>();
        CtepStudyDisease ctepStudyDisease = null;
        String diseaseTermString = null;
        boolean leadDisease;
        for (int diseaseRow = 1; diseaseRow <= diseaseRows; diseaseRow++) {
            if (primaryIdentifierString.equalsIgnoreCase(getCellData(
                    DISEASE_SHEET_NAME, diseaseRow, diseaseInfoSheet.getRow(
                            diseaseRow).getCell((short) 0)))) {
                diseaseTermString = getCellData(DISEASE_SHEET_NAME, diseaseRow,
                        diseaseInfoSheet.getRow(diseaseRow).getCell((short) 1));
                ctepStudyDisease = new CtepStudyDisease();
                ctepStudyDisease.setDiseaseTerm(diseasetermdao
                        .getByCTEPTermName(diseaseTermString));
                leadDisease = getCellData(DISEASE_SHEET_NAME, diseaseRow,
                        diseaseInfoSheet.getRow(diseaseRow).getCell((short) 2))
                        .equalsIgnoreCase("Yes") ? true : false;
                ctepStudyDisease.setLeadDisease(leadDisease);
                ctepStudyDiseases.add(ctepStudyDisease);

            }

        }
        study.setCtepStudyDiseases(ctepStudyDiseases);
    }

    private void setStudyTreatmentAssignments(Study study) {
        int tacRows = tacInfoSheet.getLastRowNum();
        List<TreatmentAssignment> treatmentAssignments = new ArrayList<TreatmentAssignment>();
        TreatmentAssignment treatmentAssignment;

        for (int tacRow = 1; tacRow <= tacRows; tacRow++) {
            if (primaryIdentifierString.equalsIgnoreCase(getCellData(
                    TAC_SHEET_NAME, tacRow, tacInfoSheet.getRow(tacRow)
                            .getCell((short) 0)))) {
                treatmentAssignment = new TreatmentAssignment();
                treatmentAssignment.setCode(getCellData(TAC_SHEET_NAME, tacRow,
                        tacInfoSheet.getRow(tacRow).getCell((short) 1)));
                treatmentAssignment
                        .setDescription(getCellData(TAC_SHEET_NAME, tacRow,
                                tacInfoSheet.getRow(tacRow).getCell((short) 2)));
                treatmentAssignment.setStudy(study);
                treatmentAssignments.add(treatmentAssignment);

            }
        }
        study.setTreatmentAssignments(treatmentAssignments);
    }

    private void setStudyOrganizations(Study study) {
        int orgRows = orgInfoSheet.getLastRowNum();
        boolean leadOrg;
        StudyOrganization studyOrganization;
        Organization organization;

        for (int orgRow = 1; orgRow <= orgRows; orgRow++) {
            if (primaryIdentifierString.equalsIgnoreCase(getCellData(
                    ORG_SHEET_NAME, orgRow, orgInfoSheet.getRow(orgRow)
                            .getCell((short) 0)))) {
                leadOrg = getCellData(ORG_SHEET_NAME, orgRow,
                        orgInfoSheet.getRow(orgRow).getCell((short) 1))
                        .equalsIgnoreCase("Lead") ? true : false;
                organization = new Organization();
                organization.setName(getCellData(ORG_SHEET_NAME, orgRow,
                        orgInfoSheet.getRow(orgRow).getCell((short) 2)));
                organization.setNciInstituteCode(formatNCIcode(getCellData(
                        ORG_SHEET_NAME, orgRow, orgInfoSheet.getRow(orgRow)
                                .getCell((short) 3))));
                if (leadOrg) {
                    CoordinatingCenter cc = new CoordinatingCenter();
                    StudyCoordinatingCenter scc = new StudyCoordinatingCenter();
                    Organization cco = new Organization();
                    cco.setName(getCellData(ORG_SHEET_NAME, orgRow,
                            orgInfoSheet.getRow(orgRow).getCell((short) 2)));
                    cco.setNciInstituteCode(formatNCIcode(getCellData(
                            ORG_SHEET_NAME, orgRow, orgInfoSheet.getRow(orgRow)
                                    .getCell((short) 3))));
                    scc.setOrganization(cco);
                    cc.setStudyCoordinatingCenter(scc);
                    OrganizationAssignedIdentifier cci = new OrganizationAssignedIdentifier();
                    cci.setValue(localDocumentNumber);
                    cci.setPrimaryIndicator(false);
                    cc.setOrganizationAssignedIdentifier(cci);
                    study.setCoordinatingCenter(cc);

                } else {
                    StudySite ss = new StudySite();
                    ss.setOrganization(organization);
                    study.addStudyOrganization(ss);
                }

            }

        }
    }

    private void setStudyTherapies(Study study) {
        int therapyRows = therapyInfoSheet.getLastRowNum();
        for (int therapyRow = 1; therapyRow <= therapyRows; therapyRow++) {
            if (primaryIdentifierString.equalsIgnoreCase(getCellData(
                    THERAPY_SHEET_NAME, therapyRow, therapyInfoSheet.getRow(
                            therapyRow).getCell((short) 0)))) {

                String therapyString = getCellData(THERAPY_SHEET_NAME,
                        therapyRow, therapyInfoSheet.getRow(therapyRow)
                                .getCell((short) 1));

                if (getStudyTherapyType(therapyString).equals(
                        StudyTherapyType.DRUG_ADMINISTRATION)) {
                    study.setDrugAdministrationTherapyType(Boolean.TRUE);
                }
                if (getStudyTherapyType(therapyString).equals(
                        StudyTherapyType.RADIATION)) {
                    study.setRadiationTherapyType(Boolean.TRUE);
                }
            }
        }
    }

    private void setStudyInvestigators(Study study) {

        StudyCoordinatingCenter scc = study.getStudyCoordinatingCenter();
        setInvestigatorsInOrg(scc);
        for (StudySite ss : study.getStudySites()) {
            setInvestigatorsInOrg(ss);
        }
    }

    private void validateStudy(Study study) {
        studyImportOutcome = studyImportService.importStudy(study);
        if (studyImportOutcome.isSavable()) {
            saveStudy(studyImportOutcome.getImportedDomainObject());
        } else {
            List<DomainObjectImportOutcome.Message> messages = studyImportOutcome
                    .getMessages();
            System.out.println("Error: Unable to save study: "
                    + study.getShortTitle() + " -- " + study.getLongTitle());
            for (Message message : messages) {
                System.out.println("Reason: " + message.toString());

            }
        }
    }

    private void saveStudy(Study study) {
        study.setDescription(study.getDescription() + "xxxx");
        if(fetchStudy(study) == null){
        	System.out.println("\n saving study: " + study.getShortTitle() + " -- "
                    + study.getLongTitle());
        	studydao.save(study);
        }else{
        	System.out.println("Warn: Study Exists in caAERS: "
                    + study.getShortTitle() + " -- " + study.getLongTitle());
        }
        
    }


    public void setOrgdao(OrganizationDao orgdao) {
        this.orgdao = orgdao;
    }

    public CtcDao getCtcdao() {
        return ctcdao;
    }

    public void setCtcdao(CtcDao ctcdao) {
        this.ctcdao = ctcdao;
    }

    // utility method to get contents of cell irrespective of cell type.
    public String getCellData(String sheetname, int rowNum, HSSFCell cell) {
        if (cell == null)
            throw new CaaersSystemException(
                    "Invalid data or Blank cell at following location: \n Sheet: "
                            + sheetname + "\n Row: " + (rowNum + 1));

        int cellType = cell.getCellType();
        if (cellType == 0) {
            if (cell.getNumericCellValue() == 0)
                throw new CaaersSystemException(
                        "Invalid data or Blank cell at following location: \n Sheet: "
                                + sheetname + "\n Row: " + (rowNum + 1)
                                + "\n Cell: " + ((cell.getCellNum()) + 1));
            return (Integer.toString((int) cell.getNumericCellValue())).trim();
        } else if (cellType == 1) {
            if (cell.getStringCellValue().equals(""))
                throw new CaaersSystemException(
                        "Invalid data or Blank cell at following location: \n Sheet: "
                                + sheetname + "\n Row: " + (rowNum + 1)
                                + "\n Cell: " + ((cell.getCellNum()) + 1));
            return cell.getStringCellValue().trim();
        } else {
            throw new CaaersSystemException(
                    "Invalid data or Blank cell at following location: \n Sheet: "
                            + sheetname + "\n Row: " + (rowNum + 1)
                            + "\n Cell: " + ((cell.getCellNum()) + 1));
        }

    }

    // Converts 4 digit nci codes to 5 digits to correctly map to DB. Does not
    // apply to alphanumeric
    // strings.
    private String formatNCIcode(String nciCode) {

        if (nciCode.length() < 5) {
            try {
                int i = Integer.parseInt(nciCode);
                if (i / 10000 == 0)
                    nciCode = '0' + nciCode;

            } catch (NumberFormatException nfe) {
            }

        }
        return nciCode;
    }

    // utility method to map therapy type values from excel sheet to
    // StudyTherapyType enum
    private StudyTherapyType getStudyTherapyType(String studyTherapyString) {
        StudyTherapyType type = null;
        if (studyTherapyString.equalsIgnoreCase("Drug and/or Immunotherapy"))
            type = StudyTherapyType.DRUG_ADMINISTRATION;
        if (studyTherapyString.equalsIgnoreCase("Radiation Therapy"))
            type = StudyTherapyType.RADIATION;
        return type;
    }

    // create investigators if not already present in DB
    private void syncInvestigators() {
        int invRows = investigatorInfoSheet.getLastRowNum();
        List<Investigator> invList;
        Investigator inv;
        InvestigatorQuery iq;
        String invEmail;
        String invFax;
        String invPhone;
        SiteInvestigator siteInv;
        Organization org;
        String invNciId;
        String orgNciId;
        String statusMessage;
        for (int invRow = 1; invRow <= invRows; invRow++) {
            iq = new InvestigatorQuery();
            invNciId = getCellData(INVESTIGATOR_SHEET_NAME, invRow,
                    investigatorInfoSheet.getRow(invRow).getCell((short) 3));
            iq.filterByNciIdentifierExactMatch(invNciId);
            System.out.println(getCellData(INVESTIGATOR_SHEET_NAME, invRow,
                    investigatorInfoSheet.getRow(invRow).getCell((short) 3)));
            invList = investigatordao.searchInvestigator(iq);

            if (invList.size() == 0) {
            	statusMessage="Creating new investigator.";
                inv = new Investigator();
                inv.setNciIdentifier(invNciId);
                inv
                        .setLastName(getCellData(INVESTIGATOR_SHEET_NAME,
                                invRow, investigatorInfoSheet.getRow(invRow)
                                        .getCell((short) 4)));
                inv
                        .setFirstName(getCellData(INVESTIGATOR_SHEET_NAME,
                                invRow, investigatorInfoSheet.getRow(invRow)
                                        .getCell((short) 5)));

                invEmail = getCellData(INVESTIGATOR_SHEET_NAME, invRow,
                        investigatorInfoSheet.getRow(invRow).getCell((short) 6));
                if (invEmail.equalsIgnoreCase("null")) {
                    invEmail = "default@abc.com";
                }
                inv.setEmailAddress(invEmail);

                invPhone = getCellData(INVESTIGATOR_SHEET_NAME, invRow,
                        investigatorInfoSheet.getRow(invRow).getCell((short) 7));
                if (invPhone.equalsIgnoreCase("null")) {
                    invPhone = "(000)000-0000";
                }
                inv.setPhoneNumber(invPhone);

                invFax = getCellData(INVESTIGATOR_SHEET_NAME, invRow,
                        investigatorInfoSheet.getRow(invRow).getCell((short) 8));
                if (invFax.equalsIgnoreCase("null")) {
                    invFax = "(000)000-0000";
                }
                inv.setFaxNumber(invFax);

            } else {
            	statusMessage="This investigator exists in database. Only associated site investigators will be updated.";
                inv = invList.get(0);
            }

            orgNciId = formatNCIcode(getCellData(INVESTIGATOR_SHEET_NAME,
                    invRow, investigatorInfoSheet.getRow(invRow).getCell(
                            (short) 1)));
            org = orgdao.getByNCIcode(orgNciId.trim());

            boolean addSiteInv = true;
            List<SiteInvestigator> siteInvList = inv.getSiteInvestigators();
            for (SiteInvestigator existingSiteInv : siteInvList) {
                if (orgNciId.equals(existingSiteInv.getOrganization().getNciInstituteCode())) {
                    addSiteInv = false;
                    break;
                }
            }
            if (addSiteInv) {
                siteInv = new SiteInvestigator();
                siteInv.setOrganization(org);
                inv.addSiteInvestigator(siteInv);
            }
            investigatordao.save(inv);

            System.out.println(statusMessage);
            System.out.println(" Details: "+ inv.getLastFirst());
        }

    }

    private void setInvestigatorsInOrg(StudyOrganization so) {

        if (so == null) {
            return;
        }

        int invRows = investigatorInfoSheet.getLastRowNum();
        StudyInvestigator studyInvestigator;
        SiteInvestigator siteInvestigator;
        Investigator investigator;
        String invStudyId;
        String invOrgId;

        for (int invRow = 1; invRow <= invRows; invRow++) {
            invStudyId = getCellData(INVESTIGATOR_SHEET_NAME, invRow,
                    investigatorInfoSheet.getRow(invRow).getCell((short) 0));
            invOrgId = getCellData(INVESTIGATOR_SHEET_NAME, invRow,
                    investigatorInfoSheet.getRow(invRow).getCell((short) 1));
            if (invStudyId.equalsIgnoreCase(primaryIdentifierString)
                    && invOrgId.equalsIgnoreCase(so.getOrganization()
                    .getNciInstituteCode())) {
                investigator = new Investigator();
                investigator.setFirstName(getCellData(INVESTIGATOR_SHEET_NAME,
                        invRow, investigatorInfoSheet.getRow(invRow).getCell(
                                (short) 5)));
                investigator.setLastName(getCellData(INVESTIGATOR_SHEET_NAME,
                        invRow, investigatorInfoSheet.getRow(invRow).getCell(
                                (short) 4)));
                investigator.setNciIdentifier(getCellData(
                        INVESTIGATOR_SHEET_NAME, invRow, investigatorInfoSheet
                                .getRow(invRow).getCell((short) 3)));
                siteInvestigator = new SiteInvestigator();
                siteInvestigator.setInvestigator(investigator);
                studyInvestigator = new StudyInvestigator();
                studyInvestigator.setSiteInvestigator(siteInvestigator);
                studyInvestigator.setRoleCode(getCellData(
                        INVESTIGATOR_SHEET_NAME, invRow, investigatorInfoSheet
                                .getRow(invRow).getCell((short) 2)));
                studyInvestigator.setStatusCode("Active");
                so.addStudyInvestigators(studyInvestigator);
            }
        }

    }

    private HSSFSheet getSheet(String sheetName) {
        HSSFSheet sheet = wb.getSheet(sheetName);
        if (sheet == null)
            throw new CaaersSystemException("\n Unable to find sheet named: "
                    + sheetName + "\n Please fix the error and try again.");
        else
            return sheet;

    }
    
    /**
	 * This method fetches a Study from the DB based identifiers.
	 * @param importedStudy
	 * @return
	 */
	private Study fetchStudy(Study importedStudy){
		Study dbStudy = null;
		for (Identifier identifier : importedStudy.getIdentifiers()) {
            dbStudy = studydao.getStudyDesignByIdentifier(identifier);
            if(dbStudy != null){
            	break;
            }
            studydao.evict(dbStudy);
        }
		return dbStudy;
	}

    /*
      * // bringing back DB to original state to facilitate testing private void
      * cleanStudies() { for (int i = 1; i < studyInfoSheet.getLastRowNum(); i++) {
      * Study s = studydao.getByShortTitle(getCellData(STUDY_SHEET_NAME,
      * i,(studyInfoSheet.getRow(i) .getCell((short) 1)))); if (s != null)
      * studydao.delete(s); } }
      */
    public void setStudydao(StudyDao studydao) {
        this.studydao = studydao;
    }

    public void setAgentdao(AgentDao agentdao) {
        this.agentdao = agentdao;
    }

    public void setInvestigationalnewdrugdao(
            InvestigationalNewDrugDao investigationalnewdrugdao) {
        this.investigationalnewdrugdao = investigationalnewdrugdao;
    }

    public void setDiseasetermdao(DiseaseTermDao diseasetermdao) {
        this.diseasetermdao = diseasetermdao;
    }

    public void setInvestigatordao(InvestigatorDao investigatordao) {
        this.investigatordao = investigatordao;
    }

    public void setStudyImportService(StudyImportServiceImpl studyImportService) {
        this.studyImportService = studyImportService;
    }

}
