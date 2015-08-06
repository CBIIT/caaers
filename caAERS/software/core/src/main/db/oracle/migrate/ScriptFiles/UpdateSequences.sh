#!/bin/bash
sqlplus username/password@//hostname:1521/orcl << EOF
DECLARE
   MAX_VAL NUMBER;
   TABLENAME VARCHAR2(2000);
   SEQUENCE_COUNT NUMBER;
   SEQUENCE_NAME VARCHAR2(2000);
BEGIN  
  MAX_VAL := 0;
  SEQUENCE_COUNT := 0;
  FOR r IN ( SELECT table_name FROM user_tab_columns WHERE column_name = 'ID')
       LOOP
          EXECUTE IMMEDIATE 'select max(id+1) from '||r.table_name into MAX_VAL;
          TABLENAME := r.table_name;
           IF LENGTH(TABLENAME) > 23 THEN
            TABLENAME := SUBSTR(TABLENAME, 1, 23);
           END IF;
           SEQUENCE_NAME := 'SEQ_'|| TABLENAME ||'_ID';
           EXECUTE IMMEDIATE 'select count(*) from user_sequences where sequence_name = ''' || SEQUENCE_NAME || ''''  into SEQUENCE_COUNT;
           IF SEQUENCE_COUNT = 1 AND MAX_VAL > 0 THEN
            EXECUTE IMMEDIATE 'DROP SEQUENCE '|| SEQUENCE_NAME;
            EXECUTE IMMEDIATE 'CREATE SEQUENCE '|| SEQUENCE_NAME ||' START WITH '|| MAX_VAL ||' INCREMENT BY 1 CACHE 20';  
           END IF; 
       END LOOP;
EXECUTE IMMEDIATE 'DROP SEQUENCE CSM_APPLICATI_APPLICATION__SEQ';
EXECUTE IMMEDIATE 'DROP SEQUENCE CSM_FILTER_CLAUSE_FILTE_ID_SEQ'; 
EXECUTE IMMEDIATE 'DROP SEQUENCE CSM_GROUP_GROUP_ID_SEQ'; 
EXECUTE IMMEDIATE 'DROP SEQUENCE CSM_MAPPING_MAPPING_SEQ'; 
EXECUTE IMMEDIATE 'DROP SEQUENCE CSM_PG_PE_PG_PE_ID_SEQ'; 
EXECUTE IMMEDIATE 'DROP SEQUENCE CSM_PRIVILEGE_PRIVILEGE_ID_SEQ'; 
EXECUTE IMMEDIATE 'DROP SEQUENCE CSM_PROTECTIO_PROTECTION_E_SEQ'; 
EXECUTE IMMEDIATE 'DROP SEQUENCE CSM_PROTECTIO_PROTECTION_G_SEQ'; 
EXECUTE IMMEDIATE 'DROP SEQUENCE CSM_ROLE_PRIV_ROLE_PRIVILE_SEQ'; 
EXECUTE IMMEDIATE 'DROP SEQUENCE CSM_ROLE_ROLE_ID_SEQ'; 
EXECUTE IMMEDIATE 'DROP SEQUENCE CSM_USER_GROU_USER_GROUP_I_SEQ'; 
EXECUTE IMMEDIATE 'DROP SEQUENCE CSM_USER_GROU_USER_GROUP_R_SEQ'; 
EXECUTE IMMEDIATE 'DROP SEQUENCE CSM_USER_PE_USER_PROTECTIO_SEQ'; 
EXECUTE IMMEDIATE 'DROP SEQUENCE CSM_USER_USER_ID_SEQ';
EXECUTE IMMEDIATE 'DROP SEQUENCE HIBERNATE_SEQUENCE';
EXECUTE IMMEDIATE 'CREATE SEQUENCE HIBERNATE_SEQUENCE START WITH 35000';
EXECUTE IMMEDIATE 'CREATE SEQUENCE CSM_APPLICATI_APPLICATION__SEQ START WITH 1000';
EXECUTE IMMEDIATE 'CREATE SEQUENCE CSM_FILTER_CLAUSE_FILTE_ID_SEQ START WITH 1000'; 
EXECUTE IMMEDIATE 'CREATE SEQUENCE CSM_GROUP_GROUP_ID_SEQ START WITH 1000'; 
EXECUTE IMMEDIATE 'CREATE SEQUENCE CSM_MAPPING_MAPPING_SEQ START WITH 1000'; 
EXECUTE IMMEDIATE 'CREATE SEQUENCE CSM_PG_PE_PG_PE_ID_SEQ START WITH 1000'; 
EXECUTE IMMEDIATE 'CREATE SEQUENCE CSM_PRIVILEGE_PRIVILEGE_ID_SEQ START WITH 1000'; 
EXECUTE IMMEDIATE 'CREATE SEQUENCE CSM_PROTECTIO_PROTECTION_E_SEQ START WITH 1000'; 
EXECUTE IMMEDIATE 'CREATE SEQUENCE CSM_PROTECTIO_PROTECTION_G_SEQ START WITH 1000'; 
EXECUTE IMMEDIATE 'CREATE SEQUENCE CSM_ROLE_PRIV_ROLE_PRIVILE_SEQ START WITH 1000'; 
EXECUTE IMMEDIATE 'CREATE SEQUENCE CSM_ROLE_ROLE_ID_SEQ START WITH 1000'; 
EXECUTE IMMEDIATE 'CREATE SEQUENCE CSM_USER_GROU_USER_GROUP_I_SEQ START WITH 1000'; 
EXECUTE IMMEDIATE 'CREATE SEQUENCE CSM_USER_GROU_USER_GROUP_R_SEQ START WITH 1000'; 
EXECUTE IMMEDIATE 'CREATE SEQUENCE CSM_USER_PE_USER_PROTECTIO_SEQ START WITH 1000'; 
EXECUTE IMMEDIATE 'CREATE SEQUENCE CSM_USER_USER_ID_SEQ START WITH 1000';	   	
END;
/
quit
EOF