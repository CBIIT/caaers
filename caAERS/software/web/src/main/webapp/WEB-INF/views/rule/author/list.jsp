<%@ include file="/WEB-INF/views/taglibs.jsp" %>

<html>
<head>
    <title>List Rules</title>
    <tags:dwrJavascriptLink objects="authorRule"/>
    <script type="text/javascript">
    
    Event.observe(window, "load", function() {});
		
		function deployRule(name , divId) {
			try {
				authorRule.deployRuleSet(name, function(values) {
							alert("Successfully Enabled");
							document.getElementById(divId).innerHTML = "<font color='green'>Enabled</font>";
					});
			} catch(e) {alert(e)}
			
		}
		function deleteRule(name , divId) {
			try {
				authorRule.deleteRuleSet(name, function(values) {
							alert("Successfully Deleted");
							document.getElementById(divId).innerHTML = "<font color='green'>Enabled</font>";
					});
			} catch(e) {alert(e)}
			
		}
		
		function unDeployRule(name , divId) {
			try {
				authorRule.unDeployRuleSet(name, function(values) {
							alert("Successfully Disabled");
							document.getElementById(divId).innerHTML = "<font color='red'>Not Enabled</font>";
					});
			} catch(e) {alert(e)}
		}
		
		function exportRule(name) {
			try {
				authorRule.exportRuleSet(name, function(values) {
							alert("Successfully Exported");
					});
			} catch(e) {alert(e)}
		}
				
		function fireRulesNow(mode) {
			try {
				authorRule.fireRules("gov.nih.nci.cabig.caaers.rule.study", mode, function(values) {
							alert("rule fired ");
					});
			} catch(e) {alert(e)}
		}

YAHOO.example.Data = {

    rsList: [
<c:forEach items="${command.ruleSets}" var="rs" varStatus="status">
        {
            rsLevel: "${rs.level}",
            rsDescription: "${rs.description}",
            rsOrganization: "${rs.organization}",
            rsStudyID: "${rs.study}",
            rsStatus: "<div id='status-${rs.id}'>${rs.coverage}</div>",
            rsAction: "<select id='action-id' onChange=\"javascript:handleAction(this, '${rs.id}', '${rs.name}', 'status-${rs.id}')\">" +
            			"<option value=\"\">Please select</option>" +
            			"<option value=\"\">Edit</option>" +
            			"<option value=\"\">Enable</option>" +
            			"<option value=\"\">Disable</option>" +
            			"<option value=\"\">Export</option>" +
            			"<option value=\"\">Delete</option>" +
            			"</select>"
            			

        }
        <c:if test="${!status.last}">,</c:if>
</c:forEach>

    ]
};

    /////////////////////////////////

YAHOO.util.Event.addListener(window, "load", function() {
    YAHOO.example.CustomSort = function() {

        var myColumnDefs = [
            {key:"rsLevel",             label:"Rule Level",         sortable:true,      resizeable:true},
            {key:"rsDescription",       label:"Rule Set",           sortable:true,      resizeable:true},
            {key:"rsOrganization",      label:"Organization",       sortable:true,      resizeable:true},
            {key:"rsStudyID",           label:"Study",              sortable:true,      resizeable:true},
            {key:"rsStatus",            label:"Status",             sortable:true,      resizeable:true, formatter:"myCustom"},
            {key:"rsAction",            label:"Action",             sortable:false,     resizeable:true}
        ];

        var myCustomFormatter = function(elCell, oRecord, oColumn, oData) {
                        if(oRecord.getData("rsStatus") == "Not Enabled") {
                            elCell.innerHTML = "<font color='red'>" + oData + "</font>";
                        }
                        else {
                            elCell.innerHTML = "<font color='green'>" + oData + "</font>";
                        }
                    };

        var actionFormatter = function(elCell, oRecord, oColumn, oData) {
                            elCell.innerHTML = 'abc';


/*
        <a id="deploy" href="javascript:deployRule('${ruleSet.name}' , 'status-${ruleSet.id}')">Enable</a>&nbsp;&nbsp;
        <a id="deploy" href="javascript:unDeployRule('${ruleSet.name}' , 'status-${ruleSet.id}')">Disable</a>&nbsp;&nbsp;


        <!-- <a id="export" href="javascript:exportRule('${ruleSet.name}')">Export</a>-->
        <a href="<c:url value="/pages/rule/export?ruleSetName=${ruleSet.name}"/>">Export/Download</a>&nbsp;&nbsp;
        <a href="<c:url value="/pages/rule/util?ruleSetName=${ruleSet.name}"/>"><font color="red">Delete</font></a>&nbsp;&nbsp;
*/

                    };

        // Add the custom formatter to the shortcuts
        YAHOO.widget.DataTable.Formatter.myCustom = myCustomFormatter;
//        YAHOO.widget.DataTable.Formatter.actionFormatter = actionFormatter;

        var myDataSource = new YAHOO.util.DataSource(YAHOO.example.Data.rsList.slice(0,50));
        myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
        myDataSource.responseSchema = {
            fields: ["rsLevel", "rsDescription", "rsOrganization", "rsStudyID", "rsStatus", "rsAction"]
        };

        //Create config
        var oConfigs = {
				initialRequest: "results=50",
				draggableColumns:false
			};
        var myDataTable = new YAHOO.widget.DataTable("basic", myColumnDefs, myDataSource, oConfigs);

        return {
            oDS: myDataSource,
            oDT: myDataTable
        };
    }();
});

    /////////////////////////////////
    
    function handleAction(selectElement, id, name, divId){
    	var action = selectElement.options[selectElement.selectedIndex].text;
    	if(confirm('Are you sure you want to take the action - ' + action + ' ?')){
    		switch (action) {
    	    	case "Please select": break;
        	    case "Edit"         : var url = '<c:url value="/pages/rule/create?from=list&_page=0&_target1=1" />' + '&ruleSetId=' + id;
                			          window.location = url; 
						              break;
            	case "Enable"       : deployRule(name, divId); break;
               	case "Disable"      : unDeployRule(name, divId);  break;
                case "Export"       : var url = '<c:url value="/pages/rule/export?ruleSetName="/>' + name;
            				          document.location = url;  
            				          break;
            	case "Delete"       : var url = '<c:url value="/pages/rule/util?ruleSetName="/>' + name;
           					          document.location = url;
           					          break; 
            }
    	}
    }

    </script>
</head>
<body>

    <p><tags:instructions code="listrules" /></p>
    <div id="basic" class="yui-skin-sam"></div>

</body>
</html>