#################################################
# Base image for the builder container
#################################################
FROM  node:lts-alpine3.19 as builderFront

#ENV TZ=Europe/Madrid

WORKDIR /frontend

COPY ../frontend/package*.json ../frontend/angular.json ../frontend/tsconfig*.json  /frontend/

RUN npm install 

COPY ../frontend/src /frontend/src

RUN npm run build -- --configuration production --base-href="/new/"

#################################################
# Base image for the build container
#################################################
FROM  maven:3.9.6-eclipse-temurin-21 as builder

# Sets the working directory for commands to run
WORKDIR /demo

# Copies the project's dependencies
COPY ../demo/pom.xml ./pom.xml

# Downloads the project's dependencies
#RUN mvn clean verify 

# Copies the project's code
COPY ../demo/src ./src

# Compiles the project
RUN mvn clean package -DskipTests=true


#################################################
# Base image for the application container
#################################################
FROM openjdk:21-jdk AS runtime

# Sets the working directory where the JAR is located
WORKDIR /demo

# Copies the JAR from the build container
COPY --from=builder ../demo/target/demo-0.0.1-SNAPSHOT.jar .

# Indicates the port that the container exposes
EXPOSE 8443

# Command to run on docker run
CMD ["java", "-jar", "demo-0.0.1-SNAPSHOT.jar"]

