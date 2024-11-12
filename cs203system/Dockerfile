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