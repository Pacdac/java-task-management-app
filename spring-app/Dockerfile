# Use an official Maven image for building
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the Maven POM and source code
COPY . .

# Make the Maven wrapper executable
RUN chmod +x ./mvnw

# Skip tests during Docker build to avoid database connection issues
RUN ./mvnw clean package -DskipTests

# Use OpenJDK for the runtime image 
FROM eclipse-temurin:17-jre-jammy

# Set the working directory in the container
WORKDIR /app

# Copy the jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
