<%@include file="/WEB-INF/views/taglibs.jsp"%>
<%@include file="/WEB-INF/views/taglibs.jsp"%>
<html>
<head>
<tags:dwrJavascriptLink objects="authorRule,createAE" />
<script type="text/javascript">
      
      Event.observe(window, "load", function() {
				destroyLineItemSortables();
				createLineItemSortables();
				createRuleSortable();
				//Event.observe("add-condition-image", "click", function() { fetchCondition() } );
				
				//remove the query string from form url
	    		removeQueryStringFromForm('command');
			});
			
			function createLineItemSortables() {
				for(var i = 0; i < sections.length; i++) {
					Sortable.create(sections[i],{tag:'div',dropOnEmpty: true, containment: sections,only:'lineitem'});
				}
			}

			function destroyLineItemSortables() {
				for(var i = 0; i < sections.length; i++) {
					Sortable.destroy(sections[i]);
				}
			}

			function createRuleSortable() {
				Sortable.create('allRules', {tag:'div',only:'section',handle:'handle'});
			}
			
	</script>
<title>Specify Rules for Trigger</title>
<style>
div.section, div#createNew {
	border: 1px solid #CCCCCC;
	margin: 30px 5px;
	padding: 0px 0px 10px 0px;
	background-color: #EFEFEF;
}
div#createNew input {
	margin-left: 5px;
}
div#createNew h3, div.section h3 {
	font-size: 14px;
	padding: 2px 5px;
	margin: 0 0 10px 0;
	background: url("<c:url value="/images/rule/window_titlebar.png" />");
	background-color: #6E81A6;
	display: block;
	color: #FFFFFF;
}
.new_rule {
	margin:10px 0 10px 35px;
}
.new_condition {
	margin:10px 0 10px 65px;
}
.one-condition {
	margin-bottom:10px;
}
</style>
<script type="text/javascript">
		//loadCategoryObjects();
		var sections = new Array();
		var callback = false;
		var newNode = 0;
		var domainObject = null;

		var categoryObjects2 = new Array()

 		
		function addRule() {
				
				try {
					
					authorRule.addRule(function (html) {
						sections.push('rule-' + (sections.length + 1));
						var columnHolder = getElementHolderDiv();
						columnHolder.innerHTML = html;
						// CAAERS-1152 child nodes count is 1 in IE , 2 in FF as FF counts white spaces , so 0(1-1) for IE and 1(2-1) for FF
						var len = columnHolder.childNodes.length;
						var newRule = columnHolder.childNodes[len-1].cloneNode(true);
						//var newRule = columnHolder.childNodes[1].cloneNode(true);
						columnHolder.innerHTML = "";
						$('allRules').appendChild(newRule);
						Effect.Appear(newRule.id);
						createRuleSortable();
					});

				} catch(e) {
					//alert(e)
				}
		}


		function fetchCondition(ruleCount, fieldIndex) {
				try {
					authorRule.addCondition(ruleCount, function(columnContent) {
							
							var columns = $('rule-'+(ruleCount + 1)+'-columns');
							var columnHolder = getElementHolderDiv();
							columnHolder.innerHTML = columnContent;
							// CAAERS-1152 child nodes count is 1 in IE , 2 in FF as FF counts white spaces , so 0(1-1) for IE and 1(2-1) for FF
							var len = columnHolder.childNodes.length;
							var newColumn = columnHolder.childNodes[len-1].cloneNode(true);
							//var newColumn = columnHolder.childNodes[1].cloneNode(true);	
							
							columnHolder.innerHTML = "";
							columns.appendChild(newColumn);
						
							Effect.Appear(newColumn.id);
							
							if (callback == true)
							{
							
								var domainObjectDropDownID = 'ruleSet.rule['+ ruleCount + '].condition.column[' + newNode + '].objectType'; 
								var domainObjectIdentifierID = 'ruleSet.rule['+ ruleCount + '].condition.column[' + newNode + '].identifier'; 
								var domainObjectDisplayUriID = 'ruleSet.rule['+ ruleCount + '].condition.column[' + newNode + '].displayUri'; 
							
			                        $(domainObjectDropDownID).value=domainObject.className;
						      		$(domainObjectIdentifierID).value=domainObject.identifier;
						      		$(domainObjectDisplayUriID).value=domainObject.displayUri;
	
							
								var newColumnId = 'ruleSet.rule['+ ruleCount + '].condition.column[' + newNode + '].fieldConstraint[0].fieldName';
								var expressionID = 'ruleSet.rule['+ ruleCount + '].condition.column[' + newNode + '].expression';
								var grammerPrefixID = 'ruleSet.rule['+ ruleCount + '].condition.column[' + newNode + '].fieldConstraint[0].grammerPrefix';
								
								//for term 
								$(grammerPrefixID).value=domainObject.field[fieldIndex].grammer.prefix;
								
								
								//alert(newColumnId);

				 				$(newColumnId).options.length = 0;
				 				$(newColumnId).options.add(new Option("Please select field", ""));
								
								// Set all the options
								domainObject.field.each(function(field)
								 		{
							 				$(newColumnId).options.add(new Option(field.displayUri, field.name));
										});
								
								$(newColumnId).value='term';
								
								var selectId =  newColumnId.substring(0,newColumnId.lastIndexOf(".")); 
								
								// Reset the operator
								
								domainObject.field.each(function(field)
										{
											if(field.name == 'term')
											{
												var operatorDropDownID = selectId + '.literalRestriction[0].evaluator';
												
												$(operatorDropDownID).options.length = 0;
												$(operatorDropDownID).options.add(new Option("Please select operator",""));

												field.operator.each(function(operator)
													{
														$(operatorDropDownID).options.add(new Option(operator.displayUri,operator.name));														
														
													});
													
												$(expressionID).value = field.expression;	
											}
										});
										
								var validValueField = document.getElementById(selectId + '.literalRestriction[0].value');

								var newId = validValueField.id; 
								var spanId = newId + '.span';
								//Element.remove(validValueField);
								$(spanId).innerHTML="";
								
								
								
								createAE.getTermsByCategory(0, function(terms) {
								
								var selectId =   newId.substring(0,newId.lastIndexOf(".")); 
			
								var displayUriID = selectId+ '.readableValue';
								
									var selectArea = '<select id="' + newId + '" name="' + newId +'" multiple="multiple"  size="3"'+' onchange="handleValueOnselectNonValidValues(this)"' +'>';
										
									var hiddenField = '<input type="hidden" id="'+displayUriID + '" name="'+displayUriID + '" />';

							
										$(spanId).innerHTML = selectArea + hiddenField;

										var sel = $(newId);	
				        		        
				                	    sel.options.length = 0
				                    
				                    
				                    	terms.each(function(term) {
											var tempT='';
											if (term.select != null) {
												tempT='-' + term.select
											}
				                        	var opt = new Option(term.term + tempT, term.id)
				                        	sel.options.add(opt)
				                    	})
				                })

								
													                

							}
							callback=false;
							
					});
				
				}catch (e) {
					//alert(e)
				}
		}
		
		function removeCondition(ruleCount, columnCount) {
				if (!confirm("Are you sure you want to delete this condition?"))
                 	return false;
				try {
					authorRule.removeCondition(ruleCount, columnCount, function(deleteStatus) {
							if(deleteStatus) {
								var columns = $('rule-'+(ruleCount+1)+'-columns');
								var column = $('rule-'+(ruleCount)+'-column-'+(columnCount));
								column.remove();
								//column.style.display = "none";
								
								//columns.removeChild($(column.id + '-br'));
								//columns.removeChild(column);
								
							} else {
								alert("Delete failed on server " + values)
							}
					});
				
				}catch (e) {
					alert("Exception " + e)
				}	
					
		}
		
		// ----------------------------------------------------------------------------------------------------------------
		//javascript:fireAction(index,section-id,sectionCSS) : This function will be called when the delete button on the AE is clicked.;
		function fireAction(ruleCount, sectionId, sectionCSS) {
			deleteRule(ruleCount);
		}

    // ----------------------------------------------------------------------------------------------------------------
		
		function deleteRule(ruleCount) {
				
				if (!confirm("Are you sure you want to delete this rule?"))
                 	return false;
				try {
					authorRule.removeRule(ruleCount-1, function(deleteStatus) {
							if(deleteStatus) {
									//toggle(ruleCount);
									var rules = $('allRules');
									var rule = $('rule-'+ruleCount);
									rule.remove();
									//rule.style.visibility="hidden";
									//alert ("ss");
									//rules.removeChild(rule);
									

								
							} else {
								alert("Delete failed on server " + values)
							}
					});
				
				}catch (e) {
					alert("Exception " + e)
				}
				
				
					
		
		}
		
		function getElementHolderDiv() {
			var elementHolderDiv = $('element-holder');
			if(elementHolderDiv == null) {
				elementHolderDiv = document.createElement("div");
				elementHolderDiv.setAttribute("id", "element-holder");
			}
			
			return elementHolderDiv;
		}
		




	
	var toggleArray = new Array();
	
	function toggle(ruleCount) {
		var toggleStatus = toggleArray[ruleCount];
		var imageObj = $('toggle-image-'+ruleCount);
		if(!toggleStatus) {
			$('rule-condition-action-container-'+ruleCount).style.display="none";
			imageObj.src="<c:url value="/images/rule/window-maximize.gif" />"
			toggleArray[ruleCount] = true;
		} else {
			AE.slideAndShow($('rule-condition-action-container-'+ruleCount));
			imageObj.src="<c:url value="/images/rule/window-minimize.gif" />"
			toggleArray[ruleCount] = false;
		}
	}

				function orgsPopulator(autocompleter, text) 
				{
						
				
					authorRule.matchSites(text, function(values) {
						                    autocompleter.setChoices(values)
						                })

				}
				
	
	
	
				function termPopulator(autocompleter, text) 
				{
						
					var selectedColumnId = autocompleter.element.id;
					
					var startIndex = selectedColumnId.indexOf('[');
					var endIndex = selectedColumnId.indexOf(']', startIndex);
					
					var ruleCount = parseInt(selectedColumnId.substring(startIndex+1, endIndex));

					// Check whether category exists
					var columns = $('rule-'+(ruleCount + 1)+'-columns');
				
				
					var divNodes = 0;
					
					for(var i=0; i < columns.childNodes.length; i++)
					{
						if (columns.childNodes[i].nodeName == 'DIV')
						{
							divNodes++;
						}
					}
							
					var category; 		

					for (var i=0; i < divNodes; i++)
					{
						var columnId = 'ruleSet.rule['+ ruleCount + '].condition.column[' + i + '].fieldConstraint[0].fieldName';
					
						if($(columnId).value == 'category')
						{
							var selectId =  columnId.substring(0,columnId.lastIndexOf(".")); 
		
							var validValueField = document.getElementById(selectId + '.literalRestriction[0].value');
							
							category = validValueField.value;
						}
					}
					
					

					
					createAE.matchTerms(text, 3, category, 10, function(values) {
						                    autocompleter.setChoices(values)
						                })

				}
	
				function termValueSelector(term) 
				{
					//alert(term.fullName);
					return term.fullName;
				}

	function isFieldExists(ruleCount, domainObjectName, fieldName)
	{

				if(true) return false
				
				var columns = $('rule-'+(ruleCount + 1)+'-columns');
				
				var divNodes = 0;
				var sameFields = 0;
				var fieldExist = false;

				// Filter all div nodes				
				for(var i=0; i < columns.childNodes.length; i++)
				{
					if (columns.childNodes[i].nodeName == 'DIV')
					{

						if (columns.childNodes[i].style.visibility != 'hidden') {							
							
							var fieldId = 'ruleSet.rule['+ ruleCount + '].condition.column[' + divNodes + '].fieldConstraint[0].fieldName';
							var domainObjectId = 'ruleSet.rule['+ ruleCount + '].condition.column[' + divNodes + '].objectType';
					
							if($(fieldId).value == fieldName && $(domainObjectId).value == domainObjectName)
							{
								sameFields++;
						
							}
						}
					
							divNodes++;
							
						
					}
				}
			
				if (sameFields > 1)
				{
					fieldExist = true;
				}


			return fieldExist;
	}
	
	function resetDropDowns()
	{
	}

		                      
	function handleValueOnselectNonValidValues(item)
	{

			var selectId =   item.id.substring(0,item.id.lastIndexOf(".")); 
			var val = "";
			var displayUriID = selectId+ '.readableValue';
			var selVal = "";

			for (var i = 0; i < item.options.length; i++) {
				if (item.options[ i ].selected) {
					 selVal = item.options[ i ].text;

					// g is for global replacement
            		 val = val + "," + selVal.replace(/\,/g,' ');

            	}
		    }
		    $(displayUriID).value = val.replace(/\,/,'');
			
	}


        function handleValueOntextNonValidValues(item)
        {

            var selectId =   item.id.substring(0,item.id.lastIndexOf("."));
            var val = "";
            var displayUriID = selectId+ '.readableValue';
            var selVal = $F(item);
            val =  selVal.replace(/\,/g,' ');
            $(displayUriID).value = val.replace(/\,/,'');

        }
	function handleValueOnselect(operatorDropDown, ruleCount, fieldIndex, multi)
	{
		
		//alert (fieldIndex);
		
		var selectedOperator = operatorDropDown.options[operatorDropDown.selectedIndex];
		var selectId =  operatorDropDown.id.substring(0,operatorDropDown.id.lastIndexOf(".")); 
		
		// Get the index of Domain Object
		var domainObjectDropDownID = selectId.substring(0,selectId.lastIndexOf("field")) + 'objectType';
		
		var domainObjectSelectedIndex = $(domainObjectDropDownID).selectedIndex;
		//alert (domainObjectSelectedIndex);
		
		var displayUriID = selectId+ '.readableValue';
		
		var values = "";
		//alert (displayUriID);		
		
		try 
		  {
			// Get the domain object
			
			authorRule.getRulesDomainObject(domainObjectSelectedIndex - 1, '${command.terminology}', function(object)
		               {
		                              		domainObject = object;
		                              		
		                    if (multi) {
		                      for (var i = 0; i < operatorDropDown.options.length; i++) {
		                    
		                    	if (operatorDropDown.options[ i ].selected) {
		                    		values = domainObject.field[fieldIndex].validValue[operatorDropDown.options[i].index].readableText + "," + values
		                    	}
		                      }
		                   } else {
		                   		values = domainObject.field[fieldIndex].validValue[operatorDropDown.selectedIndex-1].readableText
		                   }
		                    
		                    
		                    $(displayUriID).value = values;
						
							
		           })
		  }
		catch(e) 
		  {
			alert('Exception');
			alert(e);
		  }
		
	}
	
	function handleOperatorOnchange(operatorDropDown, ruleCount) {
	
		
		var selectedOperator = operatorDropDown.options[operatorDropDown.selectedIndex];
		var selectId =  operatorDropDown.id.substring(0,operatorDropDown.id.lastIndexOf(".")); 

		
		
		// Get the index of Domain Object
		var domainObjectDropDownID = selectId.substring(0,selectId.lastIndexOf("field")) + 'objectType';
		var attrDropDownID =   selectId.substring(0,selectId.lastIndexOf("literalRestriction")) + 'fieldName';
        var _attrValue = $F(attrDropDownID) ;
		
		var domainObjectSelectedIndex = $(domainObjectDropDownID).selectedIndex;
		//alert (domainObjectSelectedIndex);
		
		//ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].literalRestriction[0].displayUri
		
		var displayUriID = selectId+ '.displayUri';
		
		//alert (displayUriID);
		
		try 
		{
			// Get the domain object
			
			authorRule.getRulesDomainObject(domainObjectSelectedIndex - 1, '${command.terminology}', function(object)
		                              {
		                              		domainObject = object;
								
						//	alert(domainObject.field[0].operator[operatorDropDown.selectedIndex-1].displayUri);
						
							domainObject.field.each(function(fv){

                               if(fv.name == _attrValue){
                                   $(displayUriID).value = fv.operator[operatorDropDown.selectedIndex-1].readableText;
                               }
                            });

							
							//$(fieldDisplayUri).value = domainObject.field[fieldDropDown.selectedIndex-1].displayUri;
							
							
		                              })
		}
		catch(e) 
		{
			alert('Exception');
			alert(e);
		}
		
	}
				function orgValueSelector(organization) 
			{
				return organization.name;
			}
			
	function handleFieldOnchange(fieldDropDown, ruleCount, columnCount) 
	{
		var selectedField = fieldDropDown.options[fieldDropDown.selectedIndex];
		
		var selectId =  fieldDropDown.id.substring(0,fieldDropDown.id.lastIndexOf(".")); 
		
		var validValueField = document.getElementById(selectId + '.literalRestriction[0].value');

		
		
		
		// Get the index of Domain Object
		var domainObjectDropDownID = selectId.substring(0,selectId.lastIndexOf("."))  + '.objectType';
	//	alert (domainObjectDropDownID);
		var domainObjectSelectedIndex = $(domainObjectDropDownID).selectedIndex;
		
		var domainObjectIdentifierID = selectId.substring(0,selectId.lastIndexOf("."))  + '.identifier';

		var expressionID = selectId.substring(0,selectId.lastIndexOf("."))  + '.expression';

		// Get the index of Operator
		var operatorDropDownID = selectId + '.literalRestriction[0].evaluator';
		
		var grammerPrefix = selectId.substring(0,selectId.lastIndexOf("."))  + '.fieldConstraint[0].grammerPrefix';
		var grammerPostfix = selectId.substring(0,selectId.lastIndexOf("."))  + '.fieldConstraint[0].grammerPostfix';
		var fieldDisplayUri = selectId.substring(0,selectId.lastIndexOf("."))  + '.fieldConstraint[0].displayUri';
		
		
		var tIndex =selectedField.index-1;

		//var domainObject = null;
		
		// check whether the field already exists
		
		if (isFieldExists(ruleCount, $(domainObjectDropDownID).value, selectedField.value))
		{
			alert('Field already exisits');
			
			// Reset Fields, operators and values
			fieldDropDown.value = '';	
			$(expressionID).value='';	
			
			// Reset Operator 
			$(operatorDropDownID).options.length = 0;
			$(operatorDropDownID).options.add(new Option("Please select operator",""));
							
							
			// Reset the value selection span

			var selectArea = '<select id="' + validValueField.id + '" name="' + validValueField.id +'">';
							
			selectArea += '<option value="">Please select value</option></select>';

			$(validValueField.id+'.span').innerHTML = selectArea;
			
			
			return;
		}
		
		try 
		{
			// Get the domain object
			
			authorRule.getRulesDomainObject(domainObjectSelectedIndex - 1, '${command.terminology}', function(object)
		                              {
		                              		domainObject = object;
				
								// Reset the operators
								$(operatorDropDownID).options.length=0;
								$(operatorDropDownID).options.add(new Option("Please select operator", ""));
			
								domainObject.field[fieldDropDown.selectedIndex-1].operator.each(function(operator)
										{
											$(operatorDropDownID).options.add(new Option(operator.displayUri, operator.name));
								                })

								// Reset the expression
								$(expressionID).value=domainObject.field[fieldDropDown.selectedIndex-1].expression;
								
						//	alert(domainObject.field[fieldDropDown.selectedIndex-1].displayUri);
						//	alert(domainObject.field[fieldDropDown.selectedIndex-1].grammer.prefix);
							
							$(grammerPrefix).value = domainObject.field[fieldDropDown.selectedIndex-1].grammer.prefix;
							$(grammerPostfix).value = domainObject.field[fieldDropDown.selectedIndex-1].grammer.postfix;
							
							$(fieldDisplayUri).value = domainObject.field[fieldDropDown.selectedIndex-1].readableText;
		               })
		                              
		        // delay code
				var date = new Date();
				var curdate = null;
				 do {curdate = new Date();} 
				 	while (curdate - date < 100);
				 	
				 	//end 
				 	
				 	
				 	
			
			if (selectedField.value == 'term')
			{
				// Check whether category exists
				var columns = $('rule-'+(ruleCount + 1)+'-columns');
				
				//alert(columns.childNodes.length);
				
				
				var divNodes = 0;
				
				for(var i=0; i < columns.childNodes.length; i++)
				{
					if (columns.childNodes[i].nodeName == 'DIV')
					{
						divNodes++;
					}
				}
							
				var categoryExist = false;
				var categoryValueID;
				
				for (var i=0; i < divNodes; i++)
				{
					var columnId = 'ruleSet.rule['+ ruleCount + '].condition.column[' + i + '].fieldConstraint[0].fieldName';
					
					if($(columnId).value == 'category')
					{
						categoryExist = true;
						categoryValueID = 'ruleSet.rule['+ ruleCount + '].condition.column[' + i + '].fieldConstraint[0].literalRestriction[0].value';
						break;
					}
				}
				
				if (categoryExist == true)
				{
					var newId = validValueField.id; 
					var spanId = newId + '.span';
					//Element.remove(validValueField);
					$(spanId).innerHTML="";
					
					createAE.getTermsByCategory($(categoryValueID).value, function(terms) {
						                   

							var newId = validValueField.id; 
							var spanId = newId + '.span';


										
							var selectArea = '<select id="' + newId + '" name="' + newId +'" multiple="multiple"  size="3"'+' onchange="handleValueOnselectNonValidValues(this)"' +'>';
										
							var hiddenField = '<input type="hidden" id="ruleSet.rule['+ruleCount+'].condition.column['+columnCount+'].fieldConstraint[0].literalRestriction[0].readableValue"' + ' name="ruleSet.rule['+ruleCount+'].condition.column['+columnCount+'].fieldConstraint[0].literalRestriction[0].readableValue"' +'/>'

				
							//Element.remove(validValueField);

							
							$(spanId).innerHTML = selectArea + hiddenField;

							var sel = $(newId);	
				                
				                    sel.options.length = 0
				                    
				                    
				                    terms.each(function(term) {
										
										var tempT='';
											if (term.select != null) {
												tempT='-' + term.select
											}
											
				                        var opt = new Option(term.term + tempT, term.id)
				                        sel.options.add(opt)
				                    })
				                })
					
				}
				else
				{
					
					
					fieldDropDown.value='category';
					
				       createAE.getCtcCategoryByStudy(${not empty command.caaersRuleSet.study ? command.caaersRuleSet.study.id : 0}, function(categories) {

							var newId = validValueField.id; 
							var spanId = newId + '.span';

							var selectArea = '<select id="' + newId + '" name="' + newId +'" onchange="onCategoryChange(this, '+ruleCount+')">';
										selectArea += '</select>';
				

							var hiddenField = '<input type="hidden" id="ruleSet.rule['+ruleCount+'].condition.column['+columnCount+'].fieldConstraint[0].literalRestriction[0].readableValue"' + ' name="ruleSet.rule['+ruleCount+'].condition.column['+columnCount+'].fieldConstraint[0].literalRestriction[0].readableValue"' +'/>'
							
							
							$(spanId).innerHTML = selectArea+ hiddenField;

							var sel = $(newId);	
				                
				                    sel.options.length = 0
				                    sel.options.add(new Option("Any", ""))
				                    
				                    categories.each(function(cat) {
				                        var name = cat.name
				                        if (name.length > 45) name = name.substring(0, 45) + "..."
				                        var opt = new Option(name, cat.id)
				                        sel.options.add(opt)
				                    })
				                })
					
					
					// Add a new column for Term
					
					newNode = divNodes;
					callback = true;

					fetchCondition(ruleCount, fieldDropDown.selectedIndex - 2);
					
					// Reset all the dropdowns for 'term'
					

					
				}
	
			}
			else if (selectedField.value == 'category')
			{

				                
				          createAE.getCtcCategoryByStudy(${not empty command.caaersRuleSet.study ? command.caaersRuleSet.study.id : 0}, function(categories) {

							var newId = validValueField.id; 
							var spanId = newId + '.span';

							var selectArea = '<select id="' + newId + '" name="' + newId +'" onchange="onCategoryChange(this,' + ruleCount + ')">';
										selectArea += '</select>';
				
							var hiddenField = '<input type="hidden" id="ruleSet.rule['+ruleCount+'].condition.column['+columnCount+'].fieldConstraint[0].literalRestriction[0].readableValue"' + ' name="ruleSet.rule['+ruleCount+'].condition.column['+columnCount+'].fieldConstraint[0].literalRestriction[0].readableValue"' +'/>'
							
							$(spanId).innerHTML = selectArea + hiddenField;

							var sel = $(newId);	
				                
				                    sel.options.length = 0
				                    sel.options.add(new Option("Any", ""))
				                    
				                    categories.each(function(cat) {
				                        var name = cat.name
				                        if (name.length > 45) name = name.substring(0, 45) + "..."
				                        var opt = new Option(name, cat.id)
				                        sel.options.add(opt)
				                    })
				                })

			}
			else if (selectedField.value == 'investigationalNewDrugIndicator' || selectedField.value == 'investigationalNewDeviceIndicator') {

							var newId = validValueField.id; 
							var spanId = newId + '.span';
								
							
					var hiddenId = 'ruleSet.rule[' + ruleCount + '].condition.column[' + columnCount + '].fieldConstraint[0].literalRestriction[0].readableValue'; 
																	
															
					var inputArea = '<input type="text" id="' + newId + '" name="' + newId +'" size="35" class="autocomplete"/>';
					inputArea += '<img alt="activity indicator" src="<c:url value="/images/indicator.white.gif" />" class="indicator" id="ind-indicator"/>';
					

					var hiddenArea = '<input type="hidden" id="' + hiddenId + '" name="' + hiddenId +'" size="35"/>';
					
					
					
					$(spanId).innerHTML = inputArea + '<div id="' + newId + '-choices' + '" class="autocomplete"></div>' + hiddenArea;

	

				new Autocompleter.DWR(newId, newId + '-choices',
                	orgsPopulator, {
                	valueSelector: orgValueSelector,
                	afterUpdateElement: function(inputElement, selectedElement, selectedChoice) {                		
                		document.getElementById(hiddenId).value = orgValueSelector(selectedChoice);
                	},
                	indicator: "ind-indicator",
                	minChars : AE.autocompleterChars , 
                	frequency : AE.autocompleterDelay
                	
                	});

	
					
					
			}
            else if (selectedField.value == 'meddraCode') {
				var newId = validValueField.id; 
				var spanId = newId + '.span';
				var hiddenId = selectId + '.literalRestriction[0].readableValue'
				
				var inputArea = '<textarea id="' + newId + '" name="' + newId +'" ></textarea>';
				inputArea += '<img alt="activity indicator" src="<c:url value="/images/indicator.white.gif" />" class="indicator" id="ind-indicator"/>';
					
				var hiddenArea = '<input type="hidden" id="' + hiddenId + '" name="' + hiddenId +'" cols=40 rows=8/>';
				
				$(spanId).innerHTML = inputArea + '<div id="' + newId + '-choices' + '"></div>' + hiddenArea;
				
				
			}
            else if (selectedField.value == 'significanceLevel'){

                var newId = validValueField.id;
                var spanId = newId + '.span';
                var hiddenId = selectId + '.literalRestriction[0].readableValue'

                var inputArea = '<input id="' + newId + '" name="' + newId +'" onblur="handleValueOntextNonValidValues(this)" ></input>';
                inputArea += '<img alt="activity indicator" src="<c:url value="/images/indicator.white.gif" />" class="indicator" id="ind-indicator"/>';

                var hiddenArea = '<input type="hidden" id="' + hiddenId + '" name="' + hiddenId +'" cols=40 rows=8/>';

                $(spanId).innerHTML = inputArea + '<div id="' + newId + '-choices' + '"></div>' + hiddenArea;


            }
            else if (selectedField.value == 'reportDefinitionName' || selectedField.value == 'treatmentAssignmentCode') {
					
					var criteria1 = "" ;
					
					if (selectedField.value == 'reportDefinitionName') { 
						criteria1 = '${command.caaersRuleSet.organization.id}';
					}
					
					if (selectedField.value == 'treatmentAssignmentCode') { 
						criteria1 = '${command.caaersRuleSet.study.id}';
					}
										
					authorRule.getAjaxObjects(selectedField.value , criteria1 , function(values) {
						                   

							var newId = validValueField.id; 
							var spanId = newId + '.span';



							var selectArea = '<select id="' + newId + '" name="' + newId +'" multiple="multiple"  size="3"'+' onchange="handleValueOnselectNonValidValues(this)"' +'>';
										
							var hiddenField = '<input type="hidden" id="ruleSet.rule['+ruleCount+'].condition.column['+columnCount+'].fieldConstraint[0].literalRestriction[0].readableValue"' + ' name="ruleSet.rule['+ruleCount+'].condition.column['+columnCount+'].fieldConstraint[0].literalRestriction[0].readableValue"' +'/>'

				
							//Element.remove(validValueField);

							
							$(spanId).innerHTML = selectArea + hiddenField;

							var sel = $(newId);	
				                
				                    sel.options.length = 0
				                    
				                    
				                    values.each(function(value) {
										
										var tempT='';
											if (value.select != null) {
												tempT='-' + value.select
											}
											
				                        var opt = new Option(value.displayName + tempT, value.displayName)
				                        sel.options.add(opt)
				                    })
				                })
			
			
			}
            else
			{
				//alert ("here");
				authorRule.getValidValues(domainObjectSelectedIndex-1, fieldDropDown.selectedIndex-1, 
				                     		function (html) 
			                   	{


									var newId = validValueField.id; 
									var spanId = newId + '.span';

									var isMultiSelect=getSelect(domainObject, fieldDropDown.selectedIndex-1);
									
									
									var selectArea = '';
									
									
									
									if (isMultiSelect)
									{
										selectArea = '<select id="' + newId + '" name="' + newId +'" multiple="multiple"  size="3"'+' onchange="handleValueOnselect(this,'+ ruleCount +',' + tIndex + ', true)"' +'>';
										
									}
									else
									{
										selectArea = '<select id="' + newId + '" name="' + newId +'" onchange="handleValueOnselect(this,'+ ruleCount +',' + tIndex + ', false)"' +'>';
									}
									
									var hiddenField = '<input type="hidden" id="ruleSet.rule['+ruleCount+'].condition.column['+columnCount+'].fieldConstraint[0].literalRestriction[0].readableValue"' + ' name="ruleSet.rule['+ruleCount+'].condition.column['+columnCount+'].fieldConstraint[0].literalRestriction[0].readableValue"' +'/>'
									selectArea += html + '</select>';

									$(spanId).innerHTML = selectArea + hiddenField;

							   	});
			}
		
		}
		catch(e) 
		{
			alert('Exception');
			alert(e);
		}


	}
	
	function getSelect(domainObject, i) {
	// in IE-7 after adding rule , first time error when getting fields and properties , caught and returning false.
	// need to fix this code . 			 	
				var isMultiSelect=false;
				try {
					if (domainObject.field[i].fieldValue.inputType == 'multiselect')
						{
							isMultiSelect=true;
						}	
				} catch(e) {
						isMultiSelect=false;
				}
				return isMultiSelect;
	}
	
	function getCategoryValue(ruleCount)
	{
				
				// Check whether category exists
				//alert (ruleCount);
				var columns = $('rule-'+(ruleCount + 1)+'-columns');
				
				//alert(columns.childNodes.length);
				
				
				var divNodes = 0;
				
				for(var i=0; i < columns.childNodes.length; i++)
				{
					if (columns.childNodes[i].nodeName == 'DIV')
					{
						divNodes++;
					}
				}
							

				var categoryValueID;
				
				for (var i=0; i < divNodes; i++)
				{
					var columnId = 'ruleSet.rule['+ ruleCount + '].condition.column[' + i + '].fieldConstraint[0].fieldName';
					
					if($(columnId).value == 'category')
					{

						categoryValueID = 'ruleSet.rule['+ ruleCount + '].condition.column[' + i + '].fieldConstraint[0].literalRestriction[0].value';
						break;
					}
				}
				
				//for (var i=0; i< 100000 ; i ++ ) {
					//var a=10;
				//}
				
				//var  ret = $(categoryValueID).value;
				var ret = document.getElementById(categoryValueID).value;
				if (ret == '') {
					ret = 0;
				}
				
				//alert (ret);
				return ret;
	}

	function handleDomainObjectonChange(domainObjectDropDown, ruleCount)
	{
		
		var domainObjectDropDownID = domainObjectDropDown.id;
		
		var prefixID = domainObjectDropDownID.substring(0,domainObjectDropDownID.lastIndexOf("."));
		
		var domainObjectIdentifierID = prefixID + '.identifier'; 
		
		var expressionID = prefixID + '.expression';
		
		var fieldDropDownID = prefixID + '.fieldConstraint[0].fieldName';
		
		var operatorDropDownID = prefixID + '.fieldConstraint[0].literalRestriction[0].evaluator';

		var valueDropDownID = prefixID + '.fieldConstraint[0].literalRestriction[0].value';
		
		var valueDropDownSpanID = prefixID + '.fieldConstraint[0].literalRestriction[0].value.span';
		
		//
		var domainObjectDisplayUri = prefixID + '.displayUri'; 

		
		if (domainObjectDropDown.selectedIndex == 0)
		{
			// Set all the fields to null
							$(domainObjectIdentifierID).value = '';
						
							// Reset expression
							$(expressionID).value='';
							
							// Set the fields
							$(fieldDropDownID).options.length = 0;
							$(fieldDropDownID).options.add(new Option("Please select field",""));
							
							
							// Reset Operator 
							$(operatorDropDownID).options.length = 0;
							$(operatorDropDownID).options.add(new Option("Please select operator",""));
							
							
							// Reset the value selection span

							var selectArea = '<select id="' + valueDropDownID + '" name="' + valueDropDownID +'">';
							
									selectArea += '<option value="">Please select value</option></select>';

							$(valueDropDownSpanID).innerHTML = selectArea;

			return;
		}

		authorRule.getRulesDomainObject(domainObjectDropDown.selectedIndex - 1, '${command.terminology}', function(domainObject)
		                              {
						
							// Set the identifier Value
							$(domainObjectIdentifierID).value = domainObject.identifier;
							
							$(domainObjectDisplayUri).value = domainObject.displayUri;
							
						
							// Reset expression
							$(expressionID).value='';
							
							// Set the fields
							$(fieldDropDownID).options.length = 0;
							$(fieldDropDownID).options.add(new Option("Please select field",""));
							
							domainObject.field.each(function(field){	
									
								if (field.filter == '' || field.filter == '${command.terminology}') { 
									$(fieldDropDownID).options.add(new Option(field.displayUri,field.name));
								}
							})
							
							// Reset Operator 
							$(operatorDropDownID).options.length = 0;
							$(operatorDropDownID).options.add(new Option("Please select operator",""));
							
							
							// Reset the value selection span

							var selectArea = '<select id="' + valueDropDownID + '" name="' + valueDropDownID +'">';
							
									selectArea += '<option value="">Please select value</option></select>';

							$(valueDropDownSpanID).innerHTML = selectArea;
							
		                              });
		
		
		}
	
	function onCategoryChange(category, ruleCount)

	{
		var selectId =   category.id.substring(0,category.id.lastIndexOf(".")); 
		
		var displayUriID = selectId+ '.readableValue';

		
		
		
				              for (var i = 0; i < category.options.length; i++) {
		                    
		                    	if (category.options[ i ].selected) {
		                    		 $(displayUriID).value = category.options[ i ].text;

		                    		break;
		                    	}
		                    	
		                      }
		
		
				// Check whether category exists
				var columns = $('rule-'+(ruleCount + 1)+'-columns');
				
				
				
				var divNodes = 0;
				
				for(var i=0; i < columns.childNodes.length; i++)
				{
					if (columns.childNodes[i].nodeName == 'DIV')
					{
						divNodes++;
					}
				}
							
				var termExists = false;
				var termValueID;
				
				for (var i=0; i < divNodes; i++)
				{
					var columnId = 'ruleSet.rule['+ ruleCount + '].condition.column[' + i + '].fieldConstraint[0].fieldName';
					
					if($(columnId).value == 'term')
					{
						termExists = true;
						termValueID = 'ruleSet.rule['+ ruleCount + '].condition.column[' + i + '].fieldConstraint[0].literalRestriction[0].value';
						break;
					}
				}
				
				if (termExists)
				{
					
					var catVal = category.value;
					if (category.value == ''){
						catVal = 0;
					}
					//alert(catVal);
					
					createAE.getTermsByCategory(catVal, function(terms) {
						        
						     $(termValueID).value='';   

							var sel = $(termValueID);	
				                
				                
				                    sel.options.length = 0
				                    
				                    
				                    terms.each(function(term) {
										var tempT='';
											if (term.select != null) {
												tempT='-' + term.select
											}
											
				                        var opt = new Option(term.term + tempT, term.id)
				                        sel.options.add(opt)
				                    })
				                })
					
					
				}
	}
	
	
</script>
</head>
<body>
<c:forEach var="cat" items="${command.categories}">
  <script>
		categoryObjects2.push('${cat.id}' + '||' + '${cat.name}');
	</script>
</c:forEach>

<c:if test="${not empty ruleFlowSummary}">
	<div class="pane summary" id="rule-summary">
		<div class="row">
			<div class="label">Rule level</div>
			<div class="value">${ruleFlowSummary['Rule level']}</div>
		</div>
		<div class="row">
			<div class="label">Rule set name</div>
			<div class="value">${ruleFlowSummary['Rule set name']}</div>
		</div>
		<c:if test="${ruleFlowSummary['Sponsor'] != null}">
			<div class="row">
				<div class="label">Sponsor</div>
				<div class="value">${ruleFlowSummary['Sponsor'] }</div>
			</div>
		</c:if>
		<c:if test="${ruleFlowSummary['Institution'] != null}">
			<div class="row">
				<div class="label">Institution</div>
				<div class="value">${ruleFlowSummary['Institution'] }</div>
			</div>
		</c:if>
		<c:if test="${ruleFlowSummary['Study'] != null}">
			<div class="row">
				<div class="label">Study</div>
				<div class="value">${ruleFlowSummary['Study'] }</div>
			</div>
		</c:if>
	</div>
</c:if>

<chrome:division>
  <%--<form:form cssClass="standard">--%>
  <tags:tabForm tab="${tab}" flow="${flow}" willSave="false" hideErrorDetails="true">
    <jsp:attribute name="singleFields">
	  <tags:instructions code="3rules" />
      <div class="row">
        <div id="allRules">
          <c:set var="ruleSize" value="${fn:length(command.ruleSet.rule)}" />
          <c:forEach varStatus="ruleStatus" items="${command.ruleSet.rule}">
            <c:set var="ruleCount" value="${ruleStatus.index}" />
            <c:set var="collapsedKey" value="ruleSet.rule[${ruleCount}]" />
            <c:set var="collapsedCheck" value="${!command.errorsForFields[collapsedKey] and ruleSize lt 2}"/>
            <div id="rule-${ruleCount + 1}">
              <chrome:division title="Rule - (${ruleCount + 1})" id="rule-div-${ruleCount + 1 }" collapsable="true" collapsed="${collapsedCheck}" deleteParams="${ruleCount + 1}" enableDelete="true" >
              <div id="rule-condition-action-container-${ruleCount + 1}">
                <div class="row" value="${command.ruleSet.rule[ruleCount]}"
					id="rule-${ruleCount + 1}-columns">
                  <c:forEach varStatus="columnStatus" begin="0"
					items="${command.ruleSet.rule[ruleCount].condition.column}">
                    <c:set var="columnCount" value="${columnStatus.index}" />
                    <div id="rule-${ruleCount}-column-${columnCount}" style="font-weight:bold;"
						class="lineitem one-condition" <c:if test="${command.ruleSet.rule[ruleCount].condition.column[columnCount].markedDelete}"> visibility:hidden</c:if>">
                      <c:choose>
                        <c:when test="${columnCount == 0}">
                          <label for="IF" style="padding-left:9px; margin-right:8px;">If</label>
                          </c:when>
                        <c:otherwise>
                          <label for="AND">And</label>
                        </c:otherwise>
                      </c:choose>
                     <span>
                      <form:select 
						path="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].objectType"
						id="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].objectType"
						onchange="handleDomainObjectonChange(this, ${ruleCount})">
                        <form:option value="">Please select domain object</form:option>
                        <form:options items="${command.ruleUi.condition[0].domainObject}"
							itemLabel="displayUri" itemValue="className" />
                      </form:select>
                      <tags:errors path="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].objectType"/>
                      <!-- set domain-object display-uri to column -->
                      <form:hidden path="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].displayUri" id="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].displayUri" />
                      <form:hidden path="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].identifier" id="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].identifier" />
                      </span> 
                      <form:select 
						path="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].fieldName"
						id="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].fieldName"
						onchange="handleFieldOnchange(this, ${ruleCount}, ${columnCount})">
                        <form:option value="">Please select field</form:option>
                        <c:forEach items="${command.ruleUi.condition[0].domainObject}"
							varStatus="selectedField">
                          <c:set var="selectedIndex" value="${selectedField.index}" />
                          <c:if
								test="${command.ruleSet.rule[ruleCount].condition.column[columnCount].objectType ==
												        		      			command.ruleUi.condition[0].domainObject[selectedIndex].className}">
                            <c:forEach var="f" items="${command.ruleUi.condition[0].domainObject[selectedIndex].field}">
                              <c:if test="${f.filter == '' || f.filter == command.terminology}">
                                <form:option value="${f.name}">${f.displayUri}</form:option>
                              </c:if>
                            </c:forEach>
                          </c:if>
                        </c:forEach>
                      </form:select>
                      <tags:errors path="ruleSet.rule[${ruleCount }].condition.column[${columnCount }].fieldConstraint[0].fieldName"/>
                      <form:hidden path="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].expression" id="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].expression" />
                      <form:hidden path="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].grammerPrefix" id="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].grammerPrefix" />
                      <form:hidden path="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].grammerPostfix" id="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].grammerPostfix" />
                      <form:hidden path="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].displayUri" id="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].displayUri" />
                      <form:hidden path="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].literalRestriction[0].displayUri" id="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].literalRestriction[0].displayUri" />
                      
                      <form:select 
						path="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].literalRestriction[0].evaluator"
						id="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].literalRestriction[0].evaluator"
						onchange="handleOperatorOnchange(this, ${ruleCount})">
                        <form:option value="">Please select operator</form:option>
                        <c:forEach items="${command.ruleUi.condition[0].domainObject}"
							varStatus="selectedDomainObject">
                          <c:set var="domainObjectIndex"
								value="${selectedDomainObject.index}" />
                          <c:if
								test="${command.ruleSet.rule[ruleCount].condition.column[columnCount].objectType ==
												        		      			command.ruleUi.condition[0].domainObject[domainObjectIndex].className}">
                            <c:forEach
									items="${command.ruleUi.condition[0].domainObject[domainObjectIndex].field}"
									varStatus="selectedField">
                              <c:set var="fieldIndex" value="${selectedField.index}" />
                              <c:if
										test="${command.ruleSet.rule[ruleCount].condition.column[columnCount].fieldConstraint[0].fieldName ==
												        		      					command.ruleUi.condition[0].domainObject[domainObjectIndex].field[fieldIndex].name}">
                                <form:options
											items="${command.ruleUi.condition[0].domainObject[domainObjectIndex].field[fieldIndex].operator}"
											itemLabel="displayUri" itemValue="name" />
                              </c:if>
                            </c:forEach>
                          </c:if>
                        </c:forEach>
                      </form:select>
                      <tags:errors path="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].literalRestriction[0].evaluator"/>
                      <span
						id="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].literalRestriction[0].value.span">
                      <c:choose>
                        <c:when
							test='${command.ruleSet.rule[ruleCount].condition.column[columnCount].fieldConstraint[0].fieldName eq "category"}'>
                          <script type="text/javascript">
																	var fieldValue;
																		var readableValue;
																		//createAE.getCategories(3, function(categories) {
																	
																			var newId = 'ruleSet.rule[' + ${ruleCount} + '].condition.column[' + ${columnCount} + '].fieldConstraint[0].literalRestriction[0].value'; 
																			var spanId = newId + '.span';
																	
																		 fieldValue = '${command.ruleSet.rule[ruleCount].condition.column[columnCount].fieldConstraint[0].literalRestriction[0].value[0]}';
																		 readableValue = '${command.ruleSet.rule[ruleCount].condition.column[columnCount].fieldConstraint[0].literalRestriction[0].readableValue}';
																			//alert (readableValue);
																			var selectArea = '<select id="' + newId + '" name="' + newId +  '" value="' + fieldValue + '" onchange="onCategoryChange(this, ${ruleCount})">';
																			selectArea += '</select>';
						
						
													var hiddenField = '<input type="hidden" value = "'+ readableValue +'" id="ruleSet.rule['+${ruleCount}+'].condition.column['+${columnCount}+'].fieldConstraint[0].literalRestriction[0].readableValue"' + ' name="ruleSet.rule['+${ruleCount}+'].condition.column['+${columnCount}+'].fieldConstraint[0].literalRestriction[0].readableValue"' +'/>'

													
																			//Element.remove(validValueField);
																	
																								
																			$(spanId).innerHTML = selectArea + hiddenField;
																	
																			var sel = $(newId);	
																					                
																			sel.options.length = 0
																			sel.options.add(new Option("Any", ""))
																					                    
																			var index = 0;	
																			categoryObjects2.each(function(cat) {
																				var splitted = cat.split("||");
																				var name = splitted[1];
																				var id = splitted[0];
																				var opt = new Option(name, id)
																					    sel.options.add(opt)
																					    index++;
																					    
																					    if (id == fieldValue)
																					    {
																					    	sel.options[index].selected=true;
																					    }
																			})
																			
																			/**
																			categoryObjects.each(function(cat) {
																				 var name = cat.name
																				 if (name.length > 45) name = name.substring(0, 45) + "..."
																					    var opt = new Option(name, cat.id)
																					    sel.options.add(opt)
																					    index++;
																					    
																					    if (cat.id == fieldValue)
																					    {
																					    	sel.options[index].selected=true;
																					    }
																					    
																		      })
																		     */
																		        
																	           //})
																	</script>
                        </c:when>
                        <c:when test='${command.ruleSet.rule[ruleCount].condition.column[columnCount].fieldConstraint[0].fieldName eq "term"}'>
                          <script type="text/javascript">
												// force 1sec delay for ajax to make sure categories are loaded.. this is just  TEMP FIX 
											//   setTimeout("loadTermsBasedOnCategory()",1000);	

												loadTermsBasedOnCategory()
												function loadTermsBasedOnCategory() {

																		var newId = 'ruleSet.rule[' + ${ruleCount} + '].condition.column[' + ${columnCount} + '].fieldConstraint[0].literalRestriction[0].value'; 
																		var spanId = newId + '.span';
																		var fieldValue = '${command.ruleSet.rule[ruleCount].condition.column[columnCount].fieldConstraint[0].literalRestriction[0].value}';
																		var readableValue = '${command.ruleSet.rule[ruleCount].condition.column[columnCount].fieldConstraint[0].literalRestriction[0].readableValue}';
																		
																		$(spanId).innerHTML="";
																		
																		// Check whether category exists
																		
																		var categoryValue = getCategoryValue(${ruleCount});
																	//	alert (categoryValue);
																	
																		createAE.getTermsByCategory(categoryValue, function(terms) {
						                   

			
																var selectArea = '<select id="' + newId + '" name="' + newId +'" multiple="multiple"  size="3"'+' onchange="handleValueOnselectNonValidValues(this)"' +'>';
								
																var hiddenField = '<input type="hidden" value = "'+ readableValue +'" id="ruleSet.rule['+${ruleCount} +'].condition.column['+${columnCount}+'].fieldConstraint[0].literalRestriction[0].readableValue"' + ' name="ruleSet.rule['+${ruleCount}+'].condition.column['+${columnCount}+'].fieldConstraint[0].literalRestriction[0].readableValue"' +'/>'

						
							
																				$(spanId).innerHTML = selectArea+hiddenField;

																				var sel = $(newId);	
				                
														                    sel.options.length = 0
				                    
				                    											var index = 0;	
															                    terms.each(function(term) {
																				var tempT='';
																					if (term.select != null) {
																						tempT='-' + term.select
																					}
													                        		var opt = new Option(term.term + tempT, term.id)
				                    									    			sel.options.add(opt)
				                    									    		   
				                    									    		    		if (fieldValue.indexOf(term.id) != -1)
																					    		{
																					    			sel.options[index].selected=true;
																					    		}
																					    	index++;
																					    
				                    											})
				                										})
					
	
	
												}											

															
							</script>
                        </c:when>
                        <c:when
							test='${command.ruleSet.rule[ruleCount].condition.column[columnCount].fieldConstraint[0].fieldName eq "investigationalNewDrugIndicator" or
							command.ruleSet.rule[ruleCount].condition.column[columnCount].fieldConstraint[0].fieldName eq "investigationalNewDeviceIndicator"}'>
                          <script type="text/javascript">

														loadOrgs();
												function loadOrgs() {
	
																		var newId = 'ruleSet.rule[' + ${ruleCount} + '].condition.column[' + ${columnCount} + '].fieldConstraint[0].literalRestriction[0].value'; 
																		var hiddenId = 'ruleSet.rule[' + ${ruleCount} + '].condition.column[' + ${columnCount} + '].fieldConstraint[0].literalRestriction[0].readableValue'; 
																		var spanId = newId + '.span';
																		var fieldValue = '${command.ruleSet.rule[ruleCount].condition.column[columnCount].fieldConstraint[0].literalRestriction[0].value[0]}';
																	
																	
																	
																	var inputArea = '<input type="text" id="' + newId + '" name="' + newId +'" value = "'+ fieldValue + '" size="35" class="autocomplete"/>';
																	inputArea += '<img alt="activity indicator" src="<c:url value="/images/indicator.white.gif" />" class="indicator" id="ind-indicator"/>';
																	
																	var hiddenArea = '<input type="hidden" id="' + hiddenId + '" name="' + hiddenId +'" value = "'+ fieldValue + '" size="35" />';
																	
																	$(spanId).innerHTML = inputArea + '<div id="' + newId + '-choices' + '" class="autocomplete"></div>' + hiddenArea;

	

																	new Autocompleter.DWR(newId, newId + '-choices',
                														orgsPopulator, {
                														valueSelector: orgValueSelector,
                															afterUpdateElement: function(inputElement, selectedElement, selectedChoice) {
																					document.getElementById(hiddenId).value = orgValueSelector(selectedChoice);
                																},
                																indicator: "ind-indicator",
                																minChars : AE.autocompleterChars , 
                																frequency : AE.autocompleterDelay
                																});
	
																				}											

															
							</script>
                        </c:when>

                        <c:when
							test='${command.ruleSet.rule[ruleCount].condition.column[columnCount].fieldConstraint[0].fieldName eq "meddraCode"}'>
                          <script type="text/javascript">

										var newId = 'ruleSet.rule[' + ${ruleCount} + '].condition.column[' + ${columnCount} + '].fieldConstraint[0].literalRestriction[0].value'; 
										var hiddenId = 'ruleSet.rule[' + ${ruleCount} + '].condition.column[' + ${columnCount} + '].fieldConstraint[0].literalRestriction[0].readableValue'; 
										var spanId = newId + '.span';
										
										var fieldValue = '';
											<c:forEach items="${command.ruleSet.rule[ruleCount].condition.column[columnCount].fieldConstraint[0].literalRestriction[0].value}"
												var="val">
												fieldValue = fieldValue + ',' + '${val}';
											</c:forEach>
										
																	
										var inputArea = '<textarea id="' + newId + '" name="' + newId +'" >'+ fieldValue.replace(/\,/,'') + '</textarea>';
										inputArea += '<img alt="activity indicator" src="<c:url value="/images/indicator.white.gif" />" class="indicator" id="ind-indicator"/>';
					
										var hiddenArea = '<input type="hidden" id="' + hiddenId + '" name="' + hiddenId +'" cols=40 rows=8/>';
				
										$(spanId).innerHTML = inputArea + '<div id="' + newId + '-choices' + '"></div>' + hiddenArea;

							</script>
                        </c:when>

                        <c:when
                                test='${command.ruleSet.rule[ruleCount].condition.column[columnCount].fieldConstraint[0].fieldName eq "significanceLevel"}'>
                            <script type="text/javascript">

                                var newId = 'ruleSet.rule[' + ${ruleCount} + '].condition.column[' + ${columnCount} + '].fieldConstraint[0].literalRestriction[0].value';
                                var hiddenId = 'ruleSet.rule[' + ${ruleCount} + '].condition.column[' + ${columnCount} + '].fieldConstraint[0].literalRestriction[0].readableValue';
                                var spanId = newId + '.span';

                                var fieldValue = '';
                                <c:forEach items="${command.ruleSet.rule[ruleCount].condition.column[columnCount].fieldConstraint[0].literalRestriction[0].value}"
                                                        var="val">
                                fieldValue =  '${val}';
                                </c:forEach>


                                var inputArea = '<input id="' + newId + '" name="' + newId +'" value="'+ fieldValue.replace(/\,/,'') + '" />';
                                inputArea += '<img alt="activity indicator" src="<c:url value="/images/indicator.white.gif" />" class="indicator" id="ind-indicator"/>';

                                var hiddenArea = '<input type="hidden" id="' + hiddenId + '" name="' + hiddenId +'" value="' + fieldValue + '" cols=40 rows=8/>';

                                $(spanId).innerHTML = inputArea + '<div id="' + newId + '-choices' + '"></div>' + hiddenArea;

                            </script>
                        </c:when>

                        <c:when
							test='${command.ruleSet.rule[ruleCount].condition.column[columnCount].fieldConstraint[0].fieldName eq "reportDefinitionName" || command.ruleSet.rule[ruleCount].condition.column[columnCount].fieldConstraint[0].fieldName eq "treatmentAssignmentCode"}'>
                          <script type="text/javascript">
							
					var criteria = '${command.ruleSet.rule[ruleCount].condition.column[columnCount].fieldConstraint[0].fieldName}' ;
					var criteria1 = "" ;
					
					if (criteria == 'reportDefinitionName') { 
						criteria1 = '${command.caaersRuleSet.organization.id}';
					}
					
					if (criteria == 'treatmentAssignmentCode') { 
						criteria1 = '${command.caaersRuleSet.study.id}';
					}
					

						authorRule.getAjaxObjects(criteria, criteria1, function(values) {
						                   

								var newId = 'ruleSet.rule[' + ${ruleCount} + '].condition.column[' + ${columnCount} + '].fieldConstraint[0].literalRestriction[0].value'; 
							
								var spanId = newId + '.span';
								var fieldValue = '${command.ruleSet.rule[ruleCount].condition.column[columnCount].fieldConstraint[0].literalRestriction[0].value}';
								var readableValue = '${command.ruleSet.rule[ruleCount].condition.column[columnCount].fieldConstraint[0].literalRestriction[0].readableValue}';


										
						var selectArea = '<select id="' + newId + '" name="' + newId +'" multiple="multiple"  size="3"'+' onchange="handleValueOnselectNonValidValues(this)"' +'>';
						var hiddenField = '<input type="hidden" value = "'+ readableValue +'" id="ruleSet.rule['+${ruleCount} +'].condition.column['+${columnCount}+'].fieldConstraint[0].literalRestriction[0].readableValue"' + ' name="ruleSet.rule['+${ruleCount}+'].condition.column['+${columnCount}+'].fieldConstraint[0].literalRestriction[0].readableValue"' +'/>'
								
						
				
							//Element.remove(validValueField);

							
							$(spanId).innerHTML = selectArea + hiddenField;

							var sel = $(newId);	
				                
				                    sel.options.length = 0
				                    var index = 0;	
				                    
				                    values.each(function(value) {
										
										var tempT='';
											if (value.select != null) {
												tempT='-' + value.select
											}
											
				                        var opt = new Option(value.displayName + tempT, value.displayName)
				                        sel.options.add(opt);
				                        if (fieldValue.indexOf(value.displayName) != -1)
														{
													
													sel.options[index].selected=true;
													}
													index++;
				                    })
				                })
											

				                    											
				                    											
															
							</script>
                        </c:when>
                        <c:otherwise>
                          <c:choose>
                            <c:when
									test="${empty command.ruleSet.rule[ruleCount].condition.column[columnCount].objectType ||
																		                empty command.ruleSet.rule[ruleCount].condition.column[columnCount].fieldConstraint[0].fieldName}">
                              <form:select 
										path="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].literalRestriction[0].value"
										id="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].literalRestriction[0].value"
										multiple="false">
                                <form:option value="">Please select value</form:option>
                              </form:select>
                              <tags:errors path="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].literalRestriction[0].value" />
                            </c:when>
                            <c:otherwise>
                              <c:forEach items="${command.ruleUi.condition[0].domainObject}"
										varStatus="selectedDomainObject">
                                <c:set var="domainObjectIndex"
											value="${selectedDomainObject.index}" />
                                <c:if
											test="${command.ruleSet.rule[ruleCount].condition.column[columnCount].objectType ==
												        		      						command.ruleUi.condition[0].domainObject[domainObjectIndex].className}">
                                  <c:forEach
												items="${command.ruleUi.condition[0].domainObject[domainObjectIndex].field}"
												varStatus="selectedField">
                                    <c:set var="fieldIndex" value="${selectedField.index}" />
                                    <c:if
													test="${command.ruleSet.rule[ruleCount].condition.column[columnCount].fieldConstraint[0].fieldName ==
												        		      							command.ruleUi.condition[0].domainObject[domainObjectIndex].field[fieldIndex].name}">
                                      <c:choose>
                                        <c:when
															test='${command.ruleUi.condition[0].domainObject[domainObjectIndex].field[fieldIndex].fieldValue.inputType == "multiselect"}'>
                                          <form:select 
																path="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].literalRestriction[0].value"
																id="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].literalRestriction[0].value"
																multiple="multiple" size="3"
																onchange="handleValueOnselect(this, ${ruleCount}, ${fieldIndex}, true)">
                                            <form:options
																	items="${command.ruleUi.condition[0].domainObject[domainObjectIndex].field[fieldIndex].validValue}"
																	itemLabel="displayUri" itemValue="value" />
                                          </form:select>
                                          <tags:errors path="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].literalRestriction[0].value"/>
                                          <form:hidden path="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].literalRestriction[0].readableValue" id="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].literalRestriction[0].readableValue" />
                                        </c:when>
                                        <c:otherwise>
                                          <form:select 
																path="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].literalRestriction[0].value"
																id="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].literalRestriction[0].value"
																multiple="false"
																onchange="handleValueOnselect(this, ${ruleCount}, ${fieldIndex}, false)">
                                            <form:option value="">Please select value</form:option>
                                            <form:options
																	items="${command.ruleUi.condition[0].domainObject[domainObjectIndex].field[fieldIndex].validValue}"
																	itemLabel="displayUri" itemValue="value" />
                                          </form:select>
                                          <tags:errors path="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].literalRestriction[0].value"/>
                                          <form:hidden path="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].literalRestriction[0].readableValue" id="ruleSet.rule[${ruleCount}].condition.column[${columnCount}].fieldConstraint[0].literalRestriction[0].readableValue" />
                                        </c:otherwise>
                                      </c:choose>
                                    </c:if>
                                  </c:forEach>
                                </c:if>
                              </c:forEach>
                            </c:otherwise>
                          </c:choose>
                        </c:otherwise>
                      </c:choose>
                      </span>
                      <c:if
						test="${columnCount > 0}"> <a href="javascript:removeCondition(${ruleCount}, ${columnCount})"> <img id="remove-column-${ruleCount}"
							src="<c:url value="/images/rule/remove_condition.gif" />" align="absmiddle"
							style="cursor:hand;  border:0px" /> </a> </c:if>
                    </div>
                    
                  </c:forEach>
                </div>
				<div class="new_condition">
				<tags:button id="add-condition-${ruleCount }" color="blue" type="button" value="Add Condition" size="small" icon="add" onclick="fetchCondition(${ruleCount })"/>
                </div>
				<div class="row">
                  <div class="lineitem" style="margin-bottom:5px;">
                    <label for="action" class="label" style="font-weight:bold;">Actions</label>
                  </div>
                  <div id="action-template" class="lineitem">
                      <c:choose>
                          <c:when test='${command.caaersRuleSet.ruleType.name == "Mandatory Sections Rules"}'>
                            <form:select cssStyle="width: 300px;" path="ruleSet.rule[${ruleCount}].action" id="ruleSet.rule[${ruleCount}].action" multiple="multiple" size="3">
                                <c:forEach var="reportSectionName" items="${command.reportSectionNames}">
                                    <form:option value="${reportSectionName}">${reportSectionName.displayName}</form:option>
                                </c:forEach>
                            </form:select>
                          </c:when>
                          <c:when test='${command.caaersRuleSet.ruleType.name == "SAE Reporting Rules"}'>
                              <form:select cssStyle="width: 300px;" path="ruleSet.rule[${ruleCount}].action" id="ruleSet.rule[${ruleCount}].action" multiple="multiple" size="3">
                              <c:forEach var="reportDefinition" items="${command.reportDefinitions}">
                               <form:option value="${reportDefinition.name}">${reportDefinition.name}</form:option>
                              </c:forEach>
                              <form:option value="IGNORE">No Report Required (Study Level Exception Rule)</form:option>
                              </form:select>
                        </c:when>
                        <c:when test="${command.caaersRuleSet.ruleType.name eq 'Field Rules'}">
                            <form:select cssStyle="width: 300px;" path="ruleSet.rule[${ruleCount}].action" id="ruleSet.rule[${ruleCount}].action"  size="1">
                                <c:forEach var="mandatoryness" items="${command.mandatoryOptions}">
                                <form:option value="${mandatoryness.name}">${mandatoryness.displayName}</form:option>
                              </c:forEach>
                            </form:select>
                        </c:when>
                        <c:when test="${command.caaersRuleSet.ruleType.name eq 'Safety Signalling Rules'}">
                            <form:select cssStyle="width: 300px;" path="ruleSet.rule[${ruleCount}].action" id="ruleSet.rule[${ruleCount}].action" multiple="multiple" size="3">
                                <c:forEach var="nfOption" items="${command.notificationOptions}">
                                    <form:option value="${nfOption}">${nfOption.displayName}</form:option>
                                </c:forEach>
                            </form:select>
                         </c:when>

                      </c:choose>

                    <tags:errors path="ruleSet.rule[${ruleCount}].action"/>
                  </div>
                 

                </div>
              </div>
              </chrome:division>
            </div>
          </c:forEach>
        </div>
        <!-- closing allRules -->
        <tags:errors path="ruleSet.rule" />
        <div class="new_rule">
            <tags:button id="add-rule" color="blue" type="button" value="Add Rule" size="small" icon="add" onclick="addRule()"/>
        </div>
        
      </div>
    </jsp:attribute>
    
  </tags:tabForm>
</chrome:division>
</body>
</html>
