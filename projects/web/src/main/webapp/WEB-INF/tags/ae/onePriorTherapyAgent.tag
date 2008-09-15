<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="ae" tagdir="/WEB-INF/tags/ae" %>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="chrome" tagdir="/WEB-INF/tags/chrome" %>
<%@taglib prefix="ui" tagdir="/WEB-INF/tags/ui" %>

<%@attribute name="index" required="true" %>
<%@attribute name="parentIndex" required="true" %>
<%@attribute name="style"%>
<div class="${(index % 2 ) gt 0 ? 'odd' : 'even' }">
		<table width="100%">
 			<tr>
  				<td width="99%">
					<ui:text path="aeReport.saeReportPriorTherapies[${parentIndex}].priorTherapyAgents[${index}].name" readonly="true"/>
  				</td>
  				<td>
					<a href="#anchorPriorTherapies[${parentIndex}].priorTherapyAgents" onClick="mHistory.removeDetails('priorTherapyAgent', ${index}, 'anchorPriorTherapies[${parentIndex}].priorTherapyAgents', {parentIndex : ${parentIndex} })">
  					<img src="<chrome:imageUrl name="../checkno.gif" />" />
					</a>
				</td>
 			</tr>
		</table> 
</div>

<%--
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="ae" tagdir="/WEB-INF/tags/ae" %>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="chrome" tagdir="/WEB-INF/tags/chrome" %>

<%@attribute name="index" required="true" type="java.lang.Integer" %>
<%@attribute name="parentIndex" type="java.lang.Integer" %>
<%@attribute name="style"%>


<chrome:division title="Agent ${index +1}" cssClass="ptAgent${parentIndex}" id="ptAgent${parentIndex}-${index}" style="${style}">

    <div class="row" id="aeReport.saeReportPriorTherapies[${parentIndex}].priorTherapyAgents[${index}]-row" >
        <div class="label">
            Agent
        </div>

        <div class="value">

            <input size="50" type="text" id="aeReport.saeReportPriorTherapies[${parentIndex}].priorTherapyAgents[${index}].chemoAgent-input"  class="autocomplete"/>
            <tags:indicator id="aeReport.saeReportPriorTherapies[${parentIndex}].priorTherapyAgents[${index}].chemoAgent-indicator"/>

            <div id="aeReport.saeReportPriorTherapies[${parentIndex}].priorTherapyAgents[${index}].chemoAgent-choices" class="autocomplete" style="display: none"></div>

            <a id="showAllChemoAgentparent${parentIndex}index${index}"
               href="javascript:showChemoAgentsTable('chemoAgentTableparent${parentIndex}index${index}','chemoAgentTableparent${parentIndex}index${index}-outer')">Show
                All</a>

			<div id="chemoAgentTableparent${parentIndex}index${index}-outer"
			                 style="position: absolute; display: none; left: 640px; width:400px; z-index:99;">
			<table width="100%" class="eXtremeTable" frame="border" border-color="blue" bgcolor="white">
			<tbody>
			<tr class="titleRow">
			  <td align="left" class="title">Select Agent:</td><td width="20px"><a href="javascript:hideShowAllTable('chemoAgentTableparent${parentIndex}index${index}-outer')">
			       <img src="/caaers/images/rule/window-close.gif" id="close-image"/>
			      </a></td>
			</tr>
			<tr>
			<td colspan="2">
			        <div id="chemoAgentTableparent${parentIndex}index${index}"  />
			        
			</td>
			</tr>
			</tbody>
			</table>
			     
			</div>

            <form:hidden path="aeReport.saeReportPriorTherapies[${parentIndex}].priorTherapyAgents[${index}].chemoAgent"/>

        </div>
    </div>
</chrome:division>
--%>	