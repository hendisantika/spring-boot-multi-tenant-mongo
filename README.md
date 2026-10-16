# MongoDB Multi-Tenant Demo

This is a Spring Boot application that demonstrates multi-tenancy with MongoDB. The application uses the tenant ID from the request header to determine which MongoDB database to connect to.

## Features

- Multi-tenant architecture with tenant ID in request header
- Separate MongoDB database for each tenant
- RESTful API for managing products
- Spring Data MongoDB for data access

## How It Works

1. The application extracts the tenant ID from the `X-TenantID` header in each request.
2. If no tenant ID is provided, it uses a default tenant.
3. The tenant ID is stored in a ThreadLocal variable for the duration of the request.
4. A custom MongoTemplate is created for each request, using the tenant ID as the database name.
5. All MongoDB operations use the tenant-specific database.

## API Endpoints

The application provides the following API endpoints:

- `GET /api/products`: Get all products
- `GET /api/products/{id}`: Get a product by ID
- `GET /api/products/search?name={name}`: Get products by name
- `GET /api/products/low-stock?stock={stock}`: Get products with stock less than the given value
- `POST /api/products`: Create a new product
- `PUT /api/products/{id}`: Update an existing product
- `DELETE /api/products/{id}`: Delete a product by ID
- `GET /api/products/tenant-info`: Get the current tenant ID

## Usage Examples

### Get all products for tenant1

```bash
curl -X GET http://localhost:8080/api/products -H "X-TenantID: tenant1"
```

### Get all products for tenant2

```bash
curl -X GET http://localhost:8080/api/products -H "X-TenantID: tenant2"
```

### Create a product for tenant1

```bash
curl -X POST http://localhost:8080/api/products \
  -H "X-TenantID: tenant1" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Product 1",
    "description": "Description for Product 1",
    "price": 10.99,
    "stock": 100
  }'
```

### Create a product for tenant2

```bash
curl -X POST http://localhost:8080/api/products \
  -H "X-TenantID: tenant2" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Product 1",
    "description": "Description for Product 1",
    "price": 10.99,
    "stock": 100
  }'
```

### Get the current tenant ID

```bash
curl -X GET http://localhost:8080/api/products/tenant-info -H "X-TenantID: tenant1"
```

## Running the Application

1. Start MongoDB:

```bash
docker-compose up -d
```

2. Run the application:

```bash
./gradlew bootRun
```

## Testing Multi-Tenancy

To test the multi-tenant feature, you can:

1. Create products for different tenants using the `X-TenantID` header.
2. Verify that each tenant has its own set of products.
3. Connect to MongoDB and check that separate databases have been created for each tenant.

For example, you can create a product for tenant1, then create a product with the same name for tenant2, and then verify that each tenant has its own product by getting all products for each tenant.# spring-boot-multi-tenant-mongo
