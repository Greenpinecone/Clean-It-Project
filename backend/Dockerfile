# syntax=docker/dockerfile:1
FROM eclipse-temurin:25-jdk AS build

WORKDIR /workspace

COPY pom.xml mvnw ./
COPY .mvn/ .mvn/

RUN chmod +x ./mvnw
COPY src/ src/

RUN ./mvnw clean package -DskipTests


FROM eclipse-temurin:25-jre AS final

WORKDIR /app

COPY --from=build /workspace/target/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
