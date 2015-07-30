
LOAD DATA
	INFILE 'reconciled_adverse_events.txt'
	DISCARDMAX 9999
	TRUNCATE INTO TABLE reconciled_adverse_events
	fields terminated by '\t'
	
	trailing NULLCOLS
	(
		ID					    INTEGER EXTERNAL(10),
		VERSION				    INTEGER EXTERNAL(10),
		GRID_ID					    CHAR(2000),
		ITEM_ID					    INTEGER EXTERNAL(10),
		REPORT_ID					    INTEGER EXTERNAL(10),
		ATTRIBUTION_SUMMARY_CODE			    INTEGER EXTERNAL(10),
		WHY_SERIOUS					    CHAR(2000),
		START_DATE					   DATE "YYYY-MM-DD" NULLIF START_DATE="",
		END_DATE					   DATE "YYYY-MM-DD" NULLIF END_DATE="",
		VERBATIM					    CHAR(2000),
		ERROR_MESSAGE					    CHAR(2000),
		GRADE_CODE					    INTEGER EXTERNAL(10),
		ACTION_CODE				    INTEGER EXTERNAL(10),
		EXTERNAL_ID					    CHAR(2000),
		TERM_CODE					    CHAR(2000),
		TERM_NAME					    CHAR(2000),
		TERM_OTHER_SPECIFY				    CHAR(2000),
		SYSTEM 				    INTEGER EXTERNAL(10)
	)

