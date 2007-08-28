package gov.nih.nci.cabig.caaers.web.search;

import gov.nih.nci.cabig.caaers.domain.Study;
import gov.nih.nci.cabig.caaers.domain.AdverseEvent;

import org.extremecomponents.table.bean.Column;
import org.extremecomponents.table.cell.AbstractCell;
import org.extremecomponents.table.core.TableModel;

public class SponsorLinkDisplayCell extends AbstractCell {

    @Override
    protected String getCellValue(TableModel model, Column column) {
    	//log.debug("Class Name " + model.getCurrentRowBean().getClass().getName());
        AdverseEvent ae = (AdverseEvent) model.getCurrentRowBean();
        Study study = null;
        if (ae.getReport() != null){
        	study = ae.getReport().getAssignment().getStudySite().getStudy();
        }
        if (ae.getRoutineReport() != null){
        	study = ae.getRoutineReport().getAssignment().getStudySite().getStudy();
        }
        String cellValue = study.getPrimarySponsorCode();

        return cellValue;
    }
}
