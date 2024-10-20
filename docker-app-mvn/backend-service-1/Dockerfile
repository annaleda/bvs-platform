# Fase di build
FROM maven:3.8.6-openjdk-11 AS build

# Imposta la directory di lavoro
WORKDIR /app

# Copia il file pom.xml e scarica le dipendenze
COPY pom.xml .
#RUN mvn dependency:go-offline
COPY src ./src

# Esegui la build dell'applicazione (escludendo i test)
RUN mvn clean package -DskipTests

# Fase di runtime
FROM openjdk:11-jdk-slim

# Imposta la directory di lavoro
WORKDIR /app

# Copia il JAR dal primo stage
COPY --from=build /app/target/backend-service-1-0.0.1-SNAPSHOT.jar app.jar

# Esponi le porte necessarie
EXPOSE 8080 5005

# Avvia l'applicazione in modalità debug
CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "app.jar"]
