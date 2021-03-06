
LOAD DATA
	INFILE 'ae_terms.txt'
	DISCARDMAX 9999
	TRUNCATE INTO TABLE ae_terms
	fields terminated by '\t'
	
	trailing NULLCOLS
	(
		ID					    INTEGER EXTERNAL(10),
		ADVERSE_EVENT_ID			    INTEGER EXTERNAL(10),
		TERM_ID					    INTEGER EXTERNAL(10),
		TERM_TYPE					    CHAR(2000),
		VERSION				    INTEGER EXTERNAL(10),
		GRID_ID					    CHAR(2000)
	)

