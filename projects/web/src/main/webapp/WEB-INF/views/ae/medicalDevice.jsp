<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="chrome" tagdir="/WEB-INF/tags/chrome" %>
<%@taglib prefix="ae" tagdir="/WEB-INF/tags/ae" %>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>${tab.longTitle}</title>
    <tags:stylesheetLink name="ae"/>
    <style type="text/css">
        /* This is intended to apply to the grade longselect only */
        .longselect {
            width: 20em;
        }
        .longselect label {
            padding-left: 3.0em;
            text-indent: -2.5em;
        }
        
      	div.row div.label { width: 13em; }
    	div.row div.value { margin-left: 14em;}
    </style>
    <tags:includeScriptaculous/>
     <tags:dwrJavascriptLink objects="createAE"/>
    <script type="text/javascript">
    
    
    	var aeReportId = ${empty command.aeReport.id ? 'null' : command.aeReport.id}

        Element.observe(window, "load", function() {
           

			//var otherTextId= "aeReport.medicalDevice.otherDeviceOperator"
    		//var otherSelectId= "aeReport.medicalDevice.deviceOperator"
    	    //showOther(otherTextId,otherSelectId);
            //Event.observe("aeReport.medicalDevice.deviceOperator", "change", function() { showOther(otherTextId,otherSelectId) })

			if ( $('medicalDevice-0') != null ){
				$('add-medicalDevice-button').type="hidden";
			}
			
            new ListEditor("medicalDevice", createAE, "MedicalDevice", {
                addFirstAfter: "single-fields",
                addParameters: [aeReportId],
                addCallback: function(index) {
                	AE.registerCalendarPopups("medicalDevice-" + index)
                	$('add-medicalDevice-button').type="hidden";
                	
                }
            })
        })
    
    	
    	function showOther(otherTextId,otherSelectId){
    			if ($(otherSelectId).options[2].selected){
    				$(otherTextId).disabled=false
    			}
    			else{
    				$(otherTextId).value=""
    				$(otherTextId).disabled=true
    			}
    		}
    	
    	/*
    	Event.observe(window, "load", function() {
    		var otherTextId= "aeReport.medicalDevice.otherDeviceOperator"
    		var otherSelectId= "aeReport.medicalDevice.deviceOperator"
    	    showOther(otherTextId,otherSelectId);
           Event.observe("aeReport.medicalDevice.deviceOperator", "change", function() { showOther(otherTextId,otherSelectId) })
        })
        */
    
    </script>
    <style type="text/css">
        textarea {
            width: 30em;
            height: 12em;
        }
    </style>
</head>
<body>
<tags:tabForm tab="${tab}" flow="${flow}">
    <jsp:attribute name="instructions">
    <tags:instructions code="instruction_ae_device" />
    </jsp:attribute>
    <jsp:attribute name="repeatingFields">
        <c:forEach items="${command.aeReport.medicalDevices}" varStatus="status">
            <ae:oneMedicalDevice index="${status.index}"/>
        </c:forEach>
    </jsp:attribute>
    <jsp:attribute name="localButtons">
        <tags:listEditorAddButton divisionClass="medicalDevice" label="Add a Medical device"/>
    </jsp:attribute>
</tags:tabForm>
</body>
</html>