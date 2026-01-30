FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

RUN mkdir -p /app/src/main/resources

COPY --from=build /app/target/InkSolAgent-1.0.jar app.jar

COPY src/main/resources src/main/resources
COPY .env /app/.env

ENTRYPOINT ["java", "-jar", "app.jar"]