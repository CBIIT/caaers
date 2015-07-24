
LOAD DATA
	INFILE 'concomitant_medications.txt'
	DISCARDMAX 9999
	TRUNCATE INTO TABLE concomitant_medications
	fields terminated by '\t'
	
	trailing NULLCOLS
	(
		ID					    INTEGER EXTERNAL(10),
		VERSION				    INTEGER EXTERNAL(10),
		GRID_ID					    CHAR(2000),
		REPORT_ID				    INTEGER EXTERNAL(10),
		AGENT_NAME					    CHAR(2000),
		LIST_INDEX				    INTEGER EXTERNAL(10),
		START_DATE_DAY 				    INTEGER EXTERNAL(10),
		START_DATE_MONTH				    INTEGER EXTERNAL(10),
		START_DATE_YEAR				    INTEGER EXTERNAL(10),
		START_DATE_ZONE			    INTEGER EXTERNAL(10),
		END_DATE_DAY					    INTEGER EXTERNAL(10),
		END_DATE_MONTH 				    INTEGER EXTERNAL(10),
		END_DATE_YEAR					    INTEGER EXTERNAL(10),
		END_DATE_ZONE				    INTEGER EXTERNAL(10),
		STILL_TAKING_MEDICATIONS			    INTEGER EXTERNAL(1) "case :STILL_TAKING_MEDICATIONS
															when 't'then to_number(1)
															when 'f'then to_number(0)
															END"
	)

