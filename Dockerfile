# techstore-backend/Dockerfile
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src src
RUN mvn -B -q -DskipTests package

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
RUN useradd --system --uid 10001 techstore
COPY --from=build /workspace/target/techstore-*.jar app.jar
USER techstore
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
