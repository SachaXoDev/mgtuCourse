# Используем ARG как в оригинале, но для многостадийной сборки
ARG RUNTIME_IMAGE=eclipse-temurin:24-jre

# Стадия 1: Сборка приложения
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Стадия 2: Запуск приложения (используем RUNTIME_IMAGE из ARG)
FROM ${RUNTIME_IMAGE}
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
