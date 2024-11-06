# Use a lightweight Alpine base image with OpenJDK 17
FROM openjdk:21-jdk

# Create a temporary volume
VOLUME /tmp

# Set the working directory in the container
WORKDIR /app

# Accept the JAR file location as a build argument
ARG JAR_FILE

# Copy the JAR file to the container as app.jar
COPY ${JAR_FILE} app.jar

# Expose the application port
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
