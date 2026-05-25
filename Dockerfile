# Stage 1: build
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn package -DskipTests -q

# Stage 2: runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN mkdir -p /uploads/actividad /uploads/instructor /uploads/inscripcion /uploads/constancia

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENV UPLOAD_DIR=/uploads

ENTRYPOINT ["java", "-jar", "app.jar"]
