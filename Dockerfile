# Etap budowania aplikacji
FROM maven:3.8.4-openjdk-18 AS build

# Ustaw katalog roboczy dla Maven
WORKDIR /app

# Skopiuj plik POM i zależności (aby móc pobrać zależności przed skopiowaniem całego projektu)
COPY pom.xml .

# Pobierz zależności
RUN mvn dependency:go-offline -B

# Skopiuj resztę kodu źródłowego do kontenera
COPY src ./src

# Skompiluj aplikację jako plik JAR
RUN mvn clean package -DskipTests

# Etap uruchamiania aplikacji
FROM openjdk:18-jdk-alpine

# Ustaw katalog roboczy w kontenerze
WORKDIR /app

# Skopiuj skompilowany plik JAR z etapu budowania
COPY --from=build /app/target/*.jar /app/app.jar

# Otwórz port 8080
EXPOSE 8080

# Uruchom aplikację Spring Boot
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
