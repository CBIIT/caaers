
LOAD DATA
	INFILE 'meddra_pt.txt'
	DISCARDMAX 9999
	TRUNCATE INTO TABLE meddra_pt
	fields terminated by '\t'
	
	trailing NULLCOLS
	(
		ID					    INTEGER EXTERNAL(10),
		MEDDRA_CODE					    CHAR(2000),
		MEDDRA_TERM					    CHAR(2000),
		COSTART_SYMBOL 				    CHAR(2000),
		HARTS_CODE					    CHAR(2000),
		WHO_ART_CODE					    CHAR(2000),
		ICD9_CODE					    CHAR(2000),
		ICD9_CM_CODE					    CHAR(2000),
		ICD10_CODE					    CHAR(2000),
		JART_CODE					    CHAR(2000),
		MEDDRA_SOC_ID					    INTEGER EXTERNAL(10),
		VERSION				    INTEGER EXTERNAL(10),
		VERSION_ID					    INTEGER EXTERNAL(10)
	)

