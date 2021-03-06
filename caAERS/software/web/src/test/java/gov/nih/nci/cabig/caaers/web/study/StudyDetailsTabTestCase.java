/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.web.study;

import gov.nih.nci.cabig.caaers.domain.Ctc;
import gov.nih.nci.cabig.caaers.domain.Identifier;
import gov.nih.nci.cabig.caaers.domain.LocalOrganization;
import gov.nih.nci.cabig.caaers.domain.MeddraVersion;
import gov.nih.nci.cabig.caaers.domain.Organization;
import gov.nih.nci.cabig.caaers.domain.OrganizationAssignedIdentifier;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.StudyCoordinatingCenter;
import gov.nih.nci.cabig.caaers.domain.StudyOrganization;
import gov.nih.nci.cabig.caaers.domain.SystemAssignedIdentifier;
import gov.nih.nci.cabig.caaers.utils.ConfigProperty;
import gov.nih.nci.cabig.caaers.utils.Lov;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.springframework.validation.ObjectError;

/**
 * @author Ion C. Olaru
 */

public class StudyDetailsTabTestCase extends AbstractStudyWebTestCase {

   

    protected StudyTab createTab() {
        DetailsTab tab = new DetailsTab();
        tab.setConfigurationProperty(new ConfigProperty());

        Map<String, List<Lov>> map = new HashMap<String, List<Lov>>();
        map.put("phaseCodeRefData", new ArrayList<Lov>());
        map.put("statusRefData", new ArrayList<Lov>());

        tab.getConfigurationProperty().setMap(map);
        tab.setCtcDao(ctcDao);
        tab.setMeddraVersionDao(meddraVersionDao);

        return tab;
    }

    public void testReferenceData() {
        List<Ctc> listCtc = new ArrayList<Ctc>();
        List<MeddraVersion> listMeddra = new ArrayList<MeddraVersion>();
        EasyMock.expect(ctcDao.getAll()).andReturn(listCtc).times(2);
        EasyMock.expect(meddraVersionDao.getAll()).andReturn(listMeddra).anyTimes();

        replayMocks();
        Map<String, Object> output = tab.referenceData(request, command);
        verifyMocks();

        assertNotNull(output);
        assertNotNull(output.get("ctcVersion"));
        assertSame(listCtc, output.get("ctcVersion"));
    }

    public void testCreateFieldGroup() {
        List<Ctc> listCtc = new ArrayList<Ctc>();
        List<MeddraVersion> listMeddra = new ArrayList<MeddraVersion>();
        EasyMock.expect(ctcDao.getAll()).andReturn(listCtc).times(1);
        EasyMock.expect(meddraVersionDao.getAll()).andReturn(listMeddra).anyTimes();

        replayMocks();
        Map<String, InputFieldGroup> map = tab.createFieldGroups(command);
        verifyMocks();

        assertNotNull(map);
        InputFieldGroup rfFieldGroup = map.get("rfFieldGroup");
        assertNotNull(rfFieldGroup);
        assertEquals(8, ((InputFieldGroup)map.get("studyDetails")).getFields().size());
        assertEquals(6, rfFieldGroup.getFields().size());
        assertEquals(2, ((InputFieldGroup)map.get("fsFieldGroup")).getFields().size());
        assertEquals(2, ((InputFieldGroup)map.get("ccFieldGroup")).getFields().size());
        assertEquals(4, ((InputFieldGroup)map.get("scFieldGroup")).getFields().size());
        assertEquals(2, ((InputFieldGroup)map.get("sdcFieldGroup")).getFields().size());
        assertEquals(1, ((InputFieldGroup)map.get("dcpFieldGroup")).getFields().size());
        assertEquals("CIOMS Form", rfFieldGroup.getFields().get(2).getDisplayName());
    }


    public void testValidation() {
        List<Ctc> listCtc = new ArrayList<Ctc>();
        List<MeddraVersion> listMeddra = new ArrayList<MeddraVersion>();
        EasyMock.expect(ctcDao.getAll()).andReturn(listCtc).times(1);
        EasyMock.expect(meddraVersionDao.getAll()).andReturn(listMeddra).anyTimes();

        List<Identifier> studyIdentifiers = new ArrayList<Identifier>();
        studyIdentifiers.add(new OrganizationAssignedIdentifier());
        studyIdentifiers.add(new SystemAssignedIdentifier());
        study.setIdentifiers(studyIdentifiers);
        study.setStudyOrganizations(new ArrayList<StudyOrganization>());
        study.getStudyOrganizations().add(new StudyCoordinatingCenter());
        
        replayMocks();
        tab.validate(command, errors);
        verifyMocks();

        assertEquals(12, errors.getErrorCount());
        assertEquals("Missing Short title", ((ObjectError)errors.getAllErrors().get(0)).getDefaultMessage());
        assertEquals("Missing AdEERS  reporting required", ((ObjectError)errors.getAllErrors().get(5)).getDefaultMessage());

    }

    public void testValidationWithStudyShortTitle() {
        List<Ctc> listCtc = new ArrayList<Ctc>();
        List<MeddraVersion> listMeddra = new ArrayList<MeddraVersion>();
        EasyMock.expect(ctcDao.getAll()).andReturn(listCtc).times(1);
        EasyMock.expect(meddraVersionDao.getAll()).andReturn(listMeddra).anyTimes();

        List<Identifier> studyIdentifiers = new ArrayList<Identifier>();
        studyIdentifiers.add(new OrganizationAssignedIdentifier());
        studyIdentifiers.add(new SystemAssignedIdentifier());
        study.setIdentifiers(studyIdentifiers);
        study.setStudyOrganizations(new ArrayList<StudyOrganization>());
        study.getStudyOrganizations().add(new StudyCoordinatingCenter());
        study.setShortTitle("This is supposed to be a Short title.");

        replayMocks();
        tab.validate(command, errors);
        verifyMocks();

        assertEquals(11, errors.getErrorCount());
    }

    public void testPostProcess() {
        List<Identifier> studyIdentifiers = new ArrayList<Identifier>();
        studyIdentifiers.add(new OrganizationAssignedIdentifier());
        studyIdentifiers.add(new OrganizationAssignedIdentifier());
        ((OrganizationAssignedIdentifier)studyIdentifiers.get(0)).setOrganization(new LocalOrganization());
        ((OrganizationAssignedIdentifier)studyIdentifiers.get(1)).setOrganization(new LocalOrganization());
        study.setIdentifiers(studyIdentifiers);

        studyIdentifiers.get(0).setType(OrganizationAssignedIdentifier.SPONSOR_IDENTIFIER_TYPE);
        studyIdentifiers.get(1).setType(OrganizationAssignedIdentifier.COORDINATING_CENTER_IDENTIFIER_TYPE);

        study.setStudyOrganizations(new ArrayList<StudyOrganization>());
        study.getStudyOrganizations().add(new StudyCoordinatingCenter());
        study.getStudyCoordinatingCenter().setOrganization(new LocalOrganization());
        
        replayMocks();
        tab.postProcess(request, command, errors);
        verifyMocks();

        assertEquals(0, errors.getErrorCount());
    }

    
}
