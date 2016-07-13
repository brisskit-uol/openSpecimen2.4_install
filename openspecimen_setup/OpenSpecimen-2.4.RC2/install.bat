@ECHO off
SET db_host=""
SET db_port=""
SET db_name=""
SET db_user=""
SET db_passwd=""
SET db_type=""
SET as_home=""
SET app_host=""
SET app_port=""
SET app_data_dir=""
SET app_type=""
SET old_consent_dir=""
SET old_spr_dir=""

SET app_plugin_dir=""
SET mysql_dialect=org.hibernate.dialect.MySQLDialect
SET oracle_dialect=org.hibernate.dialect.Oracle10gDialect
SET mysqlDriver="com.mysql.jdbc.Driver"
SET oracleDriver="oracle.jdbc.driver.OracleDriver"
SET oracleUrl="jdbc:oracle:thin:@{{db-host}}:{{db-port}}:{{db-name}}"
SET mysqlUrl="jdbc:mysql://{{db-host}}:{{db-port}}/{{db-name}}"
SET driver=""
SET dbUrl=""
SET dialect=""


IF "%1" NEQ "" (
  GOTO = %1
    :-dhost 
      SHIFT
      SET db_host=%1
	  SHIFT
	  IF NOT "%1" == "" ( GOTO = %1 )
    
	:-dport
      SHIFT
      SET db_port=%1
	  SHIFT
	  IF NOT "%1" == "" ( GOTO = %1 )
    
	:-dname
      SHIFT
      SET db_name=%1
	  SHIFT
	  IF NOT "%1" == "" ( GOTO = %1 )
    
	:-duser
      SHIFT
      SET db_user=%1
	  SHIFT
	  IF NOT "%1" == "" ( GOTO = %1 )
    
	:-dpass
      SHIFT
      SET db_passwd=%1
	  SHIFT
	  IF NOT "%1" == "" ( GOTO = %1 )
	  
	:-dtype
      SHIFT
      SET db_type=%1
	  SHIFT
	  IF NOT "%1" == "" ( GOTO = %1 )
    
	:-ahome
      SHIFT
      SET as_home=%1
	  SHIFT
	  IF NOT "%1" == "" ( GOTO = %1 )
	
    :-ahost
      SHIFT
      SET app_host=%1
	  SHIFT
	  IF NOT "%1" == "" ( GOTO = %1 )
    
	:-aport
      SHIFT
      SET app_port=%1
	  SHIFT
	  IF NOT "%1" == "" ( GOTO = %1 )
	  
	:-adtype
	  SHIFT
      SET app_type=%1
	  SHIFT
	  IF NOT "%1" == "" ( GOTO = %1 )
	  
	:-adatadir
      SHIFT
      SET app_data_dir=%1
	  SHIFT
	  IF NOT "%1" == "" ( GOTO = %1 )
	
        :-aplugindir
      SHIFT
      SET app_plugin_dir=%1
	  SHIFT
	  IF NOT "%1" == "" ( GOTO = %1 )
	  
	:-aoldconsentdir
      SHIFT
      SET old_consent_dir=%1
	  SHIFT
	  IF NOT "%1" == "" ( GOTO = %1 )
	  
	:-aoldsprdir
      SHIFT
      SET old_spr_dir=%1
	  SHIFT
	  IF NOT "%1" == "" ( GOTO = %1 )
) ELSE ( ECHO "please enter paramerters")
	
	
IF "%db_host%" EQU "" ( 
  ECHO "DB hostname not specified. Use -dhost"
  EXIT /b
)

IF "%db_port%" EQU "" ( 
  ECHO "DB port not specified. Use -dport"
  EXIT /b
)

IF "%db_name%" EQU "" ( 
  ECHO "DB name not specified. Use -dname"
  EXIT /b
)

IF "%db_user%" EQU "" ( 
  ECHO "DB user not specified. Use -duser"
  EXIT /b
)

IF "%db_passwd%" EQU "" ( 
  ECHO "DB password not specified. Use -dpass"
  EXIT /b
)

IF "%db_type%" EQU "" ( 
  ECHO "DB type not specified. Use -dtype"
  EXIT /b
)

IF "%as_home%" EQU "" ( 
  ECHO "Application server home directory not specified. Use -ahome"
  EXIT /b
)

IF "%app_host%" EQU "" ( 
  ECHO "Application server host not specified. Use -ahost"
  EXIT /b
)

IF "%app_port%" EQU "" ( 
  ECHO "Application server port not specified. Use -aport"
  EXIT /b
)

IF "%app_type%" EQU "" ( 
  ECHO "Application deployment type not specified. Use -adtype"
  EXIT /b
)

IF "%app_data_dir%" EQU "" ( 
  ECHO "Application data directory not specified. Use -adatadir"
  EXIT /b
)


ECHO "Step 1: Preparing OpenSpecimen database ..."
   

ECHO "Step 2: Copying OpenSpecimen files ..."
   mkdir %as_home%
   xcopy tomcat-as %as_home% /s /e 

ECHO "Step 3: Configuring OpenSpecimen ..."

IF "%db_type%" EQU "mysql" (
SET dialect=%mysql_dialect%
SET driver=%mysqlDriver%
SET dbUrl=%mysqlUrl%
) ELSE (
SET dialect=%oracle_dialect%
SET driver=%oracleDriver%
SET dbUrl=%oracleUrl%
)


ECHO "Replacing data directory path"
set ABS_PATH=%CD%
SET tmp_dir="tmpos"

cd %ABS_PATH%\tomcat-as\webapps

IF NOT EXIST openspecimen.zip (
REN openspecimen.war openspecimen.zip
)

cd %ABS_PATH%

CALL zipjs.bat unzip -source %ABS_PATH%\tomcat-as\webapps\openspecimen.zip -destination %as_home%\tmpos 

ECHO "unzipped jar file at  location %as_home%\webapps\openspecimen.war ......................"

CALL prop_replace.bat app.data_dir %app_data_dir% %as_home%\tmpos\WEB-INF\classes\application.properties
CALL prop_replace.bat datasource.type %app_type% %as_home%\tmpos\WEB-INF\classes\application.properties
CALL prop_replace.bat datasource.dialect %dialect% %as_home%\tmpos\WEB-INF\classes\application.properties
CALL prop_replace.bat database.type %db_type% %as_home%\tmpos\WEB-INF\classes\application.properties

IF "%app_plugin_dir%" NEQ "" (
ECHO "under plugin................."
CALL prop_replace.bat plugin.dir %app_plugin_dir% %as_home%\tmpos\WEB-INF\classes\application.properties
)

IF "%old_consent_dir%" NEQ "" (
ECHO "old consent................."
CALL prop_replace.bat participant.old_consents_dir %old_consent_dir% %as_home%\tmpos\WEB-INF\classes\migration.properties
)

IF "%old_spr_dir%" NEQ "" (
ECHO "SPR dir................."
CALL prop_replace.bat visit.old_spr_dir %old_spr_dir% %as_home%\tmpos\WEB-INF\classes\migration.properties
)

DEL %as_home%\tmpos\WEB-INF\classes\application.properties.bak
                         
cd %ABS_PATH%

ECHO "Building war.........."
DEL %as_home%\webapps\openspecimen.zip

jar cf %as_home%\webapps\openspecimen.war -C %as_home%\\%tmp_dir% .
ECHO "Created jar file at  location %as_home%\webapps\openspecimen.war ......................"

IF EXIST %as_home%\\%tmp_dir% (
    rmdir %as_home%\\%tmp_dir% /s /q
)

SET consent_dir="participant-consents"
IF NOT EXIST %app_data_dir%\%consent_dir% (
ECHO "Creating consent directory : %app_data_dir%/participant-consents/"
    mkdir %app_data_dir%/participant-consents/
)

ECHO "Updating the application.properties file........" 

findstr /S /M /C:"{{db-url}}" %as_home%\*.** >> propsFiles.txt
FOR /f %%F IN (propsFiles.txt) DO (
  CALL :FindReplace "{{db-url}}" "%dbUrl%" "%%F"
)

IF EXIST propsFiles.txt (
  DEL propsFiles.txt
)

findstr /S /M /C:"{{db-driver}}" %as_home%\*.** >> propsFiles.txt
FOR /f %%F IN (propsFiles.txt) DO (
  CALL :FindReplace "{{db-driver}}" "%driver%" "%%F"
)

IF EXIST propsFiles.txt (
  DEL propsFiles.txt
)

findstr /S /M /C:"{{db-host}}" %as_home%\*.** >> propsFiles.txt
FOR /f %%F IN (propsFiles.txt) DO (
  CALL :FindReplace "{{db-host}}" "%db_host%" "%%F"
)

IF EXIST propsFiles.txt (
  DEL propsFiles.txt
)

findstr /S /M /C:"{{db-port}}" %as_home%\*.** >> propsFiles.txt
FOR /f %%F IN (propsFiles.txt) DO (
  CALL :FindReplace "{{db-port}}" "%db_port%" "%%F"
)

IF EXIST propsFiles.txt (
  DEL propsFiles.txt
)

findstr /S /M /C:"{{db-name}}"  %as_home%\*.** >> propsFiles.txt
FOR /f %%F IN (propsFiles.txt) DO (
  CALL :FindReplace "{{db-name}}" "%db_name%" "%%F"
)

IF EXIST propsFiles.txt (
  DEL propsFiles.txt
)

findstr /S /M /C:"{{db-username}}" %as_home%\*.** >> propsFiles.txt
FOR /f %%F IN (propsFiles.txt) DO (
  CALL :FindReplace "{{db-username}}" "%db_user%" "%%F"
)

IF EXIST propsFiles.txt (
  DEL propsFiles.txt
)

findstr /S /M /C:"{{db-password}}" %as_home%\*.** >> propsFiles.txt
FOR /f %%F IN (propsFiles.txt) DO (
  CALL :FindReplace "{{db-password}}" "%db_passwd%" "%%F"
)

IF EXIST propsFiles.txt (
  DEL propsFiles.txt
)

findstr /S /M /C:"{{apphost-ip}}" %as_home%\*.** >> propsFiles.txt
FOR /f %%F IN (propsFiles.txt) DO (
  CALL :FindReplace "{{apphost-ip}}" "%app_host%" "%%F"
)

IF EXIST propsFiles.txt (
  DEL propsFiles.txt
)

findstr /S /M /C:"{{apphost-port}}" %as_home%\*.** >> propsFiles.txt
FOR /f %%F IN (propsFiles.txt) DO (
  CALL :FindReplace "{{apphost-port}}" "%app_port%" "%%F"
)

IF EXIST propsFiles.txt (
  DEL propsFiles.txt
)

findstr /S /M /C:"{{as-home}}" %as_home%\*.** >> propsFiles.txt
FOR /f %%F IN (propsFiles.txt) DO (
  CALL :FindReplace "{{as-home}}" "%as_home%" "%%F"
)

IF EXIST propsFiles.txt (
  DEL propsFiles.txt
)

:FindReplace
  IF NOT [%3] EQU [] (
    TYPE %3|replace "%1" "%2" >new.new
    MOVE /y new.new %3 )
:end 




