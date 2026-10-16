package id.my.hendisantika.mongomultitenantdemo5.config;

import com.mongodb.client.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.web.context.WebApplicationContext;

/**
 * Configuration class for multi-tenant MongoDB connections.
 */
@Configuration
public class MultiTenantMongoConfig {

    @Autowired
    private MongoClient mongoClient;

    /**
     * Creates a request-scoped MongoTemplate that uses the tenant ID from TenantContext
     * to determine which database to connect to.
     *
     * @return a tenant-aware MongoTemplate
     */
    @Value("${multitenancy.default-tenant:default}")
    private String defaultTenant;

    @Bean
    @Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
    public MongoTemplate mongoTemplate() {
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            tenantId = defaultTenant;
        }
        return new MongoTemplate(new SimpleMongoClientDatabaseFactory(mongoClient, tenantId));
    }
}
