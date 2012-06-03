<%@ include file="/WEB-INF/views/taglibs.jsp" %>

<html>
<head>
    <title>Search for a Subject</title>
    <tags:dwrJavascriptLink objects="createParticipant"/>

    <style>
        .yui-dt table { width: 100%; }
    </style>
    
    <script type="text/javascript">

        function navRollOver(obj, state) {
            document.getElementById(obj).className = (state == 'on') ? 'resultsOver' : 'results';
        }
        
        function onKey(e) {
            var keynum = getKeyNum(e);
            if (keynum == 13) {
                Event.stop(e);
                buildTable('assembler', true);
            } else return;
        }

        function buildTable(form, validate) {
            var text = $F('searchText');
            if (text == '') {
                if (validate) jQuery('#flashErrors').show();
            } else {
                $('indicator').show();
                jQuery('#flashErrors').hide();
                $('indicator').className = ''
                var parameterMap = getParameterMap(form);
                createParticipant.getParticipantTable(parameterMap, "", text, ajaxCallBack)
                $('bigSearch').show();
            }
        }

        function selectParticipant(selectedParticipant){
             $('command').participant.value = selectedParticipant;
        }

        function ajaxCallBack(jsonResult) {
            $('indicator').className='indicator'
            //document.getElementById('tableDiv').innerHTML = jsonResult;
            initializeYUITable("tableDiv", jsonResult, myColumnDefs, myFields);
            hideCoppaSearchDisclaimer();
        }

        var linkFormatter = function(elCell, oRecord, oColumn, oData) {
                var orgId = oRecord.getData("id");
                elCell.innerHTML = "<a href='asaelEdit?agentID=" + orgId + "'>" + oData + "</a>";
        };

        var radioFormatter = function(elCell, oRecord, oColumn, oData) {

                var _ss = 0;
                var _checked = "";
                var _id = oRecord.getData("id");

                <c:if test="${command.participant.id > 0}">
                    _ss =  ${command.participant.id};
                </c:if>

                if (_ss == _id) {
                    _checked = "checked";
                }

                elCell.innerHTML = "<input " + _checked + " type=\"radio\" onclick=\"selectParticipant(this.value)\" value=\"" + _id + "\" id=\"participant" + _id + "\" name=\"participant\">&nbsp;" + oData;
        };

        var myColumnDefs = [
            {key:"primaryIdentifierValue", label:"Primary ID", sortable:true, resizeable:true, formatter: radioFormatter},
            {key:"firstName", label:"First Name", sortable:true, resizeable:true},
            {key:"lastName", label:"Last Name", sortable:true, resizeable:true},
            {key:"studySubjectIdentifiersCSV", label:"Study Subject Identifiers", sortable:true, resizeable:true},
            {key:"gender", label:"Gender", sortable:true, resizeable:true},
            {key:"race", label:"Race", sortable:true, resizeable:true},
            {key:"ethnicity", label:"Ethnicity", sortable:true, resizeable:true}
        ];

        var myFields = [
            {key:'id', parser:"string"},
            {key:'firstName', parser:"string"},
            {key:'lastName', parser:"string"},
            {key:'primaryIdentifierValue', parser:"string"},
            {key:'studySubjectIdentifiersCSV', parser:"string"},
            {key:'gender', parser:"string"},
            {key:'race', parser:"string"},
            {key:'ethnicity', parser:"string"}
        ];

    </script>


</head>
<body>

<chrome:box autopad="true" title="Search Criteria">
  <form:form id="searchForm" method="post" cssClass="standard">
        <tags:hasErrorsMessage hideErrorDetails="${hideErrorDetails}"/>
        <tags:jsErrorsMessage/>

      <tags:instructions code="instruction_subject_as2s.searchsub"/>

      <div class="errors" id="flashErrors" style="display: none;">
          <span id="command_errors">Provide at least one character in the search field.</span>
      </div>

      <div class="row">
          <div class="label"></div>
          <div class="value">
              <form:input path="searchText" id="searchText" size="30" onkeydown="onKey(event);"/>&nbsp;
              <tags:button color="blue" type="button" value="Search" size="small" icon="search" onclick="buildTable('assembler', true);"/>
              <img src="<c:url value="/images/alphacube/progress.gif" />" style="display:none;" id="indicator">
          </div>
      </div>
      <c:set var="targetPage" value="${assignType == 'study' ? '_target1' : '_target0'}"/>

  </form:form>
</chrome:box>

<div id="bigSearch" style="display:none;">
    <form:form id="assembler">
        <div>
            <input type="hidden" name="_prop" id="prop">
            <input type="hidden" name="_value" id="value">
        </div>
        <chrome:box title="Results">
            <p><tags:instructions code="instruction_subject_as2s.searchsubresults"/></p>
            <chrome:division id="single-fields">
                <div id="tableDiv">
                    <c:out value="${assembler}" escapeXml="false"/>
                </div>
            </chrome:division>
        </chrome:box>
    </form:form>
</div>

  <script>
      Event.observe(window, "load", function(){
          buildTable('assembler', false);
      })
  </script>
  

<form:form id="command">
	    <form:hidden path="participant"/>
     <tags:tabFields tab="${tab}"/>
     <tags:tabControls tab="${tab}" flow="${flow}"/>
</form:form>

</body>
</html>

<!-- END views\par\reg_participant_search.jsp -->
