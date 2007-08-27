<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="chrome" tagdir="/WEB-INF/tags/chrome"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@attribute name="key" required="true" %>
<%@attribute name="tabular" type="java.lang.Boolean" %>
<%@attribute name="singleRow" type="java.lang.Boolean" %>
<%@attribute name="heading" %>
<c:if test="${not tabular}">
	<c:forEach var="field" items="${fieldGroups[key].fields}">
	<tags:renderRow field="${field}" />
	</c:forEach>
</c:if>
<c:if test="${tabular}">
<c:if test="${singleRow}">
 <table>
	<tr>
	<td><div class="row"><div class="label">${heading}</div></div></td>
	<c:forEach var="field" items="${fieldGroups[key].fields}">
	<td><tags:renderLabel field="${field}" /></td>
	<td><tags:renderInputs field="${field}" /></td>
	</c:forEach>
	</tr>	
 </table>
</c:if>
<c:if test="${not singleRow}">

 <table>
	<tr><td colspan="${fn:length(fieldGroups[key].fields) * 2}"><b>${heading}</b></td></tr> 
	<tr>
	<c:forEach var="field" items="${fieldGroups[key].fields}">
		<td><tags:renderLabel field="${field}" /></td>
		<td><tags:renderInputs field="${field}" /></td>
	</c:forEach>
	</tr>	
 </table>

</c:if>
</c:if>
