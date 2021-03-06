
LOAD DATA
	INFILE 'outcomes.txt'
	DISCARDMAX 9999
	TRUNCATE INTO TABLE outcomes
	fields terminated by '\t'
	
	trailing NULLCOLS
	(
		ID					    INTEGER EXTERNAL(10),
		INCIDENT_DATE					   DATE "YYYY-MM-DD" NULLIF INCIDENT_DATE="",
		OUTCOME_TYPE_CODE				    INTEGER EXTERNAL(10),
		OTHER						    CHAR(2000),
		VERSION				    INTEGER EXTERNAL(10),
		GRID_ID					    CHAR(2000),
		REPORT_ID					    INTEGER EXTERNAL(10),
		LIST_INDEX				    INTEGER EXTERNAL(10),
		ADVERSE_EVENT_ID				    INTEGER EXTERNAL(10)
	)

