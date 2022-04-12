# Account Transfer API

Account Transfer API is the API backend for [Account Transfer](../frontend). It handles requests from the UI and connects to external services e.g. in Keycloak.

# Prerequisites

Tested with:
* Java 11
* Maven 3.6.3

# Run

To start the application run:
```
./mvnw spring-boot:run
```

To create an executable JAR run:
```
mvn clean package
```

To run the JAR:
```
java -jar target/<jar-file-name>.jar
```

To run using docker:
```
docker build -t account-transfer-api .
docker run -p 9091:9090 account-transfer-api
```

# Swagger/OpenAPI
Swagger/OpenAPI documentation is available at:
* http://localhost:9090/docs/swagger-ui.html (Swagger)
* http://localhost:9090/docs/api-docs (OpenAPI)

# Verification
The skeleton application can be tested by sending a request to:
```
GET http://localhost:9090/accounts/testname


# Logging
Application logs are logged to the console and logs/accounts-transfer-api.log.