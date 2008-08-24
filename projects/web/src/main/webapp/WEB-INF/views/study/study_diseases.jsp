<%@ include file="/WEB-INF/views/taglibs.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<style type="text/css">
        .leftpanel { margin:1px 0px 5px;}
		#build-name{
			position: relative;
			clear: left;
		}        
</style>

<title>${tab.longTitle}</title>

<tags:includeScriptaculous />
<tags:dwrJavascriptLink objects="createStudy" />
<tags:dwrJavascriptLink objects="createAE" />
<script type="text/javascript">

    function fireAction(action, selected){
        if(action == 'addMeddraStudyDisease'){
           if(!$F('diseaseLlt')) return;
        }
		
		if(action == 'addStudyDisease'){
			addDiseasesToCart()
        }

      	document.getElementById('command')._target.name='_noname';
        document.studyDiseasesForm._action.value=action;
        document.studyDiseasesForm._selected.value=selected;
        document.studyDiseasesForm.submit();
    }

       function clearField(field){
          field.value="";
          }

       function hover(index)
       {
           //var sel = $("disease-sub-category")
           //alert (sel.value)

       }

       var diseaseAutocompleterProps = {
           basename: "disease",
           populator: function(autocompleter, text) {
               createStudy.matchDiseaseCategories(text, '' , function(values) {
                   autocompleter.setChoices(values)
               })
           },
           valueSelector: function(obj) {
               return obj.name // + "<b> ::</b> " + obj.id
           }
       }


       function acPostSelect(mode, selectedChoice) {
           //Element.update(mode.basename + "-selected-name", mode.valueSelector(selectedChoice))
           updateCategories(selectedChoice.id);
           //$(mode.basename).value = selectedChoice.id;
           //$(mode.basename + '-selected').show()
           //new Effect.Highlight(mode.basename + "-selected")
       }

       function updateSelectedDisplay(mode) {
           if ($(mode.basename).value) {
               Element.update(mode.basename + "-selected-name", $(mode.basename + "-input").value)
               $(mode.basename + '-selected').show()
           }
       }

       function acCreate(mode) {
           new Autocompleter.DWR(mode.basename + "-input", mode.basename + "-choices",
               mode.populator, {
               valueSelector: mode.valueSelector,
               afterUpdateElement: function(inputElement, selectedElement, selectedChoice) {
                   acPostSelect(mode, selectedChoice)
               },
               indicator: mode.basename + "-indicator"
           })
           Event.observe(mode.basename + "-clear", "click", function() {
               $(mode.basename + "-selected").hide()
               $(mode.basename).value = ""
               $(mode.basename + "-input").value = ""
           })
       }


        function updateCategories(id) {
           createStudy.matchDiseaseCategoriesByParentId(id, function(categories) {
               var sel = $("disease-sub-category")
               sel.size= categories.length < 10 ? categories.length + 2 : 10 ;
               //sel.size= 10
               sel.options.length = 0
               sel.options.add(new Option("All", ""))
               sel.options[0].selected=true;
               categories.each(function(cat) {
                   var opt = new Option(cat.name, cat.id)
                   sel.options.add(opt)
               })
               showDiseases()
           })

       }

       function showDiseases() {
           var categoryId = $("disease-sub-category").value
           var subCategorySelect = $("disease-sub-category")
           // If all is selected
           if ( subCategorySelect.value == "" ){
               var diseaseTermSelect = $("disease-term")
               diseaseTermSelect.options.length = 0
               diseaseTermSelect.size = 10
               diseaseTermSelect.options.add(new Option("All", ""))
               diseaseTermSelect.options[0].selected=true;
               //alert(subCategorySelect.length);
               for ( i=1; i < subCategorySelect.length; i++){
                   var catId = subCategorySelect.options[i].value

                   createStudy.matchDiseaseTermsByCategoryId(catId, function(diseases) {

                       diseases.each(function(cat) {
                       var opt = new Option(cat.ctepTerm, cat.id)
                       diseaseTermSelect.options.add(opt)
                       })
                   })
               }

           }
           else {
               createStudy.matchDiseaseTermsByCategoryId(categoryId, function(diseases) {
                   var sel = $("disease-term")
                   sel.size= diseases.length + 2;
                   sel.options.length = 0
                   sel.options.add(new Option("All", ""))
                   sel.options[0].selected=true;
                   diseases.each(function(cat) {
                       var opt = new Option(cat.term, cat.id)
                       sel.options.add(opt)
                   })
               })
           }
       }

       /**
        * Copy Diseases from  [Diseases MultiSelect]
        *   to the [Selected Diseases MultiSelect]
        *
        */
       function addDiseasesToCart() {
           var diseaseTerm = $("disease-term");
           var diseaseSelected = $("disease-sel");
           var diseaseSelectedHidden = $("disease-sel-hidden");
           if ( diseaseSelected.options[0].value == "" ){
               diseaseSelected.options.length = 0
           }
           // If all is selected  in the [Diseases MultiSelect]
           if (diseaseTerm.options[0].selected ){
               for ( i=1; i < diseaseTerm.length; i++)
               {
                   var opt = new Option(diseaseTerm.options[i].text, diseaseTerm.options[i].value)
                   var opt1 = new Option(diseaseTerm.options[i].text, diseaseTerm.options[i].value)
                   diseaseSelected.options.add(opt)
                   diseaseSelectedHidden.options.add(opt1)
               }
           }
           // If anything other than all is selected
           else {
               for ( i=1; i < diseaseTerm.length; i++)
               {
                   if (diseaseTerm.options[i].selected) {
                   var opt = new Option(diseaseTerm.options[i].text, diseaseTerm.options[i].value)
                   //var opt1 = new Option(diseaseTerm.options[i].text, diseaseTerm.options[i].value)
                   diseaseSelected.options.add(opt)
                   //diseaseSelectedHidden.options.add(opt1)
                   }
               }
           }
           // Copy over [Selected Diseases MultiSelect] to [Hidden Selected Diseases MultiSelect]
           //selectAll(diseaseSelectedHidden)
           synchronizeSelects(diseaseSelected,diseaseSelectedHidden);
       }

       function synchronizeSelects(selectFrom, selectTo)
       {
           // Delete everything from the target
           selectTo.options.length=0;
           // iterate over the source and add to target
           for ( i=0; i < selectFrom.length; i++){
                   var opt = new Option(selectFrom.options[i].text, selectFrom.options[i].value)
                   selectTo.options.add(opt)
                   selectTo.options[i].selected=true;
               }
       }

       function removeDiseasesFromCart()
       {
           var diseaseSelected = $("disease-sel");
           var diseaseSelectedHidden = $("disease-sel-hidden");

           for ( i=0; i < diseaseSelected.length; i++)
               {
                  if (diseaseSelected.options[i].selected) {
                     diseaseSelected.options[i] = null
                   }
               }
           synchronizeSelects(diseaseSelected,diseaseSelectedHidden)

       }

       function populateSelectsOnLoad()
       {

           if ( $('disease-input').value.length > 0 )
           {
               createStudy.matchDiseaseCategories($('disease-input').value, '' , function(values) {
                       updateCategories(values[0].id);
               })
           }
       }

       Event.observe(window, "load", function() {
       	   <c:if test="${diseaseTerminology == 'CTEP' }">
           $('disease-sel').style.display='none';
           $('disease-sel-hidden').style.display='none';

           acCreate(diseaseAutocompleterProps)
           updateSelectedDisplay(diseaseAutocompleterProps)

           Event.observe("disease-sub-category", "change", function() { showDiseases() })
           populateSelectsOnLoad();
           </c:if>
           
            <c:if test="${diseaseTerminology == 'MEDDRA' }">
            var meddraVersionId = ${meddraVersionId};
            AE.createStandardAutocompleter('diseaseLlt',
			function(autocompleter, text) {
					createAE.matchLowLevelTermsByCode(meddraVersionId, text, function(values) {
													autocompleter.setChoices(values)
												})
				},
				function(lowLevelTerm) { return lowLevelTerm.fullName });
			</c:if>	
           
       })

    </script>
</head>
<body>
<study:summary />
<div style="clear:both;">
    <%-- Can't use tags:tabForm b/c there are two boxes in the form --%>
    <form:form method="post" name="studyDiseasesForm" >
      <input type="hidden" name="_action" value="">
      <input type="hidden" name="_selected" value="">
      <tags:tabFields tab="${tab}"/>
      <div class="leftpanel">
      <chrome:box title="${tab.shortTitle}" id="all-disease">
		
            <c:if test="${diseaseTerminology == 'CTEP' }">
            <chrome:division title="CTEP Disease Terms" id="disease">
                    Search for a Disease Category<br>
                    <form:input size="45" id="disease-input"  path="diseaseCategoryAsText" />
                    <tags:indicator id="disease-indicator" />
                    <div id="disease-choices" class="autocomplete"></div>
                    <input type="button" id="disease-clear" value="Clear" />
                    <p id="disease-selected" style="display: none"></p>

                    <br><br>Select a Sub Category<br>
                    <select multiple size="1" onmouseover="javascript:hover()" style="width:400px" id="disease-sub-category">
                        <option value="">Please select a Category first</option>
                    </select>

                    <br><br>Diseases<br>
                    <select multiple size="1" style="width:400px" id="disease-term">
                        <option value="">Please select a Category first</option>
                    </select> <span id="disease-selected-name"></span>
                    <input class='ibutton' type='button' onclick="fireAction('addStudyDisease','0');" value='Add disease'  title='Add disease'/>
                   <br>

                    <select multiple size="10" id="disease-sel">
                        <option value="">No Selected Diseases</option>
                    </select> <form:select id="disease-sel-hidden" size="1"
                        path="diseaseTermIds">
                    </form:select>
                    <tags:tabControls tab="${tab}" flow="${flow}"/>    
            </chrome:division>
            </c:if>
            
            <c:if test="${diseaseTerminology == 'MEDDRA' }">
            <chrome:division title="${meddraVersion} Terms">
					Enter a MedDRA code (or multiple codes separated by a comma) and then click Add.<br>
					<form:hidden  path="diseaseLlt" />
					<input  size="45" type="text" id="diseaseLlt-input" value="" class="autocomplete"/>
                    <input type="button" id="diseaseLlt-clear" value="Clear"/>
                    <input class='ibutton' type='button' onclick="fireAction('addMeddraStudyDisease','0');" value='Add disease'  title='Add disease'/>
                    <tags:indicator id="diseaseLlt-indicator"/>
                    <div id="diseaseLlt-choices" class="autocomplete"></div>
                    
                    <tags:tabControls tab="${tab}" flow="${flow}"/>    
            </chrome:division>
            </c:if>
            
        </chrome:box>
        </div>   
        <div class="rightpanel">
        <chrome:box title="Selected Diseases " id="diseases">
          <tags:hasErrorsMessage hideErrorDetails="${hideErrorDetails}"/>
            <!-- CTEP -->
            <c:if test="${diseaseTerminology == 'CTEP' }">
            <chrome:division title="CTEP">
			<center>
			<table width="100%" class="tablecontent">
    			<tr>
    				<th scope="col" align="left"><b>CTC disease term</b> </th>
    				<th scope="col" width="10%" align="left"><b>Primary</b> </th>
    				<th scope="col" width="5%" align="left"></th>
    			</tr>
    			 <c:forEach items="${command.ctepStudyDiseases}" var="studyDisease" varStatus="status">
    			<tr>    				
            		<td><div class="label">${studyDisease.term.ctepTerm}</div></td>
            		<td><div class="label"><form:checkbox  path="ctepStudyDiseases[${status.index}].leadDisease" /></div></td>
            		<td><div class="label"><a href="javascript:fireAction('removeStudyDisease',${status.index});">
								<img src="<c:url value="/images/checkno.gif"/>" border="0" alt="Delete"></a></div></td>
            	</tr>
            	</c:forEach>
            	 <c:if test="${fn:length(command.ctepStudyDiseases) == 0}" >
            	 	<td><div class="label"><i>No terms selected<i></div></td>
            	 </c:if>
             </table>
             </center>
            </chrome:division>
            </c:if>
            <!-- MedDRA -->
            <c:if test="${diseaseTerminology == 'MEDDRA' }">
            <chrome:division title="MedDRA">   
            
            <center>
			<table width="100%" class="tablecontent">
    			<tr>
    				<th scope="col" align="left"><b>MedDRA disease term</b> </th>
    				<th scope="col" width="5%" align="left"></th>
    			</tr>
    			<c:forEach items="${command.meddraStudyDiseases}" var="meddraStudyDisease" varStatus="status">
    			<tr>    				
            		<td><div class="label">${meddraStudyDisease.term.meddraTerm}</div></td>
            		<td><div class="label"><a href="javascript:fireAction('removeMeddraStudyDisease',${status.index});">
                                		<img src="<c:url value="/images/checkno.gif"/>" border="0" alt="Delete"></a></div></td>
            	</tr>
            	</c:forEach>
            	 <c:if test="${fn:length(command.meddraStudyDiseases) == 0}" >
            	 	<td><div class="label"><i>No terms selected<i></div></td>
            	 </c:if>
            	
             </table>
             </center>
			</chrome:division>
			</c:if>
        </chrome:box>
        </div>
    </form:form>
 </div>
</body>
</html>
