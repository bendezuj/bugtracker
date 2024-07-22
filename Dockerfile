FROM openjdk:17-jdk-slim
VOLUME /tmp
ARG JAR_FILE=target/bugtracker-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} bugtracker.jar
ENTRYPOINT ["java","-jar","/bugtracker.jar"]

