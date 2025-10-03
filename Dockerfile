# Use official Gradle image to build the application
FROM gradle:8.4.0-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle build --no-daemon

# Use a minimal Java image to run the application
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]

