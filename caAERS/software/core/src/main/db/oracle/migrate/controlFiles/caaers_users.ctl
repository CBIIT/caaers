
LOAD DATA
	INFILE 'caaers_users.txt'
	DISCARDMAX 9999
	TRUNCATE INTO TABLE caaers_users
	fields terminated by '\t'
	
	trailing NULLCOLS
	(
		ID					    INTEGER EXTERNAL(10),
		VERSION				    INTEGER EXTERNAL(10),
		LOGIN_NAME				    CHAR(2000),
		SALT						    CHAR(2000),
		TOKEN						    CHAR(2000),
		TOKEN_TIME					    TIMESTAMP(6) "YYYY-MM-DD HH24:MI:SS.FF6" NULLIF TOKEN_TIME="",
		PASSWORD_LAST_SET				    TIMESTAMP(6) "YYYY-MM-DD HH24:MI:SS.FF6" NULLIF PASSWORD_LAST_SET="",
		LAST_LOGIN					    TIMESTAMP(6) "YYYY-MM-DD HH24:MI:SS.FF6" NULLIF LAST_LOGIN="",
		NUM_FAILED_LOGINS			    INTEGER EXTERNAL(10),
		GRID_ID					    CHAR(2000)
	)

