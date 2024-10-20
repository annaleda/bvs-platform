# Fase di build
FROM gradle:7.6.0-jdk17 AS build

# Imposta la directory di lavoro
WORKDIR /app

# Copia il file build.gradle e il file settings.gradle
COPY build.gradle settings.gradle ./

# Copia la cartella 'src'
COPY src ./src

# Esegui la build dell'applicazione
RUN gradle build -x test

# Fase di runtime
FROM openjdk:17-jdk-slim

# Imposta la directory di lavoro
WORKDIR /app

# Copia il JAR dal primo stage
COPY --from=build /app/build/libs/backend-service-*.jar backend-service.jar
EXPOSE 8080
CMD ["java", "-jar", "backend-service.jar"]
