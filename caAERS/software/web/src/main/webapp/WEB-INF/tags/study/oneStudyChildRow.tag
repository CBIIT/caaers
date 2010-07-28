<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="chrome" tagdir="/WEB-INF/tags/chrome"%>
<%@taglib prefix="caaers" uri="http://gforge.nci.nih.gov/projects/caaers/tags" %>

<%@attribute name="index" required="true" type="java.lang.Integer" %>
<%@attribute name="idSuffix" %>
<%@attribute name="style"%>
<%@attribute name="cssClass" required="true" %>
<%@attribute name="disableDelete" type="java.lang.Boolean"  %>
<%@attribute name="identifiers" type="java.lang.Boolean"  %>
<%@attribute name="exclusions" %>
<%@attribute name="readOnly" type="java.lang.Boolean" %>
<%@attribute name="isNew" type="java.lang.Boolean" %>

<c:set var="mainGroup">main${index}</c:set>
<c:set var="css">${cssClass} ${index % 2 ne 0 ? 'even' : 'odd'} ${sectionClass}</c:set>
<tr id="${cssClass}-${empty idSuffix ? index : idSuffix}" class="${css}" onmouseout="this.className='${css}'" onmouseover="this.className='highlight'" style="${style}" valign="top">
    
    <c:forEach items="${fieldGroups[mainGroup].fields}" var="field" varStatus="fstatus">
        <c:if test="${not fn:contains(exclusions, field.displayName)}">
		    <td style="border-right:none;">
                <c:if test="${fstatus.index != 2 || identifiers}">
                    <c:set var="fValue"><jsp:attribute name="value"><caaers:value path="${field.propertyName}" /></jsp:attribute></c:set>
                    <tags:renderInputs field="${field}" disabled="${identifiers and (index lt 2) and (fstatus.index ne 4)}"/>
                </c:if>
                <c:if test="${fstatus.index == 2 && !identifiers}">
                    <c:if test="${isNew}">Pending</c:if>
                    <c:if test="${!isNew}">Inactive</c:if>
                </c:if>
            </td>
		</c:if>
	</c:forEach>

    <c:if test="${not disableDelete}">
        <td style="border-left:none;">
            <tags:button id="${status.index}" color="red" type="button" value="" size="small" icon="x" onclick="fireDelete(${index},'${cssClass}-${index}')"/>
        </td>
	</c:if>
	<c:if test="${disableDelete}">
	    <td>&nbsp;</td>
	</c:if>
</tr>
