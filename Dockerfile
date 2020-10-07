# escape=`
#
# Build stage:  building and packaging app into war via mavaen
#
FROM maven:3.6.3-jdk-11-slim
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

#
# Deploy stage: deploying app onto the WildFly image
#
FROM jboss/wildfly
COPY --from=0 /home/app/target/EnvVarsService-0.1.war /opt/jboss/wildfly/standalone/deployments/
EXPOSE 8080