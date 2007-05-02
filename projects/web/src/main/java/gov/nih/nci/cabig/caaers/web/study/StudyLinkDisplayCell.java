package gov.nih.nci.cabig.caaers.web.study;

import gov.nih.nci.cabig.caaers.domain.Study;

import org.extremecomponents.table.bean.Column;
import org.extremecomponents.table.cell.AbstractCell;
import org.extremecomponents.table.core.TableModel;

public class StudyLinkDisplayCell extends AbstractCell {

    private static final String LINK = "edit?studyId=";

    @Override
    protected String getCellValue(TableModel model, Column column) {
        Study study = (Study) model.getCurrentRowBean();
        String cellValue = column.getValueAsString();

        if (study != null) {
            cellValue = "<a href=\"" + LINK + study.getId().toString() + "\">"
                + cellValue + "</a>";
        }

        return cellValue;
    }
}
