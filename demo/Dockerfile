# Usamos una imagen base de Java
FROM openjdk:21-jdk

# Establecemos el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el archivo JAR desde el directorio target dentro de demo
COPY target/demo-0.0.1-SNAPSHOT.jar /app/app.jar

# Exponemos el puerto en el que la aplicación va a correr (por defecto 8080 para Spring Boot)
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
