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
# Etapa 1: Construcción de la aplicación
#################################################
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# Establecer directorio de trabajo
WORKDIR /cognite

# Copiar solo el archivo pom.xml y descargar dependencias
COPY backend/pom.xml ./
RUN mvn dependency:go-offline

# Copiar el código fuente después
COPY backend/src ./src

# Compilar la aplicación sin ejecutar pruebas
RUN mvn clean package -DskipTests=true


#################################################
# Etapa 2: Imagen final con el JAR compilado
#################################################
FROM openjdk:17.0.2 AS runtime

# Definir variables para mayor flexibilidad
ARG JAR_NAME=backend-0.0.1-SNAPSHOT.jar
ENV APP_HOME=/cognite

# Establecer directorio de trabajo
WORKDIR $APP_HOME

# Copiar el JAR generado en la fase de construcción
COPY --from=builder /gymguru/target/$JAR_NAME app.jar

# Exponer el puerto de la aplicación (configurable)
EXPOSE 8443

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]

