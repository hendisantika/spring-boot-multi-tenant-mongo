package id.my.hendisantika.mongomultitenantdemo5.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-multi-tenant-mongo
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 16/10/26
 * Time: 13.21
 */
/**
 * Configuration class for multi-tenant MongoDB connections.
 */
@Configuration
public class MultiTenantMongoConfig {

    @Value("${multitenancy.default-tenant:default}")
    private String defaultTenant;

    /**
     * Creates a MongoTemplate whose database factory resolves the database from TenantContext
     * on every call, so each operation is routed to the current tenant's database.
     * <p>
     * The factory is deliberately not exposed as a bean: Spring Boot only auto-configures
     * the MongoClient when no MongoDatabaseFactory bean is present.
     *
     * @param mongoClient the shared MongoDB client
     * @return a tenant-aware MongoTemplate
     */
    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        MongoDatabaseFactory tenantAwareFactory = new SimpleMongoClientDatabaseFactory(mongoClient, defaultTenant) {
            @Override
            public MongoDatabase getMongoDatabase() throws DataAccessException {
                String tenantId = TenantContext.getTenantId();
                return getMongoDatabase(tenantId != null ? tenantId : defaultTenant);
            }
        };
        return new MongoTemplate(tenantAwareFactory);
    }
}
