# Use an official Tomcat image with JDK 8
FROM tomcat:9-jdk8-openjdk

# Maintainer (optional)
LABEL maintainer="your-email@example.com"

# Copy the WAR file to Tomcat's webapps directory
# The WAR file is expected to be in the 'target/' directory after 'mvn clean install'
COPY target/legacy-tomcat-app.war /usr/local/tomcat/webapps/legacy-tomcat-app.war

# Expose Tomcat's default HTTP port
EXPOSE 8080

# Command to run Tomcat (default command of the base image)
CMD ["catalina.sh", "run"]
