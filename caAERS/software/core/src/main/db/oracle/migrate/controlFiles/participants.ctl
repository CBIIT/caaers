
LOAD DATA
	INFILE 'participants.txt'
	DISCARDMAX 9999
	TRUNCATE INTO TABLE participants
	fields terminated by '\t'
	
	trailing NULLCOLS
	(
		ID					    INTEGER EXTERNAL(10),
		INSTITUITIONAL_PATIENT_NUMBER			    CHAR(2000),
		INSTITUTION					    CHAR(2000),
		STUDY_PARTICIPANT_NAME 			    CHAR(2000),
		FIRST_NAME					    CHAR(2000),
		LAST_NAME					    CHAR(2000),
		GENDER 					    CHAR(2000),
		ETHNICITY					    CHAR(2000),
		RACE						    CHAR(2000),
		VERSION				    INTEGER EXTERNAL(10),
		MIDDLE_NAME					    CHAR(2000),
		MAIDEN_NAME					    CHAR(2000),
		GRID_ID					    CHAR(2000),
		LOAD_STATUS					    INTEGER EXTERNAL(10),
		BIRTH_YEAR					    INTEGER EXTERNAL(10),
		BIRTH_MONTH					    INTEGER EXTERNAL(10),
		BIRTH_DAY					    INTEGER EXTERNAL(10),
		BIRTH_ZONE				    INTEGER EXTERNAL(10)
	)

