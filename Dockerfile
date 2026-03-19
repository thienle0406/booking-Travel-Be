FROM maven:3.8.5-openjdk-8 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests -Dmaven.test.skip=true

FROM eclipse-temurin:8-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

RUN mkdir -p /app/uploads/images

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]