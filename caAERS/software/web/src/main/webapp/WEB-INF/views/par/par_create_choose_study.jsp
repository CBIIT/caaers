<%@ include file="/WEB-INF/views/taglibs.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<title>Choose a Study</title>

<tags:dwrJavascriptLink objects="searchStudy"/>

<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<title>Search for a Study</title>
<style>
    .yui-dt table { width: 100%; }
    #yui-dt0-th-primarySponsorCode {width: 130px;}
    /*#yui-dt0-th-shortTitle {width: 350px;}*/
    #yui-dt0-th-primaryIdentifierValue{width: 150px;}
    #yui-dt0-th-phaseCode{width: 100px;}
    #yui-dt0-th-active {width: 50px;}
</style>

<script>

    function sync() {
       $('searchText').value = $F('searchText_');
    }

    function onKey(e) {
        var keynum = getKeyNum(e);
        if (keynum == 13) {
            Event.stop(e);
            buildTable('assembler', true);
        } else return;
    }

    function buildTable(form, validate) {
        var text = $F('searchText_');

        if (text == '') {
            if (validate) jQuery('#flashErrors').show();
        } else {
            $('indicator').show();
            jQuery('#flashErrors').hide();
            var parameterMap = getParameterMap(form);
            parameterMap["organizationID"] = "<c:out value="${command.organization.id}" />";
            searchStudy.getStudiesForCreateParticipant(parameterMap, "", text, "${command.organization.nciInstituteCode}", ajaxCallBack);
            $('indicator').hide();
            $('bigSearch').show();
        }
    }

/*
    ValidationManager.submitPostProcess = function(formElement, flag) {
//        flag = true;
        $('searchText').value = $('searchText_').value;
        $('_searchType').value = $('searchType').value;
        
*/
/*
        if (formElement.id != 'command') {
            return true
        } else {
            $('assignment.studySubjectIdentifier').value = $('studySubjectIdentifierInput').value
        }
        return flag;
*//*

        return flag;
    }
*/

    function ajaxCallBack(jsonResult) {
        $('indicator').className='indicator'
        initializeYUITable("tableDiv", jsonResult, myColumnDefs, myFields);
        hideCoppaSearchDisclaimer();

        var value = jQuery('input:radio[name=study]:checked').val();
        if (isNaN(value)) {
            var _value = "<c:out value="${command.searchText}"/>";
            var _el = jQuery('#_study' + _value);
            if (_el) {
                _el.attr('checked', 'checked');
                var _studyId = jQuery(_el).val();
                _elStudyIdText = jQuery('input:text[name=study]');
                jQuery(_elStudyIdText).val(_studyId);
                if (jQuery("#ids")) {
                    jQuery("#ids").show();
                }
            }
        } else {
        }

    }

    var linkFormatter = function(elCell, oRecord, oColumn, oData) {
        elCell.innerHTML = oData;
    };

    var actionsFormatter = function(elCell, oRecord, oColumn, oData) {
        elCell.innerHTML = "<img src='<c:url value="/images/orange-actions.gif" />'>";
    };

    var radioFormatter = function(elCell, oRecord, oColumn, oData) {
        var _id = oRecord.getData("id");
        var _piv = oRecord.getData("primaryIdentifierValue");
        var _checked = "";
        <c:if test="${not empty command.study.id}">
            if (${command.study.id} == _id) {
                _checked = "checked";
                if ($("ids")) $("ids").show();
            }
        </c:if>
        elCell.innerHTML = "<input id='_study" + _piv + "' type='radio' " + _checked + " value='" + _id + "' name='study' onclick='$(\"command\").study.value = " + _id + "; if ($(\"ids\")) $(\"ids\").show();'>&nbsp;&nbsp;";
    };

    var myColumnDefs = [
        {key:"active", label:"Select", sortable:true, resizeable:true, formatter : radioFormatter},
        {key:"primaryIdentifierValue", label:"Study ID", sortable:true, resizeable:true},
        {key:"shortTitle", label:"Title", sortable:true, resizeable:true},
/*
        {key:"primarySponsorCode", label:"Funding Sponsor", sortable:true, resizeable:true},
        {key:"phaseCode", label:"Phase", sortable:true, resizeable:true},
        {key:"status", label:"Status", sortable:true, resizeable:true}
*/
        {key:"actions", label:"&nbsp;", sortable:true, resizeable:true, formatter:actionsFormatter},
    ];

    var myFields = [
        {key:'id', parser:"string"},
        {key:'primaryIdentifierValue', parser:"string"},
        {key:'shortTitle', parser:"string"},
        {key:'status', parser:"string"},
        {key:'phaseCode', parser:"string"},
        {key:'primarySponsorCode', parser:"string"}
    ];

</script>

</head>
<body>

<div class="row">
    <div class="summarylabel"><b>Subject</b></div>
    <div class="summaryvalue">${command.participant.fullName}</div>
</div>
</p>

<chrome:box autopad="true" title="Search Criteria">

    <form:form id="searchForm" method="post" cssClass="standard">
        <tags:hasErrorsMessage hideErrorDetails="${hideErrorDetails}"/>
        <tags:jsErrorsMessage/>

        <div class="errors" id="flashErrors" style="display: none;">
            <span id="command_errors">Provide at least one character in the search field.</span>
        </div>

        <p><tags:instructions code="instruction_subject_as2s.searchstudy"/></p>

        <div class="row">
            <div class="label"></div>
            <div class="value" style="margin-left: 100px;">
                <input type="text" size="25" onkeydown="onKey(event);" value="${command.searchText}" id="searchText_">
                <tags:button color="blue" type="button" value="Search" size="small" icon="search" onclick="sync(); buildTable('assembler', true);"/>
                <img src="<c:url value="/images/alphacube/progress.gif" />" style="display:none;" id="indicator"></td>
                <c:set var="targetPage" value="${assignType == 'study' ? '_target0' : '_target1'}"/>
            </div>
        </div>

    </form:form>
</chrome:box>


<div id="bigSearch" style="display:none;">

    <chrome:box title="Results">
        <form:form id="assembler">
            <div>
                <input type="hidden" name="_prop" id="prop">
                <input type="hidden" name="_value" id="value">
            </div>
            <p><tags:instructions code="instruction_subject_as2s.searchstudyresults"/></p>

            <chrome:division id="single-fields">
                <div id="tableDiv"></div>
            </chrome:division>

            <div id="ids" style="display:none;">
                <br/>
                <chrome:division title="Study Subject Identifier">
                    <p><tags:instructions code="instruction_subject_enter.choosestudy.sid"/></p>
                    <label for="studySubjectIdentifierInput"><tags:requiredIndicator/>&nbsp;Study subject identifier</label>
                    <input id="studySubjectIdentifierInput" type="text" maxlength="2000" value="${command.assignment.studySubjectIdentifier}" name="studySubjectIdentifierInput" class="${not empty command.assignment.studySubjectIdentifier ? 'valueOK' : 'required'}" onkeyup="$('assignment.studySubjectIdentifier').value = this.value"/>
                </chrome:division>
            </div>
        </form:form>
    </chrome:box>
</div>
<%--STANDARD FORM --%>

<form:form  id="command">
    <tags:tabFields tab="${tab}"/>
    <tags:tabControls tab="${tab}" flow="${flow}"/>

    <form:hidden path="searchText"/>
    <div style="display:none;">
        <form:input path="study" cssClass="validate-NOTEMPTY" title="Study"/>
        <form:input path="assignment.studySubjectIdentifier" cssClass="validate-NOTEMPTY" title="Study Subject Identifier"/>
    </div>
</form:form>

<%--STANDARD FORM --%>

<script>
    jQuery(document).ready(function() {
        buildTable('assembler', false);
    });
</script>


</body>
</html>
