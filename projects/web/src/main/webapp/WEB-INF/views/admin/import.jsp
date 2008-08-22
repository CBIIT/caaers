<%@ include file="/WEB-INF/views/taglibs.jsp"%>

<html>
<head>
<tags:includeScriptaculous/>
	<script language="JavaScript" type="text/JavaScript">
	
	function toggle(in_id,out_id,out_second_id,out_third_id,out_fourth_id)
	{
		
		if ($(out_id).style.display != "none" ){
			Effect.Shrink(out_id);
		}
		if ($(out_second_id).style.display != "none" ){
			Effect.Shrink(out_second_id);
		}
		if ($(out_third_id).style.display != "none" ){
			Effect.Shrink(out_third_id);
		}	
		if ($(out_fourth_id).style.display != "none" ){
			Effect.Shrink(out_fourth_id);
		}			
		Effect.toggle(in_id, 'appear');
		$('type').value = in_id;
	}
	
	function showIf()
	{
		if ( $('type').value == "study" ){
			$('study').style.display = ""
				
		}
		if (  $('type').value == "participant"){
			$('participant').style.display = ""
		}
		if (  $('type').value == "routineAeReports"){
			$('routineAeReport').style.display = ""
		}
		if (  $('type').value == "investigator"){
			$('investigator').style.display = ""
		}	
		if (  $('type').value == "researchStaff"){
			$('researchStaff').style.display = ""
		}			
	}
	
	
	Event.observe(window, "load", function() {
		
		showIf()
		Event.observe("study_button","click",function() { toggle('study','participant','routineAeReport','investigator','researchStaff') })
		Event.observe("participant_button","click",function() { toggle('participant','study','routineAeReport','investigator','researchStaff') })
		Event.observe("routine_ae_report_button","click",function() { toggle('routineAeReport','study','participant','investigator','researchStaff') })
	 	Event.observe("investigator_button","click",function() { toggle('investigator','study','participant','routineAeReport','researchStaff') })
	 	Event.observe("research_staff_button","click",function() { toggle('researchStaff','investigator','study','participant','routineAeReport') })
	 	
		       
    	}           
    )

</script>

</head>
<body>
<p id="instructions">
        Import Studies/Protocols or Subjects into caAERS
</p>
    <tags:tabForm tab="${tab}" flow="${flow}" formName="ImportForm" enctype="multipart/form-data">
        <jsp:attribute name="singleFields">
	<div>		
		<input type="hidden" name="_action" value="">
		<input type="hidden" name="_selected" value="">
	</div>
	
		<input type="button" id="study_button" value="Import Study / Protocol"><br><br>
		<input type="button" id="participant_button" value="Import Subject         "><br><br>
		<input type="button" id="routine_ae_report_button" value="Import Routine AEs         "><br><br>
		<input type="button" id="investigator_button" value="Import Investigator         "><br><br>
		<input type="button" id="research_staff_button" value="Import Research Staff         ">
		
		 <tags:errors path="*"/>
    	 
		<div style="display: none;position:relative;top:-30px; left:200px;" id = "study">
    	<input id="study_file" type="file" name="studyFile" />
		<input id="type" type="hidden" name="type" />
		</div>
		
		<div style="display: none;position:relative;top:-30px; left:200px;" id = "participant"> 
    	<input id="participant_file" type="file" name="participantFile" />
		<input id="type" type="hidden" name="type" />
		</div>
		
		<div style="display: none;position:relative;top:-30px; left:200px;" id = "routineAeReport"> 
    	<input id="routine_ae_report_file" type="file" name="routineAdverseEventReportFile" />
		<input id="type" type="hidden" name="type" />
		</div>

		<div style="display: none;position:relative;top:-30px; left:200px;" id = "investigator"> 
    	<input id="investigator_file" type="file" name="investigatorFile" />
		<input id="type" type="hidden" name="type" />
		</div>    	

		<div style="display: none;position:relative;top:-30px; left:200px;" id = "researchStaff"> 
    	<input id="research_staff_file" type="file" name="researchStaffFile" />
		<input id="type" type="hidden" name="type" />
		</div> 
		
 </jsp:attribute>
    </tags:tabForm>    
</body>
</html>