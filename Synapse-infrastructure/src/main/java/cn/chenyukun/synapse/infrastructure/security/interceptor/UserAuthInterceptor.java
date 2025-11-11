package cn.chenyukun.synapse.infrastructure.security.interceptor;

import cn.chenyukun.synapse.common.annotation.RequireLogin;
import cn.chenyukun.synapse.common.annotation.RequirePermission;
import cn.chenyukun.synapse.common.exception.AuthenticationException;
import cn.chenyukun.synapse.common.exception.ValidationException;
import cn.chenyukun.synapse.infrastructure.security.context.UserContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Optional;

@Component
public class UserAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // Handle @RequireLogin
        RequireLogin requireLogin = handlerMethod.getMethodAnnotation(RequireLogin.class);
        if (requireLogin != null) {
            // Assume user authentication logic here
            // For now, a simple check if UserContext has a user ID
            if (UserContext.getUserId() == null) {
                throw new AuthenticationException("Login required");
            }
        }

        // Handle @RequirePermission
        RequirePermission requirePermission = handlerMethod.getMethodAnnotation(RequirePermission.class);
        if (requirePermission != null) {
            String[] requiredPermissions = requirePermission.value();
            // Assume user permission checking logic here
            // For now, a simple placeholder check
            boolean hasPermission = checkUserPermissions(UserContext.getUserId(), requiredPermissions);
            if (!hasPermission) {
                throw new AuthenticationException("Insufficient permissions");
            }
        }

        return true;
    }

    private boolean checkUserPermissions(Long userId, String[] requiredPermissions) {
        // Placeholder for actual permission checking logic
        // In a real application, this would involve querying a database or an auth service
        if (userId == null) {
            return false;
        }
        // For demonstration, let's say user 1 has 'admin' and 'user' permissions
        if (userId == 1L && Arrays.asList(requiredPermissions).contains("admin")) {
            return true;
        }
        if (Arrays.asList(requiredPermissions).contains("user")) {
            return true;
        }
        return false;
    }
}
