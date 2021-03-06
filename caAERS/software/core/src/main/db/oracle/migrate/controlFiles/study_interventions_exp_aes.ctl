
LOAD DATA
	INFILE 'study_interventions_exp_aes.txt'
	DISCARDMAX 9999
	TRUNCATE INTO TABLE study_interventions_exp_aes
	fields terminated by '\t'
	
	trailing NULLCOLS
	(
		ID					    INTEGER EXTERNAL(10),
		GRID_ID					    CHAR(2000),
		VERSION				    INTEGER EXTERNAL(10),
		TERM_TYPE					    CHAR(2000),
		TERM_ID					    INTEGER EXTERNAL(10),
		LOW_LEVEL_TERM_ID				    INTEGER EXTERNAL(10),
		EXPECTEDNESS_FREQUENCY 			    FLOAT EXTERNAL(126),
		GRADE1FREQUENCY				    FLOAT EXTERNAL(126),
		GRADE2FREQUENCY				    FLOAT EXTERNAL(126),
		GRADE3FREQUENCY				    FLOAT EXTERNAL(126),
		GRADE4FREQUENCY				    FLOAT EXTERNAL(126),
		GRADE5FREQUENCY				    FLOAT EXTERNAL(126),
		EXPECTED					    INTEGER EXTERNAL(1) "case :EXPECTED
															when 't'then to_number(1)
															when 'f'then to_number(0)
															END",
		TREATMENT_ASSIGNMENT_ID			    INTEGER EXTERNAL(10)
	)

