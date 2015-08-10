
LOAD DATA
	INFILE 'spa_disease_histories.txt'
	DISCARDMAX 9999
	TRUNCATE INTO TABLE spa_disease_histories
	fields terminated by '\t'
	
	trailing NULLCOLS
	(
		ID					    INTEGER EXTERNAL(10),
		CODED_PRIMARY_DISEASE_SITE_ID			    INTEGER EXTERNAL(10),
		STUDY_DISEASE_ID				    INTEGER EXTERNAL(10),
		OTHER_PRIMARY_DISEASE				    CHAR(2000),
		OTHER_PRIMARY_DISEASE_SITE			    CHAR(2000),
		DIAGNOSIS_DATE 				   DATE "YYYY-MM-DD" NULLIF DIAGNOSIS_DATE="",
		GRID_ID					    CHAR(2000),
		ASSIGNMENT_ID				    INTEGER EXTERNAL(10),
		DIAGNOSIS_DAY					    INTEGER EXTERNAL(10),
		DIAGNOSIS_MONTH				    INTEGER EXTERNAL(10),
		DIAGNOSIS_YEAR 				    INTEGER EXTERNAL(10),
		DIAGNOSIS_ZONE 			    INTEGER EXTERNAL(10),
		VERSION				    INTEGER EXTERNAL(10)
	)

