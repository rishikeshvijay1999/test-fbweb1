FROM tomcat:latest

COPY target/your-app-name.war /usr/local/tomcat/webapps/
