# --- Stage 1: Build the React Frontend ---
FROM node:20-slim AS frontend-build
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm install
COPY frontend ./
RUN npm run build

# --- Stage 2: Build the Spring Boot application ---
FROM maven:3.8.5-openjdk-17 AS backend-build
WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src

# Create the static directory if it doesn't exist and copy the built frontend
RUN mkdir -p src/main/resources/static
COPY --from=frontend-build /app/frontend/dist ./src/main/resources/static/

# Run the Maven package command
RUN mvn clean package -DskipTests

# --- Stage 3: Create the final, lightweight, production-ready image ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy ONLY the generated .jar file from the 'backend-build' stage
COPY --from=backend-build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
