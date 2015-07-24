
LOAD DATA
	INFILE 'wf_assignees.txt'
	DISCARDMAX 9999
	TRUNCATE INTO TABLE wf_assignees
	fields terminated by '\t'
	
	trailing NULLCOLS
	(
		ID					    INTEGER EXTERNAL(10),
		GRID_ID					    CHAR(2000),
		VERSION				    INTEGER EXTERNAL(10),
		NAME						    CHAR(2000),
		USER_ROLE_ID					    INTEGER EXTERNAL(10),
		TASK_CONFIG_ID 			    INTEGER EXTERNAL(10),
		DTYPE					    CHAR(2000),
		INVESTIGATOR_ID				    INTEGER EXTERNAL(10),
		RESEARCHSTAFF_ID				    INTEGER EXTERNAL(10)
	)

