package gov.nih.nci.cabig.caaers.web;

import java.util.ArrayList;
import java.util.List;

public class ListValues {

	private String code;

	private String desc;
	
	public ListValues() {
		// TODO Auto-generated constructor stub
	}

	ListValues(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public List<ListValues> getStudySearchType() {
		List<ListValues> col = new ArrayList<ListValues>();
		ListValues lov1 = new ListValues("st", "Short Title");
		ListValues lov2 = new ListValues("lt", "Long Title");
		ListValues lov3 = new ListValues("idtf", "Identifier");
		//ListValues lov4 = new ListValues("d", "Description");
		//ListValues lov5 = new ListValues("psc", "Primary Sponsor Code");
		//ListValues lov6 = new ListValues("pc", "Phase Code");

		col.add(lov1);
		col.add(lov2);
		col.add(lov3);

		return col;
	}
	
	public List<ListValues> getParticipantSearchType(){
		List<ListValues> col = new ArrayList<ListValues>();
		ListValues lov1 = new ListValues("fn", "First Name");
		ListValues lov2 = new ListValues("ln", "Last Name");
		ListValues lov3 = new ListValues("g" , "gender");
		ListValues lov4 = new ListValues("r" , "race" );
		ListValues lov5 = new ListValues("e" , "ethnicity");
		col.add(lov1);
    	col.add(lov2);
    	col.add(lov3);
    	col.add(lov4);
    	col.add(lov5);
    	return col;
	}
	
	public List<ListValues> getParticipantGender(){
		List<ListValues> col = new ArrayList<ListValues>();
		ListValues lov1 = new ListValues("---", "---");
		ListValues lov2 = new ListValues("Male", "Male");
		ListValues lov3 = new ListValues("Female", "Female");
		ListValues lov4 = new ListValues("Not Reported" , "Not Reported");
		ListValues lov5 = new ListValues("Unknown" , "Unknown" );
		col.add(lov1);
    	col.add(lov2);
    	col.add(lov3);
    	col.add(lov4);
    	col.add(lov5);
    	return col;
	}
	
	public List<ListValues> getParticipantEthnicity(){
		List<ListValues> col = new ArrayList<ListValues>();
		ListValues lov1 = new ListValues("---", "---");
		ListValues lov2 = new ListValues("Hispanic or Latino", "Hispanic or Latino");
		ListValues lov3 = new ListValues("Non Hispanic or Latino", "Non Hispanic or Latino");
		ListValues lov4 = new ListValues("Not Reported" , "Not Reported");
		ListValues lov5 = new ListValues("Unknown" , "Unknown" );
		col.add(lov1);
    	col.add(lov2);
    	col.add(lov3);
    	col.add(lov4);
    	col.add(lov5);
    	return col;
	}
	
	public List<ListValues> getParticipantRace(){
		List<ListValues> col = new ArrayList<ListValues>();
		ListValues lov1 = new ListValues("---", "---");
		ListValues lov2 = new ListValues("Asian", "Asian");
		ListValues lov3 = new ListValues("White", "White");
		ListValues lov4 = new ListValues("Black or African American" , "Black or African American");
		ListValues lov5 = new ListValues("American Indian or Alaska Native" , "American Indian or Alaska Native" );
		ListValues lov6 = new ListValues("Native Hawaiian or other Pacific Islander" , "Native Hawaiian or other Pacific Islander" );
		ListValues lov7 = new ListValues("Not Reported" , "Not Reported" );
		ListValues lov8 = new ListValues("Unknown" , "Unknown" );
		col.add(lov1);
    	col.add(lov2);
    	col.add(lov3);
    	col.add(lov4);
    	col.add(lov5);
    	col.add(lov6);
    	col.add(lov7);
    	col.add(lov8);
    	return col;
	}
	
	public List<ListValues> getParticipantIdentifierSource(){
		List<ListValues> col = new ArrayList<ListValues>();
		ListValues lov1 = new ListValues("Duke University Comprehensive Cancer Center", "Duke University Comprehensive Cancer Center");
		ListValues lov2 = new ListValues("Warren Grant Magnuson Clinical Center", "Warren Grant Magnuson Clinical Center");
		col.add(lov1);
    	col.add(lov2);
    	return col;
	}

}
