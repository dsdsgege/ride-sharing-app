# build stage
FROM maven:3.9.11-eclipse-temurin-11-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests -X


# run stage
FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=build target/ride-sharing-app-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]