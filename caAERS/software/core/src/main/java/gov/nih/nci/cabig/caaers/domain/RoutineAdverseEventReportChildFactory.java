/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.domain;

import org.apache.commons.collections15.functors.InstantiateFactory;

/**
 * @author Rhett Sutphin
 */
public class RoutineAdverseEventReportChildFactory<T extends RoutineAdverseEventReportChild>
                extends InstantiateFactory<T> {
    private RoutineAdverseEventReport report;

    public RoutineAdverseEventReportChildFactory(Class<T> classToInstantiate,
                    RoutineAdverseEventReport parent) {
        super(classToInstantiate);
        this.report = parent;
    }

    @Override
    public T create() {
        T child = super.create();
        child.setRoutineReport(report);
        return child;
    }
}
