<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.extremecomponents.org" prefix="ec"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/css/extremecomponents.css"/>">
<%@taglib prefix="chrome" tagdir="/WEB-INF/tags/chrome" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<title>Participant Search</title>
<link href="resources/styles.css" rel="stylesheet" type="text/css">
<link href="resources/search.css" rel="stylesheet" type="text/css">
<script>
function submitPage(s){
	document.getElementById("searchCategory").value=s;
	document.getElementById("searchForm").submit();
}
function navRollOver(obj, state) {
  document.getElementById(obj).className = (state == 'on') ? 'resultsOver' : 'results';
}
function doNothing(){
}
</script>
</head>
<body>

<chrome:search title="">
<form:form id="searchForm" name="searchForm" method="post">
<form:hidden path="searchCategory" />
<table width="100%" border="0" cellspacing="0" cellpadding="0"
	class="titleArea">
	<tr>
		<!-- TITLE STARTS HERE -->
		<td width="99%" height="43" valign="middle" id="title"></td>

		<td valign="top">
		<table width="100%" border="0" cellspacing="0" cellpadding="0"
			id="search">
			<tr>
				<td class="labels">&nbsp;</td>
			</tr>
			<!--
			<tr>
				<td class="searchType">Search <select name="select"
					class="field1">

					<option selected>Participant</option>
					<option>Protocol</option>
				</select> by <select name="select" class="field1">
					<option selected>Participant Name</option>
					<option>Participant Registration#</option>
				</select></td>

			</tr>
			-->
		</table>
		<span class="notation">&nbsp;</span></td>
		<td valign="top">
		<table width="100%" border="0" cellspacing="0" cellpadding="0"
			id="search">
			<tr>
				<td align="left" class="labels"><span class="notation"><span
					class="red">*</span><em></em></span>Search by:</td>

				<td align="left" class="labels">Search String:</td>
				<td class="labels">&nbsp;</td>
			</tr>
			<tr>
				<td>
				    <form:select path="searchTypePart">
					  <form:options items="${searchTypeParticipant}" itemLabel="desc" itemValue="code" />
				    </form:select>
				</td>
				<td><form:input path="searchTypeTextPart" size="20" /></td>
				<td><input name="imageField" type="image" class="button"
					onClick="submitPage('participant');return false;" src="/images/chrome/b-go.gif" alt="GO"
					align="middle" width="22" height="10" border="0"></td>
			</tr>

		</table>
		<span class="notation"><span class="labels">(<span
			class="red">*</span><em>Required Information </em>)</span>^
		Minimum two characters for search.</span></td>
	</tr>
</table>
</form:form>
</chrome:search>


<chrome:body title="Study Search results">

		<span class="notation"><span class="labels">Please Select one of the Participants to proceed</span></span>						
		<ec:table 
    	items="participants"
    	var="participant" 
    	action="${pageContext.request.contextPath}/pages/home" 
    	imagePath="${pageContext.request.contextPath}/images/table/*.gif"
    	showPagination="false"
    	cellspacing="0" cellpadding="0" border="0" width="80%" style="" styleClass="">
    	<ec:row highlightRow="true">
        <ec:column property="firstName" title="First Name">
           <a href="searchRegisterStudy?participantId=${participant.id}">${participant.firstName}</a>
        </ec:column>
        <ec:column property="lastName" title="Last Name" />
        <ec:column property="dateOfBirth" title="Date of Birth" cell="date" parse="yyyy-MM-dd" format="MM/dd/yyyy" />
        <ec:column property="gender" title="Gender" />
        <ec:column property="race" title="Race" />
        <ec:column property="ethnicity" title="Ethnicity" />
        <%--
        <ec:column property="shortTitle" width="2" sortable="false" filterable="false" title="cpodfgdf">
        	<a href="newParticipant?studySiteId=${study.studySites[0].id}">cp</a>
        </ec:column>--%>
    </ec:row>
</ec:table>
</chrome:body>
<!-- MAIN BODY ENDS HERE -->
</body>
</html>
