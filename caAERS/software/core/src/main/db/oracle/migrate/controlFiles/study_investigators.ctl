
LOAD DATA
	INFILE 'study_investigators.txt'
	DISCARDMAX 9999
	TRUNCATE INTO TABLE study_investigators
	fields terminated by '\t'
	
	trailing NULLCOLS
	(
		ID					    INTEGER EXTERNAL(10),
		SIGNATURE_TEXT 				    CHAR(2000),
		STUDY_SITES_ID 			    INTEGER EXTERNAL(10),
		SITE_INVESTIGATORS_ID				    INTEGER EXTERNAL(10),
		GRID_ID					    CHAR(2000),
		VERSION				    INTEGER EXTERNAL(10),
		ROLE_CODE					    CHAR(2000),
		RETIRED_INDICATOR				    INTEGER EXTERNAL(1) "case :RETIRED_INDICATOR
															when 't'then to_number(1)
															when 'f'then to_number(0)
															END",
		START_DATE					    TIMESTAMP(6) "YYYY-MM-DD HH24:MI:SS.FF6" NULLIF START_DATE="",
		END_DATE					    TIMESTAMP(6) "YYYY-MM-DD HH24:MI:SS.FF6" NULLIF END_DATE=""
	)
