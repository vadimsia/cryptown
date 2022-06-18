FROM openjdk:17-jdk-alpine

COPY . /home/gradle/src
WORKDIR /home/gradle/src
RUN ./gradlew build --no-daemon 

#WORKDIR /
RUN mkdir /app

COPY build/libs/demo-0.0.1-SNAPSHOT.jar /app/application.jar

ENTRYPOINT ["java", "-jar", "/app/application.jar"]