<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="ui" tagdir="/WEB-INF/tags/ui" %>
<%@taglib prefix="chrome" tagdir="/WEB-INF/tags/chrome" %>
<%@taglib prefix="par" tagdir="/WEB-INF/tags/par" %>
<%@attribute name="index" required="true" %>
<%@attribute name="priorTherapy" required="true" type="gov.nih.nci.cabig.caaers.domain.StudyParticipantPriorTherapy" %>
<div>
 <chrome:division title="${priorTherapy.name}" id="assignment.priorTherapies[${index}]">

	<ui:row path="assignment.priorTherapies[${index}].other">
	 <jsp:attribute name="label">
		<ui:label path="assignment.priorTherapies[${index}].other" text="Comments" />
	 </jsp:attribute>
	 <jsp:attribute name="value">
		<ui:textarea path="assignment.priorTherapies[${index}].other" cols="65" />
	 </jsp:attribute>
	</ui:row>

	<ui:row path="assignment.priorTherapies[${index}].startDate">
	 <jsp:attribute name="label">
		<ui:label path="assignment.priorTherapies[${index}].startDate" text="Therapy start Date" />
	 </jsp:attribute>
	 <jsp:attribute name="value">
		<ui:splitDate path="assignment.priorTherapies[${index}].startDate" />
	 </jsp:attribute>
	 
	</ui:row>

	<ui:row path="assignment.priorTherapies[${index}].endDate">
	 <jsp:attribute name="label">
		<ui:label path="assignment.priorTherapies[${index}].endDate" text="Therapy end date" />
	 </jsp:attribute>
	 <jsp:attribute name="value">
		<ui:splitDate path="assignment.priorTherapies[${index}].endDate" />
	 </jsp:attribute>
	 
	</ui:row>
    
	<c:if test="${priorTherapy.priorTherapy.agentsPossible}">
	   <table class="tablecontent" width="95%">
			<tr>
				<td width="90%">
					<ui:autocompleter path="chemoAgent" >
						<jsp:attribute name="populatorJS">
							function(autocompleter, text){
								createAE.matchChemoAgents(text, function(values) {
        							autocompleter.setChoices(values)
    							})
							}
						</jsp:attribute>
						<jsp:attribute name="selectorJS">
							function(agent) {
            					return agent.name
        					}
						</jsp:attribute>
					</ui:autocompleter>
				</td>
				<td width="10%">
					<input id="priortherapy[${index}].agent-btn" type="button" value="Add"/>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<a name="anchorPriorTherapies[${index}].priorTherapyAgents" />
					<div id="anchorPriorTherapies[${index}].priorTherapyAgents">
						<c:set var="size" value="${fn:length(priorTherapy.priorTherapyAgents)}" />
						<c:forEach items="${priorTherapy.priorTherapyAgents}" varStatus="status">
							<c:set var="newIndex" value="${size - (status.index + 1)}" />
							<par:onePriorTherapyAgent index="{newIndex}" parentIndex="${index}" />
						</c:forEach>
					</div>
				</td>
			</tr>
	   </table>
	</c:if>
 </chrome:division>
</div>
<script type="text/javascript">
function initializePriorTherapy(){
	
	if($('priortherapy[${index}].agent-btn')){
		
		Element.observe('priortherapy[${index}].agent-btn', 'click', function(evt){
			var inField = $('chemoAgent');
			if(inField.value == '') return;
		 	mHistory.addDetails('priorTherapyAgent', evt.element(), inField.value, 'anchorPriorTherapies[${index}].priorTherapyAgents', {parentIndex : ${index} });
		 	
		 	//clear the fields
		 	AE.resetAutocompleter('chemoAgent');
	 	});	
	 	
	}
	
	//AE.registerCalendarPopups("contentOf-assignment.priorTherapies[${index}]");
	AE.registerCalendarPopups();
}

initializePriorTherapy.defer();
</script>