# techstore-backend/Dockerfile
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/techstore-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]