<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="chrome" tagdir="/WEB-INF/tags/chrome"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@attribute name="index" required="true" type="java.lang.Integer" %>
<%@attribute name="isSolicitedAE" type="java.lang.Boolean" required="true"  description="Should be set to true, when we are rendering the row for solicited adverse events"%>
<%@attribute name="isAETermOtherSpecify" type="java.lang.Boolean" required="true" description="Should be true, when the CTC term is otherspecify" %>
<%@attribute name="adverseEvent" type="gov.nih.nci.cabig.caaers.domain.AdverseEvent" required="true" description="The adverse event that is being rendered" %>
<%@attribute name="hideDeleteCtrl" type="java.lang.Boolean" description="If true, will not display the column containing delete button" %>
<%@attribute name="aeTermIndex" type="java.lang.Integer" required="true" description="The index of aeTerm, explicitly set this to 0 or 1, this is to support checkbox in confirmation page" %>

<c:set var="mainGroup">main${index}</c:set>
    	<%--
    		Logic : For Observed AE, the other Verbatim and sometimes based on the term, the OtherSpecify needs to be displayed
    			  : For Solicited AE, both Verbatim and Other specify will not be there.
    			  So the Term column will have 3 fields when the term is OtherSpecify (for observed AE)  
    	--%>
<tr class="ae-section ${index % 2 gt 0 ? 'odd' : 'even'}" id="ae-section-${index}" >
<c:if test="${aeTermIndex gt 0}">
	<td><tags:renderInputs field="${fieldGroups[mainGroup].fields[0]}" cssClass="cb${adverseEvent.adverseEventTerm.term.id} aeChk"/></td>
</c:if>	
<c:if test="${isSolicitedAE}">
	<c:forEach items="${fieldGroups[mainGroup].fields}" var="field" varStatus="lpstatus" begin="${aeTermIndex}">
		<td><tags:renderInputs field="${field}" cssClass="${lpstatus.index == aeTermIndex ? 'aeTerm' : lpstatus.index gt 1 ? 'shortselectbox' : 'selectbox' }"/></td>
	</c:forEach>
</c:if>		
		
<c:if test="${not isSolicitedAE}">
	<c:if test="${isAETermOtherSpecify}">
		<td>
			<tags:renderInputs field="${fieldGroups[mainGroup].fields[aeTermIndex]}" cssClass="aeTerm"/>
			<div class="divOtherMeddra">
			<tags:requiredIndicator/>${fieldGroups[mainGroup].fields[aeTermIndex + 1].displayName}
			<tags:renderInputs field="${fieldGroups[mainGroup].fields[aeTermIndex + 1]}" cssClass="aeOtherMeddra om${adverseEvent.adverseEventTerm.term.id}" />
			</div>
			<div class="divNotes">
			${fieldGroups[mainGroup].fields[aeTermIndex + 2].displayName}
			<tags:renderInputs field="${fieldGroups[mainGroup].fields[aeTermIndex + 2]}" cssClass="aeNotes" />
			</div>
		</td>
		<c:forEach items="${fieldGroups[mainGroup].fields}" var="field" begin="${aeTermIndex + 3 }" varStatus="lpIdx">
		<td><tags:renderInputs field="${field}" cssClass="${lpIdx.index gt 3 ? 'shortselectbox' : 'selectbox'}"/></td>
		</c:forEach>
	</c:if>
	<c:if test="${not isAETermOtherSpecify}">
		<td>
			<tags:renderInputs field="${fieldGroups[mainGroup].fields[aeTermIndex]}" cssClass="aeTerm"/>
			<div class="divNotes">
			${fieldGroups[mainGroup].fields[1].displayName}
			<tags:renderInputs field="${fieldGroups[mainGroup].fields[aeTermIndex + 1]}" cssClass="aeNotes" />
			</div>
		</td>
		<c:forEach items="${fieldGroups[mainGroup].fields}" var="field" begin="${aeTermIndex + 2}" varStatus="lpIdx">
		<td><tags:renderInputs field="${field}" cssClass="${lpIdx.index gt 2 ? 'shortselectbox' : 'selectbox'}"/></td>
		</c:forEach>
	</c:if>
	<c:if test="${not hideDeleteCtrl}">
	<td>
		<a href="#" onClick="rpCreator.deleteAdverseEvent(${index})"><img src="<chrome:imageUrl name="../checkno.gif" />"  alt="" style="border:0" /></a>
	</td>
	</c:if>
</c:if>		
</tr>