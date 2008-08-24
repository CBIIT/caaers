<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="chrome" tagdir="/WEB-INF/tags/chrome"%>
<%@attribute name="isAjaxable" type="java.lang.Boolean" description="Should be set to true, if this tag is included in the response of an AJAX call, this ensures that the javascript objects defined here are properly enabled" %>
<%@attribute name="isMeddra" required="true" type="java.lang.Boolean" description="Will tell whether the autocompleter should look for MedDRA or CTC" %>
<%@attribute name="version" required="true" type="java.lang.Integer" description="Will tell the version of ctc or meddra to use" %>
<%@attribute name="instructions" fragment="true" %>
<%@attribute name="callbackFunctionName" required="true" description="The call back function in the parent page, that will be invoked with the selected terms"%>
<%@attribute name="ignoreOtherSpecify" type="java.lang.Boolean" description="Must be true if we need to ignore other specify" %>
<%@attribute name="localButtons" fragment="true" description="Extra content to be display in the control area, by default an Add Terms button will be displayed"%>
<%@attribute name="title" required="false" %> 
<tags:dwrJavascriptLink objects="createAE"/>
<script type="text/javascript">
  
 	//This object will store the reference of the Window, will also contains function
 	//that will be called from the loaded page, that has to interact with the parent page(opener page)
 	
 	//Note:- for some reason, the javascript embeded inside javascript tag in the child page (the page loaded in the linline popup window via AJAX)
 	//is not properly executing, so I modified it to work based on inline-hidden div.
 	var catSel = null;
 	var CategorySelector = Class.create();
 	Object.extend(CategorySelector.prototype, {
		initialize: function(meddra, ver, ignoreOtherSpecify) {
			this.win = null;
			this.isMeddra = meddra;
			this.version = ver;
			this.ignoreOtherSpecify = ignoreOtherSpecify;
		},
		
		showWindow:function(wUrl, wTitle, wWidth, wHeight){
			win = new Window({className:"alphacube", destroyOnClose:true,title:wTitle,  width:wWidth,  height:wHeight, 
			onShow:this.show.bind(this),
			onBeforeShow:this.beforeShow.bind(this)
			});
			this.win = win;
			win.setContent('chooseCategory');
			win.show(true);
			
		},
		initializeAutoCompleter: function() {

			AE.createStandardAutocompleter('termCode', 
            		function(autocompleter, text){
            			if(this.options.categorySelector.isMeddra){
            				createAE.matchLowLevelTermsByCode(this.options.categorySelector.version,text, function(values) {
            					if(catSel.ignoreOtherSpecify){
                    				var vals = [];
                    				values.each(function(aterm){
                        				if(aterm.fullName.indexOf('Other (Specify') < 0){
                        					 vals.push(aterm);
                    					}
                        			});
                    				autocompleter.setChoices(vals);
                				}else{
                					autocompleter.setChoices(values);
                    			}								
							});
            			}else{
            				createAE.matchTerms(text, this.options.categorySelector.version, '', 25 , function(values){
                				if(catSel.ignoreOtherSpecify){
                    				var vals = [];
                    				values.each(function(aterm){
                        				if(aterm.fullName.indexOf('Other (Specify') < 0){
                        					 vals.push(aterm);
                    					}
                        			});
                    				autocompleter.setChoices(vals);
                				}else{
                					autocompleter.setChoices(values);
                    			}
            				});
            			}
            		},
            		function(aterm) {
            			return aterm.fullName;
            		}, 
            		{categorySelector:this}
            	);
		},
		finishSingleTermSelection:function(){
			var selTermMap = new Hash();
			var termElement = $('termCode');
			var termElementInput = $('termCode-input');
			
			var termId = termElement.getValue();
			if(termId) selTermMap.set(termId, termElementInput.getValue());
			
			termElement.clear();
			termElementInput.clear();
			${callbackFunctionName}(selTermMap); //need to refactor, this is a rude way of calling a function
			
		},
		finishMultiTermsSelection:function() {
			var terms = $('terms');
			var categories = $('categories');
			
			var opts = terms.options;
			
			var selTermMap = new Hash();
			//each over iterator is not working, dont know why.
			if(opts.length > 0) {
				for(i = 0; i< opts.length; i++){
					if(opts[i].selected) selTermMap.set(opts[i].value, opts[i].title);
				}
			}
			Windows.close(this.win.getId());
			//reset the category and terms
			terms.options.length=0;
			categories.selectedIndex = -1;
			//call the call back
			${callbackFunctionName}(selTermMap); //need to refactor, this is a rude way of calling a function
		},
		cancelTermsSelection:function(){
			Windows.close(this.win.getId());
			//reset the category and terms
			terms.options.length=0;
			categories.selectedIndex = -1;
		},
		beforeShow : function(){
			
		},
		show: function(){
			
		},
		showTerms: function(el, ignoreOtherSpecify){
			catIds = $(el).getValue();
			var terms = $('terms');
			terms.options.length=0;
			
			/* BiJu : Optimize this to make single call instead of multiple */
			
			catIds.each(function(catId){
				 createAE.getTermsByCategory(catId, function(ctcTerms) {
				 	ctcTerms.each(function(ctcTerm) {
				 		if(!(ignoreOtherSpecify && ctcTerm.fullName.indexOf('Other (Specify')  > 0) ){
				 		
				 		  var ctcFullName = (ctcTerm.fullName.length > 70 ? ctcTerm.fullName.substring(0,70)+"..." :ctcTerm.fullName );
                       		var opt = new Option(ctcFullName , ctcTerm.id)
                       		opt.title=ctcTerm.fullName;
                       		terms.options.add(opt);
				 		}
                   })
				 });		
			});
			
			
		},
		showCategoryBox:function(){
	 		this.showWindow('<c:url value="/pages/selectCTCTerms" />', '${title}', 800, 380 );
	 	},
	 	onDivScroll:function(selectBoxId)	{
			if(basename == '') return ;
			
	 		var selectBox = $(selectBoxId);
	 	    //a) On horizontal scrolling: To avoid vertical
	 	    //b) On vertical scrolling: To view all the items in selectbox
	 	    if (selectBox.options.length > 5)  	
	 	 	    selectBox.size=selectBox.options.length;
	 	    else 	
	 	 	    selectBox.size=5;
	 	},
		
	 	onSelectFocus:function(selectBoxId)	{
	 	 	var outerDiv = $(selectBoxId + '-div-id');
	 	 	var selectBox = $(selectBoxId);
	 	 	
	 	    //adjust the scrolling position, so that the content (with less length) is visible

	 	    if (outerDiv.scrollLeft != 0)   outerDiv.scrollLeft = 0;
	 	   //Adjust the selected item, so that on pressing of downarrow key or uparrowkey,the selected item should also scroll up or down as expected.
			if(selectBox.options.length > 5){
				selectBox.focus();
				selectBox.size = 5;
			}
	 	   
	 	    
	 	}
	 	
		
		
 	});
	
	function initalizeCategorySelector(){
		//some pages add this tag, via ajax, so they are supposed to
		//call this function directly.
		catSel = new CategorySelector(${isMeddra}, ${version}, ${ignoreOtherSpecify});
	 	catSel.initializeAutoCompleter();
	 	//capture events on the buttons
	 	Element.observe('addSingleTermBtn', 'click', function(){
		 	this.finishSingleTermSelection();
	 	}.bind(catSel));
	 	Element.observe('addMultiTermBtn', 'click', function(){
		 	this.showCategoryBox()
	 	}.bind(catSel));
		Element.observe('addTermsBtn','click',function(){
			this.finishMultiTermsSelection();
		}.bind(catSel));
		
	 	//capture the events on the divisions for scroll correction.
	    Element.observe('categories-div-id','scroll', function(){
			this.OnDivScroll('categories');
	    }.bind(catSel));
	    Element.observe('categories','change',function(){
	    	this.showTerms('categories', catSel.ignoreOtherSpecify);
		}.bind(catSel));
	    Element.observe('categories','focus',function(){
	    	this.onSelectFocus('categories');
	    }.bind(catSel));
		Element.observe('terms-div-id','scroll', function(){
			  this.OnDivScroll('terms');
		}.bind(catSel));
		Element.observe('terms','focus',function(){
			  this.onSelectFocus('terms');
		}.bind(catSel));
	}
 	
 	Element.observe(window, "load", function() {
 		initalizeCategorySelector();	
 	 
 	});
		
 	<c:if test="${isAjaxable}">
 		initalizeCategorySelector.defer();
 	</c:if>

</script>

<chrome:box title="Find &amp; Add Adverse Event (AE) Term(s)">
 	<c:if test="${not empty instructions}"><p class="instructions"><jsp:invoke fragment="instructions"/></p></c:if>
    <c:if test="${empty instructions}"><p class="instructions">To enter AE terms individually, begin to enter the AE term below, select the appropriate term, and click "Add".</p></c:if>
 		<table id="fnd-0" class="query">
  			<tbody>
  				<tr>
  					<td class="one">
  						<div>
  							<tags:autocompleter displayName="abcd" propertyName="termCode" size="30" initialDisplayValue="Begin typing here"/><input id="addSingleTermBtn" type="button" value="Add"  />
  						</div>
  						<div class="local-buttons">
  							
  						</div>
  					</td>
  					<c:if test="${not isMeddra}">
  					<td class="two">OR</td>
  					<td class="three"><input type="button" value="Add Multiple" id="addMultiTermBtn" />
  					 &nbsp;&nbsp;&nbsp;&nbsp;click on "Add Multiple" to add several AE terms at once.
  					</td>
  					</c:if>
  					
  				</tr>
  			</tbody>
  		</table>
  	</chrome:box>
  	<!-- the hidden window for category popup -->
  	<div style="display:none">
	<c:if test="${not isMeddra}">
	<div id="chooseCategory">
  	<chrome:box title="Choose CTC term(s):" autopad="true">
		<tags:renderRow>
			<jsp:attribute name="label">CTC category(s)</jsp:attribute>
			<jsp:attribute name="value">
			  <div id="categories-div-id" class="categories-div" >
			    <select name="categories" id="categories" size="5" class="categories" multiple >
				  <c:forEach var="cat" items="${command.ctcCategories}">
					<option value="${cat.id}">${cat.name}</option>
				  </c:forEach>
			    </select>
			  </div>
			</jsp:attribute>
		</tags:renderRow>
		<tags:renderRow>
			<jsp:attribute name="label">CTC terms(s)</jsp:attribute>
			<jsp:attribute name="value">
				<div id="terms-div-id" class="terms-div">
				  <select name="terms" id="terms" size="5" class="terms" multiple >
					<option value="" > Please select a CTC term first </option>
				  </select>
				</div>
			</jsp:attribute>
		</tags:renderRow>
		<hr />
		<div class="aeTermQuery-buttons">
			<c:if test="${empty localButtons}">
			<input id="addTermsBtn" type="button" value="Add Terms" />
			</c:if>
			 <jsp:invoke fragment="localButtons"/>
		</div>		
	</chrome:box>
	</div>
	</c:if>
	<c:if test="${isMeddra}">
		<p>Addition of multiple terms is only supported for CTC terminology</p>
	</c:if>
  	
	</div>