package gov.nih.nci.cabig.caaers.service.synchronizer;


import gov.nih.nci.cabig.caaers.service.DomainObjectImportOutcome;
import gov.nih.nci.cabig.ctms.domain.AbstractMutableDomainObject;

import java.util.List;

public interface Synchronizer <C extends AbstractMutableDomainObject> {
    void migrate(C src, C dest, DomainObjectImportOutcome<C> outcome);
    List<String> contexts();
}


