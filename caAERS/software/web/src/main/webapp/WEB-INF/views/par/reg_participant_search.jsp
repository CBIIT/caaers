<%@ include file="/WEB-INF/views/taglibs.jsp" %>

<html>
<head>
    <title>Search for a Subject</title>
    <tags:dwrJavascriptLink objects="createParticipant"/>

    <script type="text/javascript">

        function navRollOver(obj, state) {
            document.getElementById(obj).className = (state == 'on') ? 'resultsOver' : 'results';
        }
        
        function onKey(e) {
            var keynum = getKeyNum(e);

            if (keynum == 13) {
                Event.stop(e);
                buildTable('assembler');
            } else return;
        }

        function buildTable(form) {

            var text = $F('searchText');
            $('indicator').show();

            if (text == '') {
                $('error').innerHTML = "<font color='#FF0000'>Provide at least one character in the search field.</font>";
            } else {
                $('error').innerHTML = ""
                $('indicator').className = ''
                var type = $('searchType').options[$('searchType').selectedIndex].value;

                var parameterMap = getParameterMap(form);
                createParticipant.getParticipantTable(parameterMap, type, text, showTable)

                $('bigSearch').show();

            }
        }

        function selectParticipant(selectedParticipant){
             $('command').participant.value = selectedParticipant;
        }

    </script>

</head>
<body>


  <form:form id="searchForm" method="post" cssClass="standard">
        <tags:hasErrorsMessage hideErrorDetails="${hideErrorDetails}"/>
        <tags:jsErrorsMessage/>
      
		<table border="0" cellspacing="2" cellpadding="2" class="search" width="100%">
        <p><tags:instructions code="instruction_subject_as2s.searchsub"/></p>
        <tr>
            
            <td class="searchType">Search for subject by</td>
            <td>
                <form:select path="searchType">
                    <form:options items="${participantSearchType}" itemLabel="desc" itemValue="code"/>
                </form:select>
            </td>
            <td><form:input path="searchText" id="searchText" size="30" onkeydown="onKey(event);"/></td>
            <c:set var="targetPage" value="${assignType == 'study' ? '_target1' : '_target0'}"/>
            <td width="100%">
                <tags:button color="blue" type="button" value="Search" size="small" icon="search" onclick="buildTable('assembler');"/>
                <img src="<c:url value="/images/alphacube/progress.gif" />" style="display:none;" id="indicator">
            </td>
        </tr>
        <tr>
            <td></td>
            <td></td>
            <td class="notation">
                <div id="error"></div>
            </td>

        </tr>

    </table>
  </form:form>

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


<form:form  id="command">
	<form:hidden path="participant"/>
     <tags:tabFields tab="${tab}"/>
     <tags:tabControls tab="${tab}" flow="${flow}"/>
</form:form>
</body>
</html>

<!-- END views\par\reg_participant_search.jsp -->