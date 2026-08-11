package com.example.booking_system.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class PermissionMapping {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final List<Rule> rules = new ArrayList<>();

    public PermissionMapping() {
        // Rooms
        add(HttpMethod.GET, "/api/rooms", PermissionCodes.ROOM_VIEW);
        add(HttpMethod.POST, "/api/rooms", PermissionCodes.ROOM_CREATE);
        add(HttpMethod.PATCH, "/api/rooms/*", PermissionCodes.ROOM_UPDATE);

        // Bookings
        addAny(HttpMethod.GET, "/api/bookings",
                PermissionCodes.BOOKING_VIEW_OWN, PermissionCodes.BOOKING_VIEW_ALL);
        add(HttpMethod.POST, "/api/bookings", PermissionCodes.BOOKING_CREATE);
        add(HttpMethod.POST, "/api/bookings/*/approve", PermissionCodes.BOOKING_APPROVE);
        add(HttpMethod.POST, "/api/bookings/*/reject", PermissionCodes.BOOKING_REJECT);
        addAny(HttpMethod.POST, "/api/bookings/*/cancel",
                PermissionCodes.BOOKING_CANCEL_OWN, PermissionCodes.BOOKING_CANCEL_ANY);

        // Account
        add(HttpMethod.GET, "/api/account", PermissionCodes.ACCOUNT_READ);
        add(HttpMethod.PUT, "/api/account", PermissionCodes.ACCOUNT_UPDATE);
        add(HttpMethod.GET, "/api/account/invoices", PermissionCodes.INVOICE_VIEW);
        add(HttpMethod.GET, "/api/account/invoices/export", PermissionCodes.INVOICE_EXPORT);

        // Notifications
        add(HttpMethod.GET, "/api/notifications", PermissionCodes.NOTIFICATION_READ);
        add(HttpMethod.POST, "/api/notifications/*/read", PermissionCodes.NOTIFICATION_MARK_READ);
        add(HttpMethod.POST, "/api/notifications/read-all", PermissionCodes.NOTIFICATION_MARK_READ);

        // Departments (user)
        add(HttpMethod.GET, "/api/departments/user", PermissionCodes.DEPARTMENT_VIEW_OWN);
        add(HttpMethod.GET, "/api/department-change-requests/pending", PermissionCodes.DEPT_CHANGE_VIEW_OWN);
        add(HttpMethod.POST, "/api/department-change-requests", PermissionCodes.DEPT_CHANGE_REQUEST);

        // Admin users
        add(HttpMethod.GET, "/api/admin/users", PermissionCodes.USER_VIEW);
        add(HttpMethod.POST, "/api/admin/users", PermissionCodes.USER_CREATE);
        add(HttpMethod.PUT, "/api/admin/users/*", PermissionCodes.USER_UPDATE);
        add(HttpMethod.PATCH, "/api/admin/users/*/deactivate", PermissionCodes.USER_DEACTIVATE);

        // Admin revenue
        add(HttpMethod.GET, "/api/admin/revenue", PermissionCodes.REVENUE_VIEW);
        add(HttpMethod.GET, "/api/admin/revenue/rooms", PermissionCodes.REVENUE_VIEW);
        add(HttpMethod.GET, "/api/admin/revenue/by-room", PermissionCodes.REVENUE_VIEW);
        add(HttpMethod.GET, "/api/admin/revenue/export", PermissionCodes.REVENUE_EXPORT);

        // Admin department change requests
        add(HttpMethod.GET, "/api/admin/department-change-requests", PermissionCodes.DEPT_CHANGE_VIEW_ALL);
        add(HttpMethod.POST, "/api/admin/department-change-requests/*/approve", PermissionCodes.DEPT_CHANGE_APPROVE);
        add(HttpMethod.POST, "/api/admin/department-change-requests/*/reject", PermissionCodes.DEPT_CHANGE_REJECT);
    }

    public Optional<Set<String>> resolve(HttpServletRequest request) {
        String path = normalizePath(request);
        for (Rule rule : rules) {
            if (rule.method().matches(request.getMethod()) && pathMatcher.match(rule.pattern(), path)) {
                return Optional.of(rule.permissions());
            }
        }
        return Optional.empty();
    }

    private String normalizePath(HttpServletRequest request) {
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }
        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    private void add(HttpMethod method, String pattern, String permission) {
        rules.add(new Rule(method, pattern, Set.of(permission)));
    }

    private void addAny(HttpMethod method, String pattern, String... permissions) {
        rules.add(new Rule(method, pattern, Set.of(permissions)));
    }

    private record Rule(HttpMethod method, String pattern, Set<String> permissions) {
    }
}
