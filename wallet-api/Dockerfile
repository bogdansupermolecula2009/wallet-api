
FROM openjdk:17-jdk-slim


WORKDIR /wallet-api


COPY target/wallet-api-0.0.1-SNAPSHOT.jar app.jar


ENTRYPOINT ["java", "-jar", "app.jar"]

