
LOAD DATA
	INFILE 'spa_prior_therapies.txt'
	DISCARDMAX 9999
	TRUNCATE INTO TABLE spa_prior_therapies
	fields terminated by '\t'
	
	trailing NULLCOLS
	(
		ID					    INTEGER EXTERNAL(10),
		VERSION				    INTEGER EXTERNAL(10),
		GRID_ID					    CHAR(2000),
		ASSIGNMENT_ID				    INTEGER EXTERNAL(10),
		PRIOR_THERAPY_ID				    INTEGER EXTERNAL(10),
		OTHER						    CHAR(2000),
		START_DATE					   DATE "YYYY-MM-DD" NULLIF START_DATE="",
		END_DATE					   DATE "YYYY-MM-DD" NULLIF END_DATE="",
		LIST_INDEX				    INTEGER EXTERNAL(10),
		START_DATE_DAY 				    INTEGER EXTERNAL(10),
		START_DATE_MONTH				    INTEGER EXTERNAL(10),
		START_DATE_YEAR				    INTEGER EXTERNAL(10),
		START_DATE_ZONE			    INTEGER EXTERNAL(10),
		END_DATE_DAY					    INTEGER EXTERNAL(10),
		END_DATE_MONTH 				    INTEGER EXTERNAL(10),
		END_DATE_YEAR					    INTEGER EXTERNAL(10),
		END_DATE_ZONE				    INTEGER EXTERNAL(10)
	)

