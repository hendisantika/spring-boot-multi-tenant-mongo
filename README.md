# Spring Boot Multi-Tenant MongoDB

[![CI](https://github.com/hendisantika/spring-boot-multi-tenant-mongo/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/hendisantika/spring-boot-multi-tenant-mongo/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A Spring Boot application that demonstrates **database-per-tenant** multi-tenancy with MongoDB. Each request carries a
tenant ID in an HTTP header, and the application routes all MongoDB operations to a database named after that tenant.

## Features

- Multi-tenant architecture driven by the `X-TenantID` request header
- Separate MongoDB database for each tenant
- RESTful CRUD API for products
- Spring Data MongoDB for data access
- Docker Compose integration: MongoDB starts automatically with the app

## Tech Stack

| Component       | Version            |
|-----------------|--------------------|
| Java            | 21                 |
| Spring Boot     | 4.1.1              |
| MongoDB         | 9.0.2 (Docker)     |
| Build tool      | Gradle (wrapper)   |
| Lombok          | managed by Boot    |

## Prerequisites

- **JDK 21**: check with `java -version`
- **Docker** with Docker Compose: check with `docker compose version`
- **curl** (or Postman/HTTPie) to call the API

You do not need to install Gradle. The project ships with the Gradle wrapper (`./gradlew`).

## Project Structure

```
src/main/java/id/my/hendisantika/mongomultitenantdemo5
├── MongoMultitenantDemo5Application.java   # Spring Boot entry point
├── config
│   ├── MultiTenantMongoConfig.java         # Tenant-aware MongoTemplate
│   ├── TenantContext.java                  # ThreadLocal holder for the tenant ID
│   ├── TenantInterceptor.java              # Reads X-TenantID header per request
│   └── WebMvcConfig.java                   # Registers the interceptor
├── controller
│   └── ProductController.java              # REST endpoints
├── model
│   └── Product.java                        # MongoDB document
├── repository
│   └── ProductRepository.java              # Spring Data repository
└── service
    └── ProductService.java                 # Business logic
```

## Setup

### 1. Clone the repository

```bash
git clone https://github.com/hendisantika/spring-boot-multi-tenant-mongo.git
cd spring-boot-multi-tenant-mongo
```

### 2. Review the configuration

`src/main/resources/application.properties`:

```properties
spring.data.mongodb.uri=mongodb://yu71:53cret@localhost:27017/
spring.data.mongodb.database=default
spring.data.mongodb.auto-index-creation=true

multitenancy.tenant-header=X-TenantID
multitenancy.default-tenant=default
```

| Property                       | Description                                                   |
|--------------------------------|---------------------------------------------------------------|
| `multitenancy.tenant-header`   | HTTP header that carries the tenant ID                        |
| `multitenancy.default-tenant`  | Tenant (database) used when the header is missing or empty    |

The MongoDB credentials are defined in `compose.yaml` and must match the URI above.

## Running the Application

### Option A: Let Spring Boot start MongoDB (recommended)

The project includes `spring-boot-docker-compose`. When you run the app in development mode, Spring Boot runs
`compose.yaml`, waits for MongoDB to become ready, and connects to it automatically. No manual Docker step is needed.

```bash
./gradlew bootRun
```

The API is available at `http://localhost:8080`.

### Option B: Run MongoDB yourself

`compose.yaml` publishes MongoDB on a **random** host port, so the fixed `localhost:27017` URI only works if you start
MongoDB on that port yourself:

```bash
docker run -d --name mongodb -p 27017:27017 \
  -e MONGO_INITDB_ROOT_USERNAME=yu71 \
  -e MONGO_INITDB_ROOT_PASSWORD=53cret \
  mongo:9.0.2-noble
```

Then disable the Compose integration and start the app:

```bash
./gradlew bootRun --args='--spring.docker.compose.enabled=false'
```

### Build an executable JAR

```bash
./gradlew clean build
java -jar build/libs/mongo-multitenant-demo5-0.0.1-SNAPSHOT.jar
```

The packaged JAR does not include the development-only Docker Compose support, so MongoDB must already be running
(see Option B).

### Run the tests

```bash
./gradlew test
```

The integration tests in `TenantIsolationIntegrationTests` start a throwaway MongoDB container with Testcontainers, so
**Docker must be running**. They check that each tenant reads and writes only its own database, and that requests
without the `X-TenantID` header fall back to the default tenant.

## How It Works

1. `TenantInterceptor` reads the tenant ID from the `X-TenantID` header on every request.
2. If the header is missing or empty, it falls back to `multitenancy.default-tenant`.
3. The tenant ID is stored in `TenantContext`, a `ThreadLocal`, for the duration of the request, and cleared afterwards.
4. `MultiTenantMongoConfig` builds a `MongoTemplate` whose database name is the current tenant ID.
5. Repository operations go through that template, so each tenant's data lives in its own database.

## API Reference

Base URL: `http://localhost:8080/api/products`. Send `X-TenantID: <tenant>` with every request.

| Method   | Endpoint                          | Description                               |
|----------|-----------------------------------|-------------------------------------------|
| `GET`    | `/api/products`                   | List all products                         |
| `GET`    | `/api/products/{id}`              | Get a product by ID (404 if not found)    |
| `GET`    | `/api/products/search?name=`      | Find products by exact name               |
| `GET`    | `/api/products/low-stock?stock=`  | Find products with stock below a value    |
| `POST`   | `/api/products`                   | Create a product (201 Created)            |
| `PUT`    | `/api/products/{id}`              | Update a product (404 if not found)       |
| `DELETE` | `/api/products/{id}`              | Delete a product (204 No Content)         |
| `GET`    | `/api/products/tenant-info`       | Show the tenant resolved for the request  |

### Product payload

```json
{
  "name": "Product 1",
  "description": "Description for Product 1",
  "price": 10.99,
  "stock": 100
}
```

`id` is generated by MongoDB on create.

## Usage Examples

### Check which tenant is resolved

```bash
curl http://localhost:8080/api/products/tenant-info -H "X-TenantID: tenant1"
# Current tenant: tenant1
```

### Create a product

```bash
curl -X POST http://localhost:8080/api/products \
  -H "X-TenantID: tenant1" \
  -H "Content-Type: application/json" \
  -d '{"name": "Laptop", "description": "14-inch laptop", "price": 999.99, "stock": 10}'
```

### List products

```bash
curl http://localhost:8080/api/products -H "X-TenantID: tenant1"
```

### Get, update and delete by ID

```bash
curl http://localhost:8080/api/products/{id} -H "X-TenantID: tenant1"

curl -X PUT http://localhost:8080/api/products/{id} \
  -H "X-TenantID: tenant1" \
  -H "Content-Type: application/json" \
  -d '{"name": "Laptop", "description": "14-inch laptop", "price": 899.99, "stock": 8}'

curl -X DELETE http://localhost:8080/api/products/{id} -H "X-TenantID: tenant1"
```

### Search and filter

```bash
curl "http://localhost:8080/api/products/search?name=Laptop" -H "X-TenantID: tenant1"
curl "http://localhost:8080/api/products/low-stock?stock=20" -H "X-TenantID: tenant1"
```

## Verifying Tenant Isolation

1. Create a product for `tenant1` and a different product for `tenant2`.
2. List products with `X-TenantID: tenant1`, then with `X-TenantID: tenant2`. Each list should contain only that
   tenant's products.
3. Inspect the databases directly:

```bash
docker exec -it $(docker ps -qf ancestor=mongo:9.0.2-noble) \
  mongosh -u yu71 -p 53cret --authenticationDatabase admin --eval "show dbs"
```

You should see a `tenant1` database and a `tenant2` database, each with a `products` collection.

## Author

**Hendi Santika**

- Email: hendisantika@gmail.com
- Telegram: [@hendisantika34](https://t.me/hendisantika34)

## License

This project is licensed under the [MIT License](LICENSE).
