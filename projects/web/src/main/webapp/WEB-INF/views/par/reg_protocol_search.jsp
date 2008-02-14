<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.extremecomponents.org" prefix="ec"%>
<%@taglib prefix="chrome" tagdir="/WEB-INF/tags/chrome" %>
<link rel="stylesheet" type="text/css"
	href="<c:url value="/css/extremecomponents.css"/>">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<title>Search for a Study</title>
<script>
	function submitPage(s){
		document.getElementById("command").submit();
	}
	function navRollOver(obj, state) {
  		document.getElementById(obj).className = (state == 'on') ? 'resultsOver' : 'results';
	}
	function doNothing(){
	}

	function updateTargetPage(s){
		document.checkEligibility.nextView.value=s;
		document.checkEligibility.submit();
	}

	function resetSites(btn){
		var classValue= 'siteStudy_' + btn.value;
		$$('.sitesRadioBtn').each(function(input){
		   if(input.classNames().toArray().indexOf(classValue) < 0){
		   	  input.checked=false;
		   }
		});
	}
	
	function resetStudy(study_id){
		$$('.studyRadioBtn').each(function(input){
			if(input.classNames().toArray().indexOf(study_id) < 0){
				input.checked=false;
			}else{
				input.checked=true;
			}
		});
	}
</script>
</head>
<body>
<!-- TOP LOGOS END HERE -->
<!-- TOP NAVIGATION STARTS HERE -->

<chrome:box autopad="true">
    <form:form id="searchForm" method="post" cssClass="standard">
        <table border="0" cellspacing="0" cellpadding="0" class="search">
            <tr>
            </tr>
            <tr>
                <td class="searchType">
                    Search for a Study&nbsp;&nbsp;
                </td>
                <td><form:select path="studyType">
						<form:options items="${studySearchType}" itemLabel="desc"itemValue="code" />
					</form:select></td>
                <td><form:input path="studyText" size="25" /></td>
                <c:set var="targetPage" value="${assignType == 'study' ? '_target0' : '_target1'}"/>
                <td><input type="submit" value="Search" name="${targetPage}" alt="SEARCH" align="middle" width="22"
						height="10" border="0"></td>
            </tr>
            <tr>
                <td></td>
                <td colspan="4" class="notation">
                    <span class="labels">(<span class="red">*</span><em>Required Information</em>)</span>
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    ^ Minimum two characters for search.
                </td>
            </tr>
        </table>
    </form:form>
</chrome:box>

<p id="instructions">
Please choose a Study and then press Save & Continue to proceed 
</p>
<tags:tabForm tab="${tab}" flow="${flow}" title="Study search results">
    <jsp:attribute name="singleFields">
        <tags:tabFields tab="${tab}" />
                    <ec:table autoIncludeParameters="false" items="command.studies" var="study" 
                        action="${pageContext.request.contextPath}/pages/newParticipant"
                        imagePath="${pageContext.request.contextPath}/images/table/*.gif"
                        filterable="false"
                        showPagination="false" form="command"
                        cellspacing="0" cellpadding="0" border="0" width="80%" style=""
                        styleClass="">
                        <ec:row highlightRow="true">
                            <ec:column property="kk" style="width:10px" filterable="false"
                                sortable="false" title=" ">
                                <form:radiobutton path="studyId" cssClass="studyRadioBtn studyId_${study.id}" value="${study.id}" onclick="javascript:resetSites(this);"/>
                            </ec:column>
                            <ec:column property="primaryIdentifier" title="Primary ID" />
                            <ec:column property="shortTitle" title="Short Title" />
                            <ec:column property="primarySponsorCode" title="Funding Sponsor" />
                            <ec:column property="phaseCode" title="Phase" />
                            <ec:column property="status" title="Status" />
                            <ec:column title="Sites" property="status">
                               <table>
                               <c:forEach items="${study.studySites}" var="site">
                               		<tr><td><form:radiobutton  cssClass="sitesRadioBtn siteStudy_${study.id}" 
                               						path="studySiteId" value="${site.id}" onclick="javascript:resetStudy('studyId_${study.id}');"  />
                               		${site.organization.name }</td></tr>
                               </c:forEach>
                               </table>
                            </ec:column>
                        </ec:row>
                    </ec:table>
    </jsp:attribute>
</tags:tabForm>
</body>
</html>