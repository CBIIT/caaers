LOAD DATA
	INFILE 'participant_histories.txt'
	DISCARDMAX 9999
	TRUNCATE INTO TABLE participant_histories
	fields terminated by '\t'	
	trailing NULLCOLS
	(
		ID					    INTEGER EXTERNAL(10),
		BASELINE_PERFORMANCE_STATUS			    CHAR(2000),
		HEIGHT_UNIT					    CHAR(2000),
		WEIGHT_UNIT					    CHAR(2000),
		GRID_ID					    CHAR(2000),
		VERSION				    INTEGER EXTERNAL(10),
		REPORT_ID					    INTEGER EXTERNAL(10),
		HEIGHT 					    INTEGER EXTERNAL,
		WEIGHT 					    INTEGER EXTERNAL,
		HEIGHT_CODE					    INTEGER EXTERNAL(10),
		WEIGHT_CODE					    INTEGER EXTERNAL(10)
	)
