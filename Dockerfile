FROM openjdk:8-jdk-alpine
COPY target/testjavasdk-1.0-SNAPSHOT-with-deps.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]