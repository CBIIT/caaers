
LOAD DATA
	INFILE 'ext_adverse_events.txt'
	DISCARDMAX 9999
	TRUNCATE INTO TABLE ext_adverse_events
	fields terminated by '\t'
	
	trailing NULLCOLS
	(
		ID					    INTEGER EXTERNAL(10),
		VERSION				    INTEGER EXTERNAL(10),
		ADVERSE_EVENT_TERM				    CHAR(2000),
		ATTRIBUTION					    CHAR(2000),
		START_DATE					   DATE "YYYY-MM-DD" NULLIF START_DATE="",
		END_DATE					   DATE "YYYY-MM-DD" NULLIF END_DATE="",
		VERBATIM					    CHAR(2000),
		GRADE						    INTEGER EXTERNAL(10),
		EXTERNAL_ID					    CHAR(2000),
		GRID_ID					    CHAR(2000),
		ADVERSE_EVENT_TERM_CODE			    CHAR(2000),
		ADVERSE_EVENT_TERM_OTHER_VALUE 		    CHAR(2000),
		HOW_SERIOUS					    CHAR(2000),
		CREATION_DATE				   DATE "YYYY-MM-DD" NULLIF CREATION_DATE="",
		REVIEWED_DATE					   DATE "YYYY-MM-DD" NULLIF REVIEWED_DATE="",
		STATUS 				    CHAR(2000),
		EXT_REP_PRD_ID 				    INTEGER EXTERNAL(10),
		OTHER_SPECIFY					    CHAR(2000)
	)

