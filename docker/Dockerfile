# # Usamos una imagen base de Java
# FROM openjdk:21-jdk

# # Establecemos el directorio de trabajo dentro del contenedor
# WORKDIR /app

# # Copiamos el archivo JAR desde el directorio target dentro de demo
# COPY target/demo-0.0.1-SNAPSHOT.jar /app/app.jar

# # Exponemos el puerto en el que la aplicación va a correr (por defecto 8080 para Spring Boot)
# EXPOSE 8080

# # Comando para ejecutar la aplicación
# ENTRYPOINT ["java", "-jar", "/app/app.jar"]


#################################################
# Stage 1: Build rhe application
#################################################
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# Set the working directory
WORKDIR /cognite

# Copy only the file  pom.xml y download dependencies
COPY backend/pom.xml ./
RUN mvn dependency:go-offline

# Copy the source code afterwards to avoid re-downloading dependencies
COPY backend/src ./src

# Build the application without running tests 
RUN mvn clean package -DskipTests=true


#################################################
# Stage 2: Final image with the compiled JAR
#################################################
FROM openjdk:17.0.2 AS runtime

# Define variables for greater flexibility
ARG JAR_NAME=backend-0.0.1-SNAPSHOT.jar
ENV APP_HOME=/cognite

# Set the working directory
WORKDIR $APP_HOME

# Copy the JAR file generated during the build stage
COPY --from=builder /cognite/target/$JAR_NAME app.jar

# Expose the application port (configurable)
EXPOSE 8443

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

