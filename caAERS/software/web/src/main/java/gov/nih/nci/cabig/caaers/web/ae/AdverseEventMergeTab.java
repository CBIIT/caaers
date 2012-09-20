package gov.nih.nci.cabig.caaers.web.ae;

import gov.nih.nci.cabig.caaers.domain.dto.AdverseEventDTO;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroup;
import gov.nih.nci.cabig.caaers.web.fields.InputFieldGroupMap;
import gov.nih.nci.cabig.caaers.web.fields.TabWithFields;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author: Biju Joseph
 */
public class AdverseEventMergeTab extends TabWithFields<AdverseEventReconciliationCommand>{

    public AdverseEventMergeTab(String longTitle, String shortTitle, String viewName) {
        super(longTitle, shortTitle, viewName);
    }

    @Override
    public Map<String, InputFieldGroup> createFieldGroups(AdverseEventReconciliationCommand command) {
        InputFieldGroupMap map = new InputFieldGroupMap();
        return map;
    }

    @Override
    public Map<String, Object> referenceData(HttpServletRequest request, AdverseEventReconciliationCommand command) {
        Map<String, Object> refData = super.referenceData(request, command);
        Map<Integer,Boolean> differingAeIdMap  = new LinkedHashMap<Integer, Boolean> ();
        for(Map.Entry<AdverseEventDTO, AdverseEventDTO> e : command.getMatchedAeMapping().entrySet()) {
            if(!(e.getKey().diff(e.getValue()).isEmpty()) ){
                differingAeIdMap.put(e.getKey().getId(), true);
            }
        }
        refData.put("differingAeIdMap", differingAeIdMap);
        return refData;
    }
}
