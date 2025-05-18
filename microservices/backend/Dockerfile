FROM openjdk:17-jdk-slim

# Install mysql client
RUN apt-get update && apt-get install -y default-mysql-client && rm -rf /var/lib/apt/lists/*

COPY target/app.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
