# build stage
FROM maven:3.9.11-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY ride-sharing-backend/ ./ride-sharing-backend/
RUN mvn -f ride-sharing-backend/pom.xml package -DskipTests


# run stage
FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/ride-sharing-backend/target/ride-sharing-backend-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]