FROM gradle:jdk17-alpine AS build
WORKDIR /app
COPY . .
run gradle build --no-daemon

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=build /app/build/libs/*.jar /app/transaction-service.jar

EXPOSE 8084

CMD ["java", "-jar", "/app/transaction-service.jar"]