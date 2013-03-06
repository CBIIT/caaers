/*******************************************************************************
 * Copyright SemanticBits, Northwestern University and Akaza Research
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caaers/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.cabig.caaers.web.search;

import gov.nih.nci.cabig.caaers.domain.AdverseEvent;
import gov.nih.nci.cabig.caaers.domain.Study;

import org.extremecomponents.table.bean.Column;
import org.extremecomponents.table.cell.AbstractCell;
import org.extremecomponents.table.core.TableModel;

public class SponsorLinkDisplayCell extends AbstractCell {

    @Override
    protected String getCellValue(TableModel model, Column column) {
        // log.debug("Class Name " + model.getCurrentRowBean().getClass().getName());
        AdverseEvent ae = (AdverseEvent) model.getCurrentRowBean();
        Study study = null;
        if (ae.getReport() != null) {
            study = ae.getReport().getAssignment().getStudySite().getStudy();
        }
        String cellValue = study.getPrimarySponsorCode();

        return cellValue;
    }
}
