<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="chrome" tagdir="/WEB-INF/tags/chrome"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@attribute name="rdd" required="true" type="gov.nih.nci.cabig.caaers.domain.report.ReportDeliveryDefinition" %>
<%@attribute name="index" required="true" type="java.lang.Integer" %>
<%@attribute name="originalIndex" required="true" type="java.lang.Integer" %>
<%@attribute name="style"%>

<c:set var="mainGroup">main${originalIndex}</c:set>
<c:set var="divisionClass">${(rdd.entityType == 1)? 'rdd-sys-row':'rdd-email-row'}</c:set>
<c:set var="cssClass">${divisionClass} ${index % 2 ne 0 ? 'even' : 'odd'} ${sectionClass}</c:set>

<tr id="${divisionClass}-${index}" class="${cssClass}" onmouseout="this.className='${cssClass}'" onmouseover="this.className='highlight'">
	<c:forEach items="${fieldGroups[mainGroup].fields}" var="field" varStatus="status">
	 
	 <%-- System recipient --%>
	 <c:if test="${rdd.entityType eq 1}">
	 	<c:if test="${status.index ne 4}">
            <c:set var="_width">
                <c:if test="${status.index == 3}">25</c:if>
                <c:if test="${status.index < 3}">15</c:if>
            </c:set>
	 	    <td><tags:renderInputs field="${field}" size="${_width}"/></td>
	 	</c:if>
	 </c:if>
	 
	 <%-- Email recipient --%>
        <c:if test="${rdd.entityType eq 2}">
         <c:if test="${status.index eq 3}">
                  <td><tags:renderInputs field="${field}" size="20"/></td>
                </c:if>
        </c:if>

        <%-- Role recipient --%>
        <c:if test="${rdd.entityType eq 3}">
          <c:if test="${status.index eq 4}">
                  <td><tags:renderInputs field="${field}" size="20"/></td>
                </c:if>
        </c:if>




	</c:forEach>
	<td width="100px;" nowrap="true">
        <tags:button color="red" type="button" value="Delete" size="small" icon="x" onclick="javascript:fireDelete(${originalIndex},'${divisionClass}-${index}');"/>
    </td>
</tr>
