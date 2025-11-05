# Для dev образа
FROM openjdk:17-jdk-alpine AS build  # Если нужно build, но т.к. JAR готов — можно просто runtime
WORKDIR /app
RUN apt-get update && apt-get install -y curl

# Копируем JAR (собранный mvn package)
COPY target/Authorization_service-0.0.1-SNAPSHOT.jar app.jar

# Runtime stage (минимальный)
FROM openjdk:17-jre-alpine
WORKDIR /app
COPY --from=build /app/app.jar app.jar

EXPOSE 8080
# Запуск с dev профилем (env в compose переопределит, но базово)
ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "/app.jar"]