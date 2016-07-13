#!/bin/bash

db_host=
db_port=
db_name=
db_user=
db_passwd=
db_type=

as_home=

app_host=
app_port=
app_type=

data_dir=
old_spr_dir=
old_consent_dir=
app_plugin_dir=

mysql_dialect="org.hibernate.dialect.MySQLDialect"
oracle_dialect="org.hibernate.dialect.Oracle10gDialect"
mysqlDriver="com.mysql.jdbc.Driver"
oracleDriver="oracle.jdbc.driver.OracleDriver"
oracleUrl='jdbc:oracle:thin:@{{db-host}}:{{db-port}}:{{db-name}}'
mysqlUrl='jdbc:mysql:\/\/{{db-host}}:{{db-port}}\/{{db-name}}'
tmpVar="mysql"
dialect=
driver=
dbUrl=

while [ "$1" != "" ]; do
  case $1 in
    -dhost)
        shift
	db_host=$1;;
    -dport)
        shift
        db_port=$1;;
    -dname)
        shift
        db_name=$1;;
    -duser)
        shift
        db_user=$1;;
    -dpass)
        shift
        db_passwd=$1;;
    -dtype)
        shift
        db_type=$1;;
	-adtype)
        shift
        app_type=$1;;
    -ahome)
        shift
        as_home=$1;;
    -ahost)
        shift
        app_host=$1;;
    -aport)
        shift
        app_port=$1;;
    -adatadir)
    	shift
    	data_dir=$1;;
   	-aoldsprdir)
    	shift
    	old_spr_dir=$1;;
   	-aoldconsentdir)
    	shift
    	old_consent_dir=$1;;	 	    
  esac
  shift
done

if [ -z $db_host ]
then
  echo "DB hostname not specified. Use -dhost"
  exit
fi

if [ -z $db_port ]
then
  echo "DB port not specified. Use -dport"
  exit
fi
     
if [ -z $db_name ]
then
  echo "DB name not specified. Use -dname"
  exit
fi
     
if [ -z $db_user ]
then
  echo "DB user not specified. Use -duser"
  exit
fi
     
if [ -z $db_passwd ]
then
  echo "DB password not specified. Use -dpass"
  exit
fi

if [ -z $db_type ]
then
  echo "DB type not specified. Use -dtype"
  exit
fi

if [ -z $as_home ]
then
  echo "Application server home directory not specified. Use -ahome"
  exit
fi

if [ -z $app_host ]
then
  echo "Application server host not specified. Use -ahost"
  exit
fi

if [ -z $app_port ]
then
  echo "Application server port not specified. Use -aport"
  exit
fi

if [ -z $app_type ]
then
  echo "Application deployment type not specified. Use -adtype"
  exit
fi

echo "Step 1: Preparing OpenSpecimen database ..."

if [ $db_type == $tmpVar ]
then
  dialect=$mysql_dialect
  driver=$mysqlDriver
  dbUrl=$mysqlUrl
else
  dialect=$oracle_dialect
  driver=$oracleDriver
  dbUrl=$oracleUrl
fi

echo "Step 2: Copying OpenSpecimen files ..."
mkdir -p $as_home
cp -R tomcat-as/* $as_home

echo "Step 3: Configuring OpenSpecimen ..."
grep -rl {{db-url}} $as_home | xargs sed -i s/{{db-url}}/${dbUrl}/g
grep -rl {{db-host}} $as_home | xargs sed -i s/{{db-host}}/${db_host}/g
grep -rl {{db-port}} $as_home | xargs sed -i s/{{db-port}}/${db_port}/g
grep -rl {{db-name}} $as_home | xargs sed -i s/{{db-name}}/${db_name}/g
grep -rl {{db-username}} $as_home | xargs sed -i s/{{db-username}}/${db_user}/g
grep -rl {{db-password}} $as_home | xargs sed -i s/{{db-password}}/${db_passwd}/g
#grep -rl {{as-home}} $as_home | xargs sed -i "s|{{as-home}}|${as_home}|g"
#grep -rl {{apphost-ip}} $as_home | xargs sed -i s/{{apphost-ip}}/${app_host}/g
grep -rl {{apphost-port}} $as_home | xargs sed -i s/{{apphost-port}}/${app_port}/g
grep -rl {{db-driver}} $as_home | xargs sed -i s/{{db-driver}}/${driver}/g


if [[ ! -z $data_dir ]] 
then 
  tmp_dir=$as_home/tmpos
  unzip $as_home/webapps/openspecimen.war -d $tmp_dir
  echo "Unzip war to replace data-dir property in application.properties file"
  sed -i 's:^[ \t]*app.data_dir[ \t]*=\([ \t]*.*\)$:app.data_dir='${data_dir}':' $tmp_dir/WEB-INF/classes/application.properties
  sed -i 's:^[ \t]*visit.old_spr_dir[ \t]*=\([ \t]*.*\)$:visit.old_spr_dir='${old_spr_dir}':' $tmp_dir/WEB-INF/classes/migration.properties
  sed -i 's:^[ \t]*participant.old_consents_dir[ \t]*=\([ \t]*.*\)$:participant.old_consents_dir='${old_consent_dir}':' $tmp_dir/WEB-INF/classes/migration.properties
  
  sed -i 's:^[ \t]*database.type[ \t]*=\([ \t]*.*\)$:database.type='${db_type}':' $tmp_dir/WEB-INF/classes/application.properties
  sed -i 's:^[ \t]*datasource.type[ \t]*=\([ \t]*.*\)$:datasource.type='${app_type}':' $tmp_dir/WEB-INF/classes/application.properties
  sed -i 's:^[ \t]*datasource.dialect[ \t]*=\([ \t]*.*\)$:datasource.dialect='${dialect}':' $tmp_dir/WEB-INF/classes/application.properties
  sed -i 's:^[ \t]*plugin.dir[ \t]*=\([ \t]*.*\)$:plugin.dir='${app_plugin_dir}':' $tmp_dir/WEB-INF/classes/application.properties
  
  jar cvf $as_home/webapps/openspecimen.war -C $tmp_dir .
  echo "Created war"
  rm -rf $tmp_dir
  echo "Removed temp directory"
fi  

echo "Step 4: Finished. Now start application server....."

