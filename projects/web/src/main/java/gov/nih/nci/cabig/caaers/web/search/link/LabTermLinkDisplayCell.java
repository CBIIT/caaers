package gov.nih.nci.cabig.caaers.web.search.link;

import org.extremecomponents.table.cell.AbstractCell;
import org.extremecomponents.table.core.TableModel;
import org.extremecomponents.table.bean.Column;
import gov.nih.nci.cabig.caaers.domain.AnatomicSite;
import gov.nih.nci.cabig.caaers.domain.LabTerm;

/**
 * @author Biju Joseph
 */
public class LabTermLinkDisplayCell extends AbstractCell {


    @Override
    protected String getCellValue(final TableModel model, final Column column) {

        LabTerm labTerm = (LabTerm) model.getCurrentRowBean();
        String cellValue = column.getValueAsString();
        String tableId = model.getTableHandler().getTable().getTableId();


        if (labTerm != null) {
            cellValue="<a  href=\"javascript:fillLabsAutoCompletor('"+labTerm.getId()+"','"+tableId+"')\">"+labTerm.getTerm()+"</a>";
                     }
        return cellValue;
    }


}
