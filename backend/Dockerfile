FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY build/libs/enterprise-coffee-backend.jar backend.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "backend.jar"]
