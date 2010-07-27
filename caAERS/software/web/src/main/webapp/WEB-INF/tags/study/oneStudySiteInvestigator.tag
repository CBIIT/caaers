<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="chrome" tagdir="/WEB-INF/tags/chrome"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="study" tagdir="/WEB-INF/tags/study"%>
<%@taglib prefix="caaers" uri="http://gforge.nci.nih.gov/projects/caaers/tags" %>

<%@attribute name="index" required="true" type="java.lang.Integer" %>
<%@attribute name="style"%>

<div id="ss-section-0" class="row ss-section" style="${style}">
	<p id="instructions" align="left"><p><tags:instructions code="study.study_investigator.1" /></p>
    <br>

    <table width="100%" class="tablecontent" valign="top" id="ssi-table-row-TABLE" style="display:${fn:length(command.study.activeStudyOrganizations[index].studyInvestigators) lt 1 ? 'none' : 'inline;'};">
        <tr id="ssi-table-head" class="ssi-table-head">
            <th width="55%" class="tableHeader"><tags:requiredIndicator/>Investigator</th>
            <th width="20%" class="tableHeader"><tags:requiredIndicator/>Role</th>
            <th width="20%" class="tableHeader"><tags:requiredIndicator/>Status</th>
            <th width="5%" class="tableHeader">&nbsp;</th>
        </tr>

        <c:forEach var="si" items="${command.study.activeStudyOrganizations[index].studyInvestigators}" varStatus="status">
            <c:if test="${not si.retired}">
        		<study:oneInvestigator cssClass="ssi-table-row" index="${status.index}" readOnly="${not empty si.siteInvestigator}" si="${si}"/>
        		<c:if test="${empty si.siteInvestigator}">
        			<script>new jsInvestigator(${status.index}, "${si.siteInvestigator.investigator.fullName}");</script>
        		</c:if>
            </c:if>
        </c:forEach>

        <c:if test="${fn:length(command.study.activeStudyOrganizations[index].activeStudyInvestigators) lt 1}">
            <tr id="ssi-empty-row" class="ssi-empty-row">
                <td colspan="4"><caaers:message code="study.noInvestigator" /></td>
            </tr>
        </c:if>
    </table>
</div>