package id.my.hendisantika.mongomultitenantdemo5.config;

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
 * A thread-local storage for the current tenant ID.
 */
public class TenantContext {
    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    private TenantContext() {
        // Private constructor to prevent instantiation
    }

    /**
     * Set the current tenant ID.
     *
     * @param tenantId the tenant ID to set
     */
    public static void setTenantId(String tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    /**
     * Get the current tenant ID.
     *
     * @return the current tenant ID
     */
    public static String getTenantId() {
        return CURRENT_TENANT.get();
    }

    /**
     * Clear the current tenant ID.
     */
    public static void clear() {
        CURRENT_TENANT.remove();
    }
}