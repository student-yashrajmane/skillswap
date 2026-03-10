# Use official Eclipse Temurin OpenJDK 17 image
FROM eclipse-temurin:17-jdk

# Set working directory inside container
WORKDIR /app

# Copy the built JAR file into the container
COPY target/SkillSwap-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your application listens on
EXPOSE 5051

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
