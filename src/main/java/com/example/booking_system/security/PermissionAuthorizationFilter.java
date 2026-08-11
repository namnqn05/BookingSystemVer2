package com.example.booking_system.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PermissionAuthorizationFilter extends OncePerRequestFilter {

    private final PermissionMapping permissionMapping;
    private final ObjectMapper objectMapper;

    public PermissionAuthorizationFilter(PermissionMapping permissionMapping, ObjectMapper objectMapper) {
        this.permissionMapping = permissionMapping;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return HttpMethod.OPTIONS.matches(request.getMethod())
                || path.startsWith("/api/auth/")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.equals("/swagger-ui.html");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Optional<Set<String>> requiredPermissions = permissionMapping.resolve(request);

        if (requiredPermissions.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized", "Authentication required");
            return;
        }

        Set<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        boolean allowed = requiredPermissions.get().stream().anyMatch(authorities::contains);
        if (!allowed) {
            writeError(response, HttpServletResponse.SC_FORBIDDEN, "Forbidden",
                    "Missing required permission: " + String.join(" or ", requiredPermissions.get()));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void writeError(HttpServletResponse response, int status, String error, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status);
        body.put("error", error);
        body.put("message", message);
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
