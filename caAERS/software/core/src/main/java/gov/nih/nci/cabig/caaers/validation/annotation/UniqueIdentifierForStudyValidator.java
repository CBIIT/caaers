/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.validation.annotation;

import gov.nih.nci.cabig.caaers.dao.StudyDao;
import gov.nih.nci.cabig.caaers.dao.query.StudyQuery;
import gov.nih.nci.cabig.caaers.domain.Identifier;
import gov.nih.nci.cabig.caaers.domain.OrganizationAssignedIdentifier;
import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.SystemAssignedIdentifier;

import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.factory.annotation.Required;

public class UniqueIdentifierForStudyValidator implements Validator<UniqueIdentifierForStudy> {
    private String message;

    private StudyDao studyDao;

    public boolean validate(Object value) {
        if (!(value instanceof Identifier)) return true;
        Identifier id = (Identifier) value;
        StudyQuery query = new StudyQuery();
        query.filterByIdentifierValueExactMatch(id.getValue());
        query.filterByIdentifierType(id.getType());

        List<Study> studies = studyDao.find(query);
        for (Study study : studies) {
            for (Identifier otherId : study.getIdentifiersLazy()) {
                if (otherId.getValue().equals(id.getValue())
                                && otherId.getType().equals(id.getType())
                                && !ObjectUtils.equals(id.getId(), otherId.getId())) {

                    if (id instanceof OrganizationAssignedIdentifier && otherId instanceof OrganizationAssignedIdentifier) {
                        OrganizationAssignedIdentifier orgId = (OrganizationAssignedIdentifier) id;
                        OrganizationAssignedIdentifier orgOtherId = (OrganizationAssignedIdentifier) otherId;

                        boolean a1 = orgId.getId() == null;
                        boolean a2 = otherId.getId() == null;
                        if (!a1 && !a2 && orgId.getId().equals(orgOtherId.getId())) return false;
                    } else if (id instanceof SystemAssignedIdentifier && otherId instanceof SystemAssignedIdentifier) {
                        SystemAssignedIdentifier sId = (SystemAssignedIdentifier) id;
                        SystemAssignedIdentifier sOtherId = (SystemAssignedIdentifier) otherId;
                        if (sId.getSystemName().equals(sOtherId.getSystemName())) return false;
                    }
                }
            }
        }
        return true;
    }

    public void initialize(UniqueIdentifierForStudy parameters) {
        message = parameters.message();
    }

    public String message() {
        return message;
    }

    @Required
    public void setStudyDao(StudyDao studyDao) {
        this.studyDao = studyDao;
    }
}
