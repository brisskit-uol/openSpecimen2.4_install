#!/bin/bash 
cd /var/local/brisskit/openspecimen/system/OpenSpecimen-2.4.RC2/tomcat-as/webapps/integration/temp/files
rm *
cd /var/local/brisskit/openspecimen/system/OpenSpecimen-2.4.RC2/tomcat-as/webapps/integration/temp/pdo
rm *
cd /var/local/brisskit/openspecimen/system/OpenSpecimen-2.4.RC2/tomcat-as/webapps/integration/catissue_i2b2
export CLASSPATH=.:/var/local/brisskit/openspecimen/system/OpenSpecimen-2.4.RC2/tomcat-as/webapps/integration/lib/log4j-1.2.14.jar:/var/local/brisskit/openspecimen/system/OpenSpecimen-2.4.RC2/tomcat-as/webapps/integration/lib/commons-beanutils.jar:/var/local/brisskit/openspecimen/system/OpenSpecimen-2.4.RC2/tomcat-as/webapps/integration/lib/mysql-connector-java-5.0.8-bin.jar:/var/local/brisskit/openspecimen/system/OpenSpecimen-2.4.RC2/tomcat-as/webapps/integration/lib/jersey-bundle-1.3.jar:/var/local/brisskit/openspecimen/system/OpenSpecimen-2.4.RC2/tomcat-as/webapps/integration/lib/jersey-client-1.3.jar:/var/local/brisskit/openspecimen/system/OpenSpecimen-2.4.RC2/tomcat-as/webapps/integration/lib/jersey-core-1.3.jar:/var/local/brisskit/openspecimen/system/OpenSpecimen-2.4.RC2/tomcat-as/webapps/integration/lib/jsr311-api-1.1.1.jar
JAVA_HOME=/var/local/brisskit/jdk1.6.0_39/
PATH=$JAVA_HOME/bin:$JAVA_HOME/jre/bin:$PATH
javac -classpath $CLASSPATH /var/local/brisskit/openspecimen/system/OpenSpecimen-2.4.RC2/tomcat-as/webapps/integration/os2i2b2/*.java
$JAVA_HOME/bin/java -classpath $CLASSPATH os2i2b2/caTissueParser
$JAVA_HOME/bin/java -classpath $CLASSPATH os2i2b2/caTissue_StorageOntology
$JAVA_HOME/bin/java -classpath $CLASSPATH os2i2b2/caTissue_ProtocolOntology
$JAVA_HOME/bin/java -classpath $CLASSPATH os2i2b2/caTissue_LocationOntology
$JAVA_HOME/bin/java -classpath $CLASSPATH os2i2b2/caTissue_BarcodeOntology
echo $JAVA_HOME
echo "files created"
chmod 777 /var/local/brisskit/openspecimen/system/OpenSpecimen-2.4.RC2/tomcat-as/webapps/integration/temp/files/*
chmod 777 /var/local/brisskit/openspecimen/system/OpenSpecimen-2.4.RC2/tomcat-as/webapps/integration/temp/pdo/*
chmod -R 777 /var/local/brisskit/openspecimen/system/OpenSpecimen-2.4.RC2/tomcat-as/webapps/integration/*
/var/local/brisskit/openspecimen/system/OpenSpecimen-2.4.RC2/tomcat-as/webapps/integration/os_to_i2b2_push.sh
echo "files transferred to i2b2"
exit 0

