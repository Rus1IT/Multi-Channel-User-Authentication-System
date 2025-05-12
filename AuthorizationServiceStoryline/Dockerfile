# Используем базовый образ с Java
FROM openjdk:23-jdk-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем JAR-файл в контейнер
COPY build/libs/messageServiceStoryline-0.0.1-SNAPSHOT.jar app.jar

# Указываем команду для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]

# Открываем порт, который используется приложением
EXPOSE 8081
