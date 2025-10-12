# --- Stage 1: Build the application with Maven ---
# Use a Maven image that includes a JDK to build the project
FROM maven:3.8.5-openjdk-17 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml file to download dependencies first (for caching)
COPY pom.xml .

# Copy the rest of the source code
COPY src ./src

# Run the Maven package command to build the project and create the .jar file
# The -DskipTests flag is used to speed up the build process in CI/CD environments
RUN mvn clean package -DskipTests


# --- Stage 2: Create the final, lightweight image ---
# Use a slim Java runtime image, which is smaller and more secure
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy ONLY the generated .jar file from the 'build' stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080 so the application can be accessed
EXPOSE 8080

# The command to run the application
ENTRYPOINT ["java","-jar","app.jar"]

