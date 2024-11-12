# Use a lightweight Alpine base image with OpenJDK 17
FROM bellsoft/liberica-openjdk-alpine:17

# Create a temporary volume
VOLUME /tmp

# Set the working directory in the container
WORKDIR /app

# Accept the JAR file location as a build argument
ARG JAR_FILE

# Copy the JAR file to the container as app.jar
COPY ./build/libs/cs203system-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

# ------------------- OLD CONFIG FOR UBUNTU COMPUTERS ---------------------

## Use an official OpenJDK runtime as the base image
#FROM openjdk:17-jdk-slim as builder
#
## Set the working directory in the container
#WORKDIR /app
#
## Copy the Gradle wrapper and build files
#COPY gradlew .
#COPY gradle gradle
#COPY build.gradle .
#COPY settings.gradle .
#
## Make the gradlew script executable
#RUN chmod +x gradlew
#
## Download Gradle dependencies (use a local cache to speed up future builds)
#RUN ./gradlew --no-daemon dependencies
#
## Copy the whole source code into the container
#COPY . .
#
## Build the application
#RUN ./gradlew build --no-daemon
#
## Second stage: Create the runtime image
#FROM openjdk:17-jdk-slim
#
## Set the working directory in the container
#WORKDIR /app
#
## Copy the built jar from the builder stage
#COPY --from=builder /app/build/libs/*.jar app.jar
#
## Expose the port your application runs on (default Spring Boot port is 8080)
#EXPOSE 8080
#
## Run the application
#ENTRYPOINT ["java", "-jar", "app.jar"]