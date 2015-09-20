serverName=52.26.75.208

#scp -r -i keys/Hackathon.pem /Users/yankopy/warehouse/programms/tomcat/8.0.23.zip  ubuntu@$serverName:/home/ubuntu

scp -r -i keys/Hackathon.pem /Users/yankopy/warehouse/projects/eclipseWorkspace_sandbox/smartWalk3/target/mytomcatapp-0.1.war ubuntu@$serverName:/home/ubuntu



#scp -r -i keys/Hackathon.pem /Users/yankopy/warehouse/to/hackatone/2015.09.Stamford/data/PoliceIncidents/* ubuntu@$serverName:/home/ubuntu/PoliceIncidents/
