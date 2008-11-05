alter table JBPM_ACTION drop constraint FK_ACTION_REFACT
alter table JBPM_ACTION drop constraint FK_CRTETIMERACT_TA
alter table JBPM_ACTION drop constraint FK_ACTION_PROCDEF
alter table JBPM_ACTION drop constraint FK_ACTION_EVENT
alter table JBPM_ACTION drop constraint FK_ACTION_ACTNDEL
alter table JBPM_ACTION drop constraint FK_ACTION_EXPTHDL
alter table JBPM_BYTEARRAY drop constraint FK_BYTEARR_FILDEF
alter table JBPM_BYTEBLOCK drop constraint FK_BYTEBLOCK_FILE
alter table JBPM_COMMENT drop constraint FK_COMMENT_TOKEN
alter table JBPM_COMMENT drop constraint FK_COMMENT_TSK
alter table JBPM_DECISIONCONDITIONS drop constraint FK_DECCOND_DEC
alter table JBPM_DELEGATION drop constraint FK_DELEGATION_PRCD
alter table JBPM_EVENT drop constraint FK_EVENT_PROCDEF
alter table JBPM_EVENT drop constraint FK_EVENT_TRANS
alter table JBPM_EVENT drop constraint FK_EVENT_NODE
alter table JBPM_EVENT drop constraint FK_EVENT_TASK
alter table JBPM_ID_GROUP drop constraint FK_ID_GRP_PARENT
alter table JBPM_ID_MEMBERSHIP drop constraint FK_ID_MEMSHIP_GRP
alter table JBPM_ID_MEMBERSHIP drop constraint FK_ID_MEMSHIP_USR
alter table JBPM_JOB drop constraint FK_JOB_PRINST
alter table JBPM_JOB drop constraint FK_JOB_ACTION
alter table JBPM_JOB drop constraint FK_JOB_TOKEN
alter table JBPM_JOB drop constraint FK_JOB_NODE
alter table JBPM_JOB drop constraint FK_JOB_TSKINST
alter table JBPM_LOG drop constraint FK_LOG_SOURCENODE
alter table JBPM_LOG drop constraint FK_LOG_DESTNODE
alter table JBPM_LOG drop constraint FK_LOG_TOKEN
alter table JBPM_LOG drop constraint FK_LOG_TRANSITION
alter table JBPM_LOG drop constraint FK_LOG_TASKINST
alter table JBPM_LOG drop constraint FK_LOG_CHILDTOKEN
alter table JBPM_LOG drop constraint FK_LOG_OLDBYTES
alter table JBPM_LOG drop constraint FK_LOG_SWIMINST
alter table JBPM_LOG drop constraint FK_LOG_NEWBYTES
alter table JBPM_LOG drop constraint FK_LOG_ACTION
alter table JBPM_LOG drop constraint FK_LOG_VARINST
alter table JBPM_LOG drop constraint FK_LOG_NODE
alter table JBPM_LOG drop constraint FK_LOG_PARENT
alter table JBPM_MODULEDEFINITION drop constraint FK_MODDEF_PROCDEF
alter table JBPM_MODULEDEFINITION drop constraint FK_TSKDEF_START
alter table JBPM_MODULEINSTANCE drop constraint FK_MODINST_PRCINST
alter table JBPM_MODULEINSTANCE drop constraint FK_TASKMGTINST_TMD
alter table JBPM_NODE drop constraint FK_DECISION_DELEG
alter table JBPM_NODE drop constraint FK_NODE_PROCDEF
alter table JBPM_NODE drop constraint FK_NODE_ACTION
alter table JBPM_NODE drop constraint FK_PROCST_SBPRCDEF
alter table JBPM_NODE drop constraint FK_NODE_SCRIPT
alter table JBPM_NODE drop constraint FK_NODE_SUPERSTATE
alter table JBPM_POOLEDACTOR drop constraint FK_POOLEDACTOR_SLI
alter table JBPM_PROCESSDEFINITION drop constraint FK_PROCDEF_STRTSTA
alter table JBPM_PROCESSINSTANCE drop constraint FK_PROCIN_PROCDEF
alter table JBPM_PROCESSINSTANCE drop constraint FK_PROCIN_ROOTTKN
alter table JBPM_PROCESSINSTANCE drop constraint FK_PROCIN_SPROCTKN
alter table JBPM_RUNTIMEACTION drop constraint FK_RTACTN_PROCINST
alter table JBPM_RUNTIMEACTION drop constraint FK_RTACTN_ACTION
alter table JBPM_SWIMLANE drop constraint FK_SWL_ASSDEL
alter table JBPM_SWIMLANE drop constraint FK_SWL_TSKMGMTDEF
alter table JBPM_SWIMLANEINSTANCE drop constraint FK_SWIMLANEINST_TM
alter table JBPM_SWIMLANEINSTANCE drop constraint FK_SWIMLANEINST_SL
alter table JBPM_TASK drop constraint FK_TASK_STARTST
alter table JBPM_TASK drop constraint FK_TASK_PROCDEF
alter table JBPM_TASK drop constraint FK_TASK_ASSDEL
alter table JBPM_TASK drop constraint FK_TASK_SWIMLANE
alter table JBPM_TASK drop constraint FK_TASK_TASKNODE
alter table JBPM_TASK drop constraint FK_TASK_TASKMGTDEF
alter table JBPM_TASK drop constraint FK_TSK_TSKCTRL
alter table JBPM_TASKACTORPOOL drop constraint FK_TASKACTPL_TSKI
alter table JBPM_TASKACTORPOOL drop constraint FK_TSKACTPOL_PLACT
alter table JBPM_TASKCONTROLLER drop constraint FK_TSKCTRL_DELEG
alter table JBPM_TASKINSTANCE drop constraint FK_TSKINS_PRCINS
alter table JBPM_TASKINSTANCE drop constraint FK_TASKINST_TMINST
alter table JBPM_TASKINSTANCE drop constraint FK_TASKINST_TOKEN
alter table JBPM_TASKINSTANCE drop constraint FK_TASKINST_SLINST
alter table JBPM_TASKINSTANCE drop constraint FK_TASKINST_TASK
alter table JBPM_TOKEN drop constraint FK_TOKEN_SUBPI
alter table JBPM_TOKEN drop constraint FK_TOKEN_PROCINST
alter table JBPM_TOKEN drop constraint FK_TOKEN_NODE
alter table JBPM_TOKEN drop constraint FK_TOKEN_PARENT
alter table JBPM_TOKENVARIABLEMAP drop constraint FK_TKVARMAP_TOKEN
alter table JBPM_TOKENVARIABLEMAP drop constraint FK_TKVARMAP_CTXT
alter table JBPM_TRANSITION drop constraint FK_TRANSITION_FROM
alter table JBPM_TRANSITION drop constraint FK_TRANS_PROCDEF
alter table JBPM_TRANSITION drop constraint FK_TRANSITION_TO
alter table JBPM_VARIABLEACCESS drop constraint FK_VARACC_PROCST
alter table JBPM_VARIABLEACCESS drop constraint FK_VARACC_SCRIPT
alter table JBPM_VARIABLEACCESS drop constraint FK_VARACC_TSKCTRL
alter table JBPM_VARIABLEINSTANCE drop constraint FK_VARINST_PRCINST
alter table JBPM_VARIABLEINSTANCE drop constraint FK_VARINST_TKVARMP
alter table JBPM_VARIABLEINSTANCE drop constraint FK_VARINST_TK
alter table JBPM_VARIABLEINSTANCE drop constraint FK_BYTEINST_ARRAY
alter table JBPM_VARIABLEINSTANCE drop constraint FK_VAR_TSKINST
drop table JBPM_ACTION if exists
drop table JBPM_BYTEARRAY if exists
drop table JBPM_BYTEBLOCK if exists
drop table JBPM_COMMENT if exists
drop table JBPM_DECISIONCONDITIONS if exists
drop table JBPM_DELEGATION if exists
drop table JBPM_EVENT if exists
drop table JBPM_EXCEPTIONHANDLER if exists
drop table JBPM_ID_GROUP if exists
drop table JBPM_ID_MEMBERSHIP if exists
drop table JBPM_ID_PERMISSIONS if exists
drop table JBPM_ID_USER if exists
drop table JBPM_JOB if exists
drop table JBPM_LOG if exists
drop table JBPM_MODULEDEFINITION if exists
drop table JBPM_MODULEINSTANCE if exists
drop table JBPM_NODE if exists
drop table JBPM_POOLEDACTOR if exists
drop table JBPM_PROCESSDEFINITION if exists
drop table JBPM_PROCESSINSTANCE if exists
drop table JBPM_RUNTIMEACTION if exists
drop table JBPM_SWIMLANE if exists
drop table JBPM_SWIMLANEINSTANCE if exists
drop table JBPM_TASK if exists
drop table JBPM_TASKACTORPOOL if exists
drop table JBPM_TASKCONTROLLER if exists
drop table JBPM_TASKINSTANCE if exists
drop table JBPM_TOKEN if exists
drop table JBPM_TOKENVARIABLEMAP if exists
drop table JBPM_TRANSITION if exists
drop table JBPM_VARIABLEACCESS if exists
drop table JBPM_VARIABLEINSTANCE if exists