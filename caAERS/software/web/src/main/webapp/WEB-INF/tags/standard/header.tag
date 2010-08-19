<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="csmauthz" uri="http://csm.ncicb.nci.nih.gov/authz" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="standard" tagdir="/WEB-INF/tags/standard"%>
<csmauthz:swapAuth useOriginal="true">
<div id="header">
  <div id="skipnav">
  <a href="#skipnav">Skip Navigation</a>
  </div>
    <div class="background-R">

        <a href="/caaers/pages/task" id="logo">caAERS</a>
		  <c:url value="${empty configuration.map.caaersBaseHelpUrl ? '/help/caAERS_Help.htm' : configuration.map.caaersBaseHelpUrl}" scope="request" var="_caaersHelpURL" />
		  <c:set var="_tabNum" value="${(not empty tab and tab.number gt 0) ? tab.number : ''}" />
		  <c:set var="roboHelpKey">ROBOHELP_${currentTask.linkName}${_tabNum gt 0 ? '_' : ''}${_tabNum gt 0 ? _tabNum : ''}</c:set>
		  <spring:message var="roboHelpLink" code="${roboHelpKey}" text="NO_${roboHelpKey}"/>
          <standard:welcome/>
		  <a href="${_caaersHelpURL}${roboHelpLink}" target="_blank" id="help">Help</a>
          <a href="<c:url value="/j_acegi_logout"/>" id="logout">Log out</a>
        
        <ul id="sections" class="tabs">
        <c:set var="_visibleTabIndex" value="-1" />
        <c:forEach items="${sections}" var="section" varStatus="index">
            <c:set var="_showSection" value="false" />
            <c:set var="_useSectionLink" value="false" />
            <c:set var="_sectionLink" value="${section.mainUrl}" />
            <c:forEach var="task" items="${section.tasks}">
                <csmauthz:accesscontrol domainObject="${task}" authorizationCheckName="taskAuthorizationCheck">
                    <c:set var="_showSection" value="true"/>
                </csmauthz:accesscontrol>
            </c:forEach>
            <c:if test="${_showSection}">
                <c:set var="_visibleTabIndex" value="${_visibleTabIndex + 1}" />
                <li class="${section == currentSection ? 'selected' : ''}">
                    <a id="firstlevelnav_${section.mainController}" href="<c:url value="${section.mainUrl}"/>" index="${_visibleTabIndex}">${section.displayName}</a>
                </li>
            </c:if>
        </c:forEach>
        </ul>

        <div id="taskbar">
            <c:if test="${not empty currentSection.tasks}">
				<c:set var="noOfTasks" value="${fn:length(currentSection.tasks)}" />
				 <!-- test : ${noOfTasks} , ${fn:length(currentSection.tasks)}-->
				 <ul>
                <c:forEach items="${currentSection.tasks}" var="task">
				<c:set var="lengthOfTask" value="${fn:length(task.displayName)}" />
                    <csmauthz:accesscontrol domainObject="${task}" authorizationCheckName="taskAuthorizationCheck">
                    	 <li class="${noOfTasks gt 4 ? 'gt4' : 'lte4'}"><a class="${(task == currentTask) || (task.displayName == currentTask.displayName) ?  'selected' : '' } ${(lengthOfTask gt 21 ? 'gt18' : '')}" id="secondlevelnav_${task.linkName}"  href="<c:url value="${task.url}"/>"><img class="${(lengthOfTask gt 21 ? 'imagegt18' : '')}" src="/caaers/images/blue/icons/${task.linkName}_icon.png" alt=""/><span class="spangt18">${task.displayName}</span></a></li>
                    </csmauthz:accesscontrol>
                </c:forEach>
				</ul>
            </c:if>
        </div>
        <div id="floatingTaskbar" style="display:none;">
            <tags:floatingTaskbar />
        </div>
    </div>
</div>
</csmauthz:swapAuth>
<!-- end header -->
