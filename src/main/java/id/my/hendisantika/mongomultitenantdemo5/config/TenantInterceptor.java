package id.my.hendisantika.mongomultitenantdemo5.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Interceptor to extract tenant ID from request headers and set it in the TenantContext.
 */
@Component
public class TenantInterceptor implements HandlerInterceptor {

    @Value("${multitenancy.tenant-header:X-TenantID}")
    private String tenantHeader;

    @Value("${multitenancy.default-tenant:default}")
    private String defaultTenant;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String tenantId = request.getHeader(tenantHeader);

        // If no tenant ID is provided, use the default tenant
        if (tenantId == null || tenantId.isEmpty()) {
            tenantId = defaultTenant;
        }

        TenantContext.setTenantId(tenantId);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        // Not used
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // Clear the tenant ID after the request is completed
        TenantContext.clear();
    }
}
