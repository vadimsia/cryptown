FROM maven:3.6.0-jdk-11-slim AS build
COPY plugin/src /home/app/src
COPY plugin/pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

#
# Package stage
#
FROM openjdk:17-slim
COPY server /home/app
COPY --from=build /home/app/target/WorldManager-1.0.jar /home/app/plugins/WorldManager-1.0.jar

WORKDIR /home/app

ENTRYPOINT ["java","-jar","spigot-1.18.2.jar"]