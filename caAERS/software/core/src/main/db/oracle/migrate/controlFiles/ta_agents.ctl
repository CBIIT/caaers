
LOAD DATA
	INFILE 'ta_agents.txt'
	DISCARDMAX 9999
	TRUNCATE INTO TABLE ta_agents
	fields terminated by '\t'
	
	trailing NULLCOLS
	(
		ID					    INTEGER EXTERNAL(10),
		GRID_ID					    CHAR(2000),
		VERSION				    INTEGER EXTERNAL(10),
		STUDY_AGENT_ID 				    INTEGER EXTERNAL(10)
	)

