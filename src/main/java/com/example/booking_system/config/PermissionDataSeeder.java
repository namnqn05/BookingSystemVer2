package com.example.booking_system.config;

import com.example.booking_system.model.Permission;
import com.example.booking_system.model.Role;
import com.example.booking_system.model.RolePermission;
import com.example.booking_system.repository.PermissionRepository;
import com.example.booking_system.repository.RolePermissionRepository;
import com.example.booking_system.security.PermissionCodes;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class PermissionDataSeeder implements ApplicationRunner {

    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    public PermissionDataSeeder(PermissionRepository permissionRepository,
                                RolePermissionRepository rolePermissionRepository) {
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Map<String, String> definitions = permissionDefinitions();
        for (Map.Entry<String, String> entry : definitions.entrySet()) {
            permissionRepository.findByCode(entry.getKey()).orElseGet(() ->
                    permissionRepository.save(new Permission(entry.getKey(), entry.getValue())));
        }

        assign(Role.ROLE_USER, List.of(
                PermissionCodes.ROOM_VIEW,
                PermissionCodes.BOOKING_VIEW_OWN,
                PermissionCodes.BOOKING_CREATE,
                PermissionCodes.BOOKING_CANCEL_OWN,
                PermissionCodes.ACCOUNT_READ,
                PermissionCodes.ACCOUNT_UPDATE,
                PermissionCodes.INVOICE_VIEW,
                PermissionCodes.INVOICE_EXPORT,
                PermissionCodes.NOTIFICATION_READ,
                PermissionCodes.NOTIFICATION_MARK_READ,
                PermissionCodes.DEPARTMENT_VIEW_OWN,
                PermissionCodes.DEPT_CHANGE_VIEW_OWN,
                PermissionCodes.DEPT_CHANGE_REQUEST
        ));

        assign(Role.ROLE_ADMIN, List.of(
                PermissionCodes.ROOM_VIEW,
                PermissionCodes.ROOM_CREATE,
                PermissionCodes.ROOM_UPDATE,
                PermissionCodes.BOOKING_VIEW_OWN,
                PermissionCodes.BOOKING_VIEW_ALL,
                PermissionCodes.BOOKING_CREATE,
                PermissionCodes.BOOKING_APPROVE,
                PermissionCodes.BOOKING_REJECT,
                PermissionCodes.BOOKING_CANCEL_OWN,
                PermissionCodes.BOOKING_CANCEL_ANY,
                PermissionCodes.ACCOUNT_READ,
                PermissionCodes.ACCOUNT_UPDATE,
                PermissionCodes.INVOICE_VIEW,
                PermissionCodes.INVOICE_EXPORT,
                PermissionCodes.NOTIFICATION_READ,
                PermissionCodes.NOTIFICATION_MARK_READ,
                PermissionCodes.DEPARTMENT_VIEW_OWN,
                PermissionCodes.DEPT_CHANGE_VIEW_OWN,
                PermissionCodes.DEPT_CHANGE_REQUEST,
                PermissionCodes.DEPT_CHANGE_VIEW_ALL,
                PermissionCodes.DEPT_CHANGE_APPROVE,
                PermissionCodes.DEPT_CHANGE_REJECT,
                PermissionCodes.USER_VIEW,
                PermissionCodes.USER_CREATE,
                PermissionCodes.USER_UPDATE,
                PermissionCodes.USER_DEACTIVATE,
                PermissionCodes.REVENUE_VIEW,
                PermissionCodes.REVENUE_EXPORT
        ));
    }

    private Map<String, String> permissionDefinitions() {
        Map<String, String> definitions = new LinkedHashMap<>();
        definitions.put(PermissionCodes.ROOM_VIEW, "View meeting rooms");
        definitions.put(PermissionCodes.ROOM_CREATE, "Create meeting rooms");
        definitions.put(PermissionCodes.ROOM_UPDATE, "Update meeting rooms");
        definitions.put(PermissionCodes.BOOKING_VIEW_OWN, "View own bookings");
        definitions.put(PermissionCodes.BOOKING_VIEW_ALL, "View all bookings");
        definitions.put(PermissionCodes.BOOKING_CREATE, "Create bookings");
        definitions.put(PermissionCodes.BOOKING_APPROVE, "Approve bookings");
        definitions.put(PermissionCodes.BOOKING_REJECT, "Reject bookings");
        definitions.put(PermissionCodes.BOOKING_CANCEL_OWN, "Cancel own bookings");
        definitions.put(PermissionCodes.BOOKING_CANCEL_ANY, "Cancel any booking");
        definitions.put(PermissionCodes.ACCOUNT_READ, "View own account");
        definitions.put(PermissionCodes.ACCOUNT_UPDATE, "Update own account");
        definitions.put(PermissionCodes.INVOICE_VIEW, "View own invoices");
        definitions.put(PermissionCodes.INVOICE_EXPORT, "Export own invoices");
        definitions.put(PermissionCodes.NOTIFICATION_READ, "View notifications");
        definitions.put(PermissionCodes.NOTIFICATION_MARK_READ, "Mark notifications as read");
        definitions.put(PermissionCodes.DEPARTMENT_VIEW_OWN, "View own department");
        definitions.put(PermissionCodes.DEPT_CHANGE_VIEW_OWN, "View own department change request");
        definitions.put(PermissionCodes.DEPT_CHANGE_REQUEST, "Request department change");
        definitions.put(PermissionCodes.DEPT_CHANGE_VIEW_ALL, "View all department change requests");
        definitions.put(PermissionCodes.DEPT_CHANGE_APPROVE, "Approve department change requests");
        definitions.put(PermissionCodes.DEPT_CHANGE_REJECT, "Reject department change requests");
        definitions.put(PermissionCodes.USER_VIEW, "View users");
        definitions.put(PermissionCodes.USER_CREATE, "Create users");
        definitions.put(PermissionCodes.USER_UPDATE, "Update users");
        definitions.put(PermissionCodes.USER_DEACTIVATE, "Deactivate users");
        definitions.put(PermissionCodes.REVENUE_VIEW, "View revenue reports");
        definitions.put(PermissionCodes.REVENUE_EXPORT, "Export revenue reports");
        return definitions;
    }

    private void assign(Role role, List<String> codes) {
        Set<String> existing = Set.copyOf(rolePermissionRepository.findPermissionCodesByRole(role));
        for (String code : codes) {
            if (existing.contains(code)) {
                continue;
            }
            Permission permission = permissionRepository.findByCode(code)
                    .orElseThrow(() -> new IllegalStateException("Missing permission: " + code));
            rolePermissionRepository.save(new RolePermission(role, permission));
        }
    }
}
