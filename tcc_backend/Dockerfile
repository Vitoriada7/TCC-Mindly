FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml ./
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
RUN groupadd --system mindly && useradd --system --gid mindly mindly
COPY --from=build --chown=mindly:mindly /app/target/tcc_backend-0.0.1-SNAPSHOT.jar app.jar
USER mindly
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
