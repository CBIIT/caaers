/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.service.synchronizer;

import gov.nih.nci.cabig.caaers.domain.ReportFormat;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class StudyReportTypeSynchronizer implements Synchronizer<Study>{

    private List<String> context = new ArrayList<String>();
    @Override
    public List<String> contexts() {
        return context;
    }
	public void migrate(Study dbStudy, Study xmlStudy,DomainObjectImportOutcome<Study> outcome) {
		
		/**
		 *	If none of the below given elements are provided in the XML, then ReportFormats list will be null.
			In this case we do not modify the existing ReportFormats.
			If one or more of the below given elements are provided in the XML, 
			In this case the existing ReportFormats on the study will be replaced with the ones provided.
			
            <xs:element name="reportTypeCaaersXML" type="xs:boolean"  minOccurs="0" maxOccurs="1"/>
            <xs:element name="reportTypeAdeersPDF" type="xs:boolean"  minOccurs="0" maxOccurs="1"/>
            <xs:element name="reportTypeMedwatchPDF" type="xs:boolean"  minOccurs="0" maxOccurs="1"/>
            <xs:element name="reportTypeDCPSAEForm" type="xs:boolean"  minOccurs="0" maxOccurs="1"/>
            <xs:element name="reportTypeCIOMSForm" type="xs:boolean"  minOccurs="0" maxOccurs="1"/>
            <xs:element name="reportTypeCIOMSAEForm" type="xs:boolean"  minOccurs="0" maxOccurs="1"/>
		 */
		
		if(CollectionUtils.isEmpty(xmlStudy.getReportFormats())){
			return;
		}
		
		dbStudy.getReportFormats().clear();
		for(ReportFormat rf : xmlStudy.getReportFormats()){
			dbStudy.addReportFormat(rf);
		}
	}
}
