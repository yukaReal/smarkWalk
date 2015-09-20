webServerFolderFullPath=~/warehouse/programms/tomcat/8.0.23
projectHome=/Users/yankopy/warehouse/projects/eclipseWorkspace_sandbox/smartWalk3
appName=mytomcatapp-0.1

mvn clean package

$webServerFolderFullPath/bin/shutdown.sh

rm -R $webServerFolderFullPath/webapps/$appName
rm $webServerFolderFullPath/webapps/$appName.war

cp $projectHome/target/$appName.war $webServerFolderFullPath/webapps

$webServerFolderFullPath/bin/startup.sh

echo Done.
