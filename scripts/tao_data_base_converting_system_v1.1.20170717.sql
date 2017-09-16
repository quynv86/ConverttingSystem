
drop table  AUTH_PERM;

  CREATE TABLE AUTH_PERM 
   (	ID bigint identity(1,1) primary key, 
      NAME VARCHAR(50 )
   )
   ;
--------------------------------------------------------
--  DDL for Table AUTH_ROLE
--------------------------------------------------------

  CREATE TABLE AUTH_ROLE 
   (	ID bigint identity(1,1) primary key, 
	NAME VARCHAR(50 )
   );
--------------------------------------------------------
--  DDL for Table AUTH_ROLE_PERM
--------------------------------------------------------

  CREATE TABLE AUTH_ROLE_PERM 
   (	ROLE_ID bigint, 
      PERM_ID bigint
   );
--------------------------------------------------------
--  DDL for Table AUTH_USER
--------------------------------------------------------
 drop table AUTH_USER;
 
  CREATE TABLE AUTH_USER 
   (	ID bigint identity(1,1) primary key, 
	USER_NAME VARCHAR(45 ), 
	FIRST_NAME VARCHAR(15 ), 
	MIDDLE_NAME VARCHAR(15 ), 
	LAST_NAME VARCHAR(15 ), 
	FULL_NAME VARCHAR(50 ), 
	EMAIL VARCHAR(50 ), 
	GENDER VARCHAR(1 ), 
	SALT VARCHAR(45 ), 
	PASSWORD VARCHAR(128 ), 
	CREATED_DATE DATETIME, 
	MODIFIED_DATE DATETIME, 
	IS_VERIFIED int, 
	STATUS int, 
	USER_TYPE int
   );
--------------------------------------------------------
--  DDL for Table AUTH_USER_ROLE
--------------------------------------------------------

  CREATE TABLE AUTH_USER_ROLE 
   (USER_ID bigint, 
    ROLE_ID bigint
   );
--------------------------------------------------------
--  DDL for Table AUTH_USERMETA
--------------------------------------------------------
 drop table AUTH_USERMETA;
 
  CREATE TABLE AUTH_USERMETA 
   (	ID bigint identity(1,1) primary key, 
      USER_ID bigint, 
      META_KEY VARCHAR(255 ), 
      META_VALUE VARCHAR(500 )
   );
--------------------------------------------------------
--  DDL for Table CDR_FILE
--------------------------------------------------------
drop table CDR_FILE;

CREATE TABLE CDR_FILE 
   (	CDR_FILE_ID bigint identity(1,1) primary key, 
      FILE_ID bigint, 
      FILE_NAME VARCHAR(50 ), 
      FILE_SEQ bigint, 
      FILE_DATE DATE, 
      PROCESS_DATE DATETIME, 
      DATASOURCE int, 
      FILE_SIZE float, 
      TOTAL_RECORDS int, 
      ACTION VARCHAR(30 ), 
      STATUS int, 
      ERROR_REASON VARCHAR(200)
   );
--------------------------------------------------------
--  DDL for Table DATA_SOURCE
--------------------------------------------------------
  drop table   DATA_SOURCE;
  
  CREATE TABLE DATA_SOURCE 
   (	ID bigint identity(1,1) primary key, 
    CODE VARCHAR(100 )
   );
--------------------------------------------------------
--  DDL for Table THREADS_CONFIG
--------------------------------------------------------
  drop table   THREADS_CONFIG;
  
  CREATE TABLE THREADS_CONFIG 
   (	THREAD_ID bigint identity(1,1) primary key, 
      THREAD_NAME VARCHAR(50 ), 
      CLASS_NAME VARCHAR(100 ), 
      AUTO_START VARCHAR(10 ), 
      DELAY_TIME bigint, 
      SCHEDULE VARCHAR(200 ), 
      THREAD_ORDER int, 
      THREAD_GROUP int, 
      PARAMS VARCHAR(2000 ), 
      ENABLE_SCHEDULE VARCHAR(1)
   );
--------------------------------------------------------
--  DDL for Table THREADS_LOG
--------------------------------------------------------
  drop table   THREADS_LOG;
  
  CREATE TABLE THREADS_LOG 
   (	LOG_ID bigint identity(1,1) primary key, 
	THREAD_ID bigint, 
	LOG_LEVEL int, 
	LOG_DATE DATETIME, 
	LOG_CONTENT VARCHAR(2000)
   );
--------------------------------------------------------
--  DDL for Table CDR_LOG_DETAIL
--------------------------------------------------------
  drop table  CDR_LOG_DETAIL;
  
  CREATE TABLE CDR_LOG_DETAIL 
   (ID bigint identity(1,1) primary key, 
    PROCESS_DATE DATETIME, 
    CDR_FILE_ID bigint, 
    V_KEY VARCHAR(50 ), 
    VALUE bigint, 
    FILE_DATE DATE
   );

select *From threads_log;

Insert into THREADS_CONFIG (THREAD_NAME,CLASS_NAME,AUTO_START,DELAY_TIME,SCHEDULE,THREAD_ORDER,THREAD_GROUP,PARAMS,ENABLE_SCHEDULE)
values ('CDR Converting New','vn.yotel.thread.CDRConvertingThread','N','5','schedule','0','1','{"wildcard":"*.dat","local-file-format":"$BaseFileName.txt","fdate-index":"2","temp-dir":"C:/app/convert_demo_v1/data/temp/","fname-pattern":"^(NVXS_)(\\d{4}\\d{2}\\d{2})(\\d{8}).dat$","output-header":"FILE_ID;TYPE;CALL_TYPE;IMSI;IMEI;STA_DATETIME;DURATION;CALLING_NUMBER;CALLED_NUMBER;TAR_CLASS;TS_CODE;CELL_ID;IC_ROUTE;OG_ROUTE;CHARGE_INFO;SERVICE_KEY;CONNECTEDNUMBER;TRANSLATEDNUMBER;ORG_CALLING_NUMBER;ORG_CALLED_NUMBER;MSCADDRESS;CALLREFERENCE;ORGMSCADDRESS;CAMELDESTINATIONNUMBER;SUPP_CODE;CAUSEFORTERM;SEQUENCENUMBER;FILE_NAME;LOCATION;OG_ROUTE_ID;IC_ROUTE_ID;NETWORK_CALL_REF;GLOBAL_CALL_REF;RELEASE_TIME;RECORD_NUMBER;DIAGNOSTICS","fseq-index":"3","recursive":"false","fdate-format":"yyyyMMdd","local-dir":"C:/app/convert_demo_v1/data/import/","backup-dir":"C:/app/convert_demo_v1/data/backupdir/","log-level":"INFOR","cdr-logger":"ConvertLogger","export-dir":"C:/app/convert_demo_v1/data/exportdir/"}','Y');

Insert into AUTH_USER_ROLE (USER_ID,ROLE_ID) values ('1','1');
Insert into AUTH_USER_ROLE (USER_ID,ROLE_ID) values ('1','2');
Insert into AUTH_USER_ROLE (USER_ID,ROLE_ID) values ('1','3');
Insert into AUTH_USER_ROLE (USER_ID,ROLE_ID) values ('3','2');

Insert into AUTH_ROLE (NAME) values ('ROLE_ADMIN');
Insert into AUTH_ROLE (NAME) values ('ROLE_USER');
Insert into AUTH_ROLE (NAME) values ('ROLE_MANAGE_THREADS');


Insert into AUTH_USER (USER_NAME,FIRST_NAME,MIDDLE_NAME,LAST_NAME,FULL_NAME,EMAIL,GENDER,SALT,PASSWORD,CREATED_DATE,MODIFIED_DATE,IS_VERIFIED,STATUS,USER_TYPE) values ('admin','Admin',null,null,null,'admin@yo.com','1','5876695f8e4e1811','jYDYj+agRnS8NfxNLEGj11paJlQ=','2017-01-01 00:00:00',null,'1','1','1');
Insert into AUTH_USER (USER_NAME,FIRST_NAME,MIDDLE_NAME,LAST_NAME,FULL_NAME,EMAIL,GENDER,SALT,PASSWORD,CREATED_DATE,MODIFIED_DATE,IS_VERIFIED,STATUS,USER_TYPE) values ('user','User',null,null,null,'user@yo.com','1','5876695f8e4e1811','IEkbnAKxLSX3d74SDOH5wBdgOu4=','2017-01-01 00:00:00',null,'1','1','1');

select *From THREADS_CONFIG;
select *From AUTH_USER_ROLE;
select *from AUTH_USER;

commit;


