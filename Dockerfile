# --- Этап 1: Сборка приложения с помощью Maven ---
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

# Сначала копируем только pom.xml, чтобы закэшировать зависимости
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Теперь копируем исходный код и собираем проект
COPY src ./src
RUN mvn clean package -DskipTests

# --- Этап 2: Запуск приложения ---
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Копируем собранный JAR из первого этапа
# Обратите внимание: имя JAR-файла зависит от artifactId и version в вашем pom.xml
# У вас artifactId=demo, version=0.0.1-SNAPSHOT, поэтому имя будет demo-0.0.1-SNAPSHOT.jar
COPY --from=builder /build/target/demo-0.0.1-SNAPSHOT.jar app.jar

# Открываем порт, который слушает ваше приложение
EXPOSE 8080

# Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]