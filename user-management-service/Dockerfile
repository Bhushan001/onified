# Use Eclipse Temurin OpenJDK 21 as base image
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/user-management-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"] 