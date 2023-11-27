FROM gradle:8.4-jdk21 as builder

WORKDIR /app

COPY build.gradle .
COPY settings.gradle .
COPY gradle ./gradle
COPY src ./src

RUN gradle build --no-daemon -x test

# Application Stage
FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]