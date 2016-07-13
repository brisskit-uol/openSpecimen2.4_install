#!/bin/bash 

#Copy the pdos and enums from the catissue VM to the i2b2 VM.
#This will delete the files on i2b2 if they dont exist on catissue anymore

#Run this on openspecimen, make sure ping i2b2 works, and 
#the ssh keys have been copied across properly.

#Saj Issa
#20/01/2016


#Define the directories. Note that these need a trailing slash at the 
#end of all of them!
catissue_source_enum="/var/local/brisskit/openspecimen/system/OpenSpecimen-2.4.RC2/tomcat-as/webapps/integration/temp/files/"

i2b2_destination_enum="/var/local/brisskit/i2b2-procedures-2.0-development/ontologies/catissue/ontology-enums"

#i2b2 hostname, almost certainly 'i2b2'
i2b2_hostname="i2b2"

#Copy the pdos
#rsync -avz --delete ${catissue_source_pdo} ${i2b2_hostname}:${i2b2_destination_pdo}

#Copy the enums
rsync -avz --delete ${catissue_source_enum} ${i2b2_hostname}:${i2b2_destination_enum}

