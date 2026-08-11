package com.example.booking_system.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PermissionMappingTest {

    private PermissionMapping permissionMapping;

    @BeforeEach
    void setUp() {
        permissionMapping = new PermissionMapping();
    }

    @Test
    void resolve_CreateRoom_RequiresRoomCreate() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/rooms");

        Optional<Set<String>> permissions = permissionMapping.resolve(request);

        assertTrue(permissions.isPresent());
        assertEquals(Set.of(PermissionCodes.ROOM_CREATE), permissions.get());
    }

    @Test
    void resolve_ListRooms_RequiresRoomView() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/rooms");

        Optional<Set<String>> permissions = permissionMapping.resolve(request);

        assertTrue(permissions.isPresent());
        assertEquals(Set.of(PermissionCodes.ROOM_VIEW), permissions.get());
    }

    @Test
    void resolve_ListBookings_AllowsOwnOrAll() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/bookings");

        Optional<Set<String>> permissions = permissionMapping.resolve(request);

        assertTrue(permissions.isPresent());
        assertEquals(Set.of(PermissionCodes.BOOKING_VIEW_OWN, PermissionCodes.BOOKING_VIEW_ALL), permissions.get());
    }

    @Test
    void resolve_CancelBooking_AllowsOwnOrAny() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/bookings/12/cancel");

        Optional<Set<String>> permissions = permissionMapping.resolve(request);

        assertTrue(permissions.isPresent());
        assertEquals(Set.of(PermissionCodes.BOOKING_CANCEL_OWN, PermissionCodes.BOOKING_CANCEL_ANY), permissions.get());
    }

    @Test
    void resolve_AccountRead_RequiresAccountRead() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/account");

        Optional<Set<String>> permissions = permissionMapping.resolve(request);

        assertTrue(permissions.isPresent());
        assertEquals(Set.of(PermissionCodes.ACCOUNT_READ), permissions.get());
    }

    @Test
    void resolve_RevenueExport_RequiresExportPermission() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/admin/revenue/export");

        Optional<Set<String>> permissions = permissionMapping.resolve(request);

        assertTrue(permissions.isPresent());
        assertEquals(Set.of(PermissionCodes.REVENUE_EXPORT), permissions.get());
    }
}
