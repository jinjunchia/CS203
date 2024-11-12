## Use a lightweight Alpine base image with OpenJDK 17
#FROM openjdk:21-jdk
#
## Create a temporary volume
#VOLUME /tmp
#
## Set the working directory in the container
#WORKDIR /app
#
## Accept the JAR file location as a build argument
#ARG JAR_FILE
#
## Copy the JAR file to the container as app.jar
#COPY ${JAR_FILE} app.jar
#
## Expose the application port
#EXPOSE 8080
#
## Command to run the application
#ENTRYPOINT ["java", "-jar", "app.jar"]

# Stage 1: Build the application
#FROM gradle:7.6-jdk17 AS build
#WORKDIR /app
#COPY build.gradle .
#COPY settings.gradle .
#COPY src ./src
#RUN gradle build --info
#
## Stage 2: Run the application
#FROM openjdk:17-alpine
#WORKDIR /app
#
##RUN echo ${PWD} && ls -lR
#RUN ls -al /app
##RUN ls -l /app/build
## Copy the JAR from Gradle's output directory (build/libs)
#COPY --from=build /app/aws-0.0.1-SNAPSHOT.jar ./demo.jar

#EXPOSE 8080
#CMD ["java", "-jar", "demo.jar"]

# Stage 1: Build the application using Gradle
#FROM openjdk:17-jdk-slim as builder
#
#WORKDIR /app
#COPY build.gradle .
#COPY settings.gradle .
#COPY src ./src
#RUN gradle build --info -x test
#
## Debug step: Check if the JAR file is present after build
#RUN ls -al /app/build/libs
#
## Stage 2: Run the application using OpenJDK
#FROM openjdk:17-alpine
#WORKDIR /app
#
## Copy the JAR file from Gradle's output directory (build/libs)
#COPY --from=build /app/build/libs/aws-0.0.1-SNAPSHOT.jar ./demo.jar
#
#EXPOSE 8080
#CMD ["java", "-jar", "demo.jar"]

# Use an official OpenJDK runtime as the base image
FROM openjdk:17-jdk-slim as builder

# Set the working directory in the container
WORKDIR /app

# Copy the Gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Make the gradlew script executable
RUN chmod +x gradlew

# Download Gradle dependencies (use a local cache to speed up future builds)
RUN ./gradlew --no-daemon dependencies

# Copy the whole source code into the container
COPY . .

# Build the application
RUN ./gradlew build --no-daemon

# Second stage: Create the runtime image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the built jar from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the port your application runs on (default Spring Boot port is 8080)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]



