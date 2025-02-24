FROM openjdk:22-jdk-slim

WORKDIR /app

COPY target/UniMapAPI-0.0.1-SNAPSHOT.jar /app/UniMapAPI.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "UniMapAPI.jar"]