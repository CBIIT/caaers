<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net/el"%>
<%@ taglib prefix="chrome" tagdir="/WEB-INF/tags/chrome"%>
<%@taglib uri="http://www.extremecomponents.org" prefix="ec"%>
<link rel="stylesheet" type="text/css"
	href="<c:url value="/css/extremecomponents.css"/>">
<html>
<head>
<tags:stylesheetLink name="tabbedflow"/>
<tags:stylesheetLink name="participant"/>
<tags:includeScriptaculous />
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<style type="text/css">
        /* Override default lable length */
         div.row div.label { width: 9em; } 
        div.row div.value { margin-left: 10em; }
        div.content {
            padding: 5px 15px;
        }        
</style>

<title>${tab.longTitle}</title>
<script type="text/javascript" src="/caaers/js/extremecomponents.js"></script>
<tags:dwrJavascriptLink objects="search"/>

<script>

function buildTable(form) {
	$('indicator').className=''
	var type = "";
	var text = "";

	for(var x=0; x < 3; x++) {
	
		if ( $('prop'+x).value.length > 0 ){
			text = text +  $('prop'+x).value + ",";
			type = type +  $('prop'+x).name +',';
		}
	}
	
	$('prop').value=type
	$('value').value=text
	
	var parameterMap = getParameterMap(form);		
	search.getResearchStaffTable(parameterMap,type,text,showTable);
}


</script>
</head>
<body>
<div class="tabpane">
 <ul id="workflow-tabs" class="tabs autoclear">
    <li class="tab"><div>
        <a href="createResearchStaff">Create Research Staff</a>
    </div></li>
    <li class="tab selected"><div>
        <a href="searchResearchStaff">Search Research Staff</a>
    </div></li>
 </ul>
 
 <div class="content">
  <form:form name="searchForm" id="searchForm" method="post">
   <p class="instructions">
    <br />
     Search for Research Staffs by choosing any of the listed Criteria. The result will show the details of Research Staff.
   </p> 
   <chrome:box title="Research Staff Criteria" cssClass="mpaired" autopad="false">
		    <div class="row">
		    	<div class="label"> First Name :&nbsp; </div>
		    	<div class="value"><input id="prop0" name="firstName" type="text"/></div>
		    </div>
		    
		    <div class="row">
		    	<div class="label"> Last Name :&nbsp; </div>
		    	<div class="value"><input id="prop1" name="lastName" type="text"/></div>
		    </div>
		    
		    <div class="row">
		    	<div class="label"> Site :&nbsp; </div>
		    	<div class="value"><input id="prop2" name="name" type="text"/></div>
		    </div>
		    
		    
   </chrome:box>

	<div class="endpanes" />
	<div class="row" style="float:right;">
	<input class='ibutton' type='button' onclick="buildTable('assembler');" value='Search'  title='Search'/>
	<tags:indicator id="indicator" />
	</div>
	<div class="endpanes" />


   </form:form>
  <br>
  <form:form id="assembler" >
	<div>			
	<input type="hidden" name="_prop" id="prop" >
	<input type="hidden" name="_value" id="value"  >
	</div>
	<chrome:box title="Search Results">
     <chrome:division id="single-fields">
        <div id="tableDiv">
   			<c:out value="${assembler}" escapeXml="false"/> 
		</div>
	</chrome:division>
	</chrome:box>
   </form:form>
 </div>
 
</div>
</body>
</html>