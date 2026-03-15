# Step 1: Use a small, secure version of Java 21
FROM eclipse-temurin:21-jdk-alpine

# Step 2: Create a spot for temporary files
VOLUME /tmp

# Step 3: Copy the JAR file from the 'target' folder into the container
# Note: 'mvn clean verify' creates this JAR in the pipeline
COPY target/*.jar app.jar

# Step 4: Tell the container to run the JAR when it starts
ENTRYPOINT ["java", "-jar", "/app.jar"]