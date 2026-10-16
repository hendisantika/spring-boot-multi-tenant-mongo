package id.my.hendisantika.mongomultitenantdemo5;

import id.my.hendisantika.mongomultitenantdemo5.model.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-multi-tenant-mongo
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 16/10/26
 * Time: 13.33
 */
/**
 * Integration tests that verify each tenant's data is stored in, and read from, its own database.
 */
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TenantIsolationIntegrationTests {

    private static final String TENANT_HEADER = "X-TenantID";

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:9.0.2-noble");

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @BeforeEach
    void setUp() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultStatusHandler(status -> true, (request, response) -> {
                    // Let tests assert on 4xx/5xx responses instead of throwing
                })
                .build();
    }

    @Test
    void productsAreVisibleOnlyToTheTenantThatCreatedThem() {
        String tenantA = uniqueTenant();
        String tenantB = uniqueTenant();

        Product laptop = create(tenantA, "Laptop");
        Product phone = create(tenantB, "Phone");

        assertThat(findAll(tenantA)).extracting(Product::getId).containsExactly(laptop.getId());
        assertThat(findAll(tenantB)).extracting(Product::getId).containsExactly(phone.getId());
    }

    @Test
    void productCannotBeReadOrDeletedByAnotherTenant() {
        String owner = uniqueTenant();
        String other = uniqueTenant();
        Product product = create(owner, "Keyboard");

        ResponseEntity<Product> readByOther = exchange(HttpMethod.GET, "/api/products/" + product.getId(), other, null, Product.class);
        assertThat(readByOther.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        exchange(HttpMethod.DELETE, "/api/products/" + product.getId(), other, null, Void.class);

        ResponseEntity<Product> readByOwner = exchange(HttpMethod.GET, "/api/products/" + product.getId(), owner, null, Product.class);
        assertThat(readByOwner.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void requestWithoutTenantHeaderUsesDefaultTenant() {
        String tenant = uniqueTenant();
        create(tenant, "Monitor");

        ResponseEntity<Product[]> response = restClient.get().uri("/api/products").retrieve().toEntity(Product[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).extracting(Product::getName).doesNotContain("Monitor");
        assertThat(restClient.get().uri("/api/products/tenant-info").retrieve().body(String.class))
                .isEqualTo("Current tenant: default");
    }

    private Product create(String tenant, String name) {
        Product product = new Product(null, name, name + " description", new BigDecimal("10.99"), 5);
        ResponseEntity<Product> response = exchange(HttpMethod.POST, "/api/products", tenant, product, Product.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response.getBody();
    }

    private Product[] findAll(String tenant) {
        return exchange(HttpMethod.GET, "/api/products", tenant, null, Product[].class).getBody();
    }

    private <T> ResponseEntity<T> exchange(HttpMethod method, String url, String tenant, Object body, Class<T> type) {
        RestClient.RequestBodySpec request = restClient.method(method).uri(url).header(TENANT_HEADER, tenant);
        if (body != null) {
            request.contentType(MediaType.APPLICATION_JSON).body(body);
        }
        return request.retrieve().toEntity(type);
    }

    private static String uniqueTenant() {
        return "tenant_" + UUID.randomUUID().toString().replace("-", "");
    }
}
