# Similar Products API

## Description

This project exposes a REST API to retrieve products similar to a given product.

## Requirements

- Java 21
- Gradle 8.0 or later
- Docker

## Build and Run

### Building the Application

1. **Clean:**

   ```bash
   ./gradlew clean
   ```

2. **Build:**

   ```bash
   ./gradlew bootJar
   ```

3. **Run from JAR:**

   ```bash
   java -jar build/libs/pruebatenica-0.0.1.jar
   ```

## Test

1. **Run the following command in the folder that was provided with the exercise:**

   ```bash
   docker-compose up -d simulado influxdb grafana
   ```

2. **Run tests locally:**

   ```bash
   ./gradlew test
   ```

