
LOAD DATA
	INFILE 'caaers_bootstrap_log.txt'
	DISCARDMAX 9999
	TRUNCATE INTO TABLE caaers_bootstrap_log
	fields terminated by '\t'
	
	trailing NULLCOLS
	(
		ID					    INTEGER EXTERNAL(10),
		RUNDATE					   DATE "YYYY-MM-DD" NULLIF RUNDATE="",
		OPERATION_CODE 				    INTEGER EXTERNAL(10),
		STATUS_CODE					    INTEGER EXTERNAL(10),
		GRID_ID					    CHAR(2000)
	)

