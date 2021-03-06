
LOAD DATA
	INFILE 'course_agents.txt'
	DISCARDMAX 9999
	TRUNCATE INTO TABLE course_agents
	fields terminated by '\t'
	
	trailing NULLCOLS
	(
		ID					    INTEGER EXTERNAL(10),
		GRID_ID					    CHAR(2000),
		VERSION				    INTEGER EXTERNAL(10),
		TREATMENT_ID				    INTEGER EXTERNAL(10),
		LIST_INDEX				    INTEGER EXTERNAL(10),
		STUDY_AGENT_ID 				    INTEGER EXTERNAL(10),
		ADMINISTRATION_DELAY_MINUTES			    INTEGER EXTERNAL,
		DOSE_AMOUNT					    CHAR(2000),
		DOSE_UNITS					    CHAR(2000),
		DOSE_ROUTE					    CHAR(2000),
		TOTAL_DOSE_THIS_COURSE 			    INTEGER EXTERNAL,
		DURATION_AND_SCHEDULE				    CHAR(2000),
		LAST_ADMINISTERED_DATE 			   DATE "YYYY-MM-DD" NULLIF LAST_ADMINISTERED_DATE="",
		MODIFIED_DOSE_AMOUNT				    CHAR(2000),
		MODIFIED_DOSE_UNITS				    CHAR(2000),
		MODIFIED_DOSE_ROUTE				    CHAR(2000),
		COMMENTS					    CHAR(2000),
		LOT_NUMBER					    CHAR(2000),
		FORMULATION					    CHAR(2000),
		AGENT_ADJUSTMENT_CODE				    INTEGER EXTERNAL(10),
		DOSE_CODE				    INTEGER EXTERNAL(10),
		MODIFIED_DOSE_CODE			    INTEGER EXTERNAL(10),
		FIRST_ADMINISTERED_DATE			   DATE "YYYY-MM-DD" NULLIF FIRST_ADMINISTERED_DATE=""
	)

