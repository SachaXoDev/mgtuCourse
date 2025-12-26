FROM eclipse-temurin:17

WORKDIR /app

# Копируем JAR файл
COPY target/project-1.0.jar app.jar

# Правильная точка входа - запускаем Java напрямую
ENTRYPOINT ["java", "-jar", "app.jar"]
