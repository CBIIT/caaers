<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="chrome" tagdir="/WEB-INF/tags/chrome"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@attribute name="index" required="true" type="java.lang.Integer" %>
<%@attribute name="ae" type="gov.nih.nci.cabig.caaers.domain.AdverseEvent" required="true" description="The adverse event that is being rendered" %>
<%@attribute name="width" required="true" type="java.lang.String" %>

<% String currClass= ( ( (index.intValue() % 2) ==0) ? "odd":"even"); %>
<tr align="center"  class="<%= currClass %>">
	<td width="${width}" align="left">${ae.adverseEventTerm.universalTerm}
	<c:if test="${ae.retired}"><img src="<chrome:imageUrl name="../deleted_icon.png" />" /></c:if>
	</td>
	<td width="${width}">${ae.grade.code}</td>
	<td width="${width}" align="left"><tags:formatDate value="${ae.startDate}" /></td>
	<td width="${width}" align="left">${ae.requiresReporting ? 'Yes' : 'No'}</td>
		
</tr>
