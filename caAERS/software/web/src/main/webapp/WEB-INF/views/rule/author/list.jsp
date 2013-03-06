<%--
Copyright SemanticBits, Northwestern University and Akaza Research

Distributed under the OSI-approved BSD 3-Clause License.
See http://ncip.github.com/caaers/LICENSE.txt for details.
--%>
<%@ include file="/WEB-INF/views/taglibs.jsp" %>
<csmauthz:accesscontrol var="hasRuleCreate" objectPrivilege="gov.nih.nci.cabig.caaers.domain.Rule:CREATE"/>
<html>
<head>
    <title>Manage rules</title>
    <tags:dwrJavascriptLink objects="authorRule"/>
    <style type="text/css">
        p.description {
            margin: 0.25em 0 0 1em;
        }
        div.submit {
            margin:0 0 0 145px;
			padding:0;
        }
        .value input[type=text] {
            width: 80%;
        }

        form {
            margin-top: 1em;
        }

        .updated {
            border: #494 solid;
            border-width: 1px 0;
            background-color: #8C8;
            padding: 1em 2em;
            text-align: center;
            margin: 1em 10%;
            font-weight: bold;
            font-size: 1.1em;
        }
		#basic table {
			width:100%;
		}
		.new_rule {
			margin:10px 0 10px 65px;
		}
    </style>
    
    
    
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
		
		
		function unDeployRule(name , divId) {
			try {
				authorRule.unDeployRuleSet(name, function(values) {
							alert("Successfully Disabled");
							document.getElementById(divId).innerHTML = "<font color='red'>Not Enabled</font>";
					});
			} catch(e) {alert(e)}
		}
		
		

	

YAHOO.example.Data = {

    rsList: [
<c:forEach items="${command.ruleSets}" var="rs" varStatus="status">
        {
            rsLevel: "${empty rs.level ? 'NA' : rs.level}",
            rsDescription: "${rs.description}",
            rsOrganization: "${empty rs.organization ? 'NA' : rs.organization}",
            rsStudyID: "${rs.study}",
            rsStatus: "<div id='status-${rs.id}'>${rs.coverage}</div>"

            ,
            rsAction: "<select id='action-id' onChange=\"javascript:handleAction(this, '${rs.id}', '${rs.name}', 'status-${rs.id}')\">" +
            			"<option value=\"\">Please select</option>" +
                        "<option value=\"\">View/Edit</option>" +
                       <c:if test="${hasRuleCreate}">
            			"<option value=\"\">Enable</option>" +
            			"<option value=\"\">Disable</option>" +
            			"<option value=\"\">Export</option>" +
            			"<option value=\"\">Delete</option>" +
                       </c:if>
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



                    };

        // Add the custom formatter to the shortcuts
        YAHOO.widget.DataTable.Formatter.myCustom = myCustomFormatter;
//        YAHOO.widget.DataTable.Formatter.actionFormatter = actionFormatter;

        var myDataSource = new YAHOO.util.DataSource(YAHOO.example.Data.rsList.slice(0,50));
        myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
        myDataSource.responseSchema = {
            fields: ["rsLevel", "rsDescription", "rsOrganization", "rsStudyID", "rsStatus"
                     , "rsAction"
                     ]
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
    	if(action != 'Please select')
    	if(confirm('Are you sure you want to take the action - ' + action + ' ?')){
    		switch (action) {
    	    	case "Please select": break;
        	    case "View/Edit"         : var url = '<c:url value="/pages/rule/${hasRuleCreate ?'create' : 'read'}?from=list&_page=0&${hasRuleCreate ? '_target1=1' : '_target0=0'}" />' + '&ruleSetId=' + id;
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
<chrome:box title="Manage / Import rules" autopad="true">
	<chrome:division title="Manage rules" id="rule-set-id" >
	    <tags:instructions code="listrules" />
    	<div id="basic" class="yui-skin-sam"></div>
		<csmauthz:accesscontrol objectPrivilege="gov.nih.nci.cabig.caaers.domain.Rule:CREATE">
		<div class="new_rule">
			<c:set var="create_url"><c:url value="/pages/rule/create"/></c:set>
			<tags:button color="blue" icon="add" size="small" type="button" value="New Rule" markupWithTag="a" href="${create_url}"/>
		</div>
		</csmauthz:accesscontrol>
		</div>
    </chrome:division>
    <csmauthz:accesscontrol objectPrivilege="gov.nih.nci.cabig.caaers.domain.Rule:CREATE">
    <chrome:division title="Import rules" id="import-rules-id">
    	<p>
			<tags:instructions code="importxmlrules" />
		</p>
		<c:if test="${command.updated}">
			<p class="updated">${command.message}</p>
		</c:if>
		<form:form action="${action}" enctype="multipart/form-data" cssClass="standard">
            <div class="row">
                <div class="label">
                    Select XML file
                </div>
                <div class="value">
                	<input type="file" onchange="$('add-rule').removeAttribute('disabled')" name="ruleSetFile1" id="browse_field" size="50"/>
                </div>
            </div>    
        <div class="row submit">
            <tags:button id="add-rule" disabled="disabled" color="green" type="submit" value="Import" size="small" icon="check" />
        </div>
    </form:form>

    </chrome:division>
    </csmauthz:accesscontrol>
</chrome:box>

</body>
</html>
