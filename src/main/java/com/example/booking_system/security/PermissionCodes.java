package com.example.booking_system.security;

public final class PermissionCodes {

    private PermissionCodes() {
    }

    // Rooms
    public static final String ROOM_VIEW = "ROOM_VIEW";
    public static final String ROOM_CREATE = "ROOM_CREATE";
    public static final String ROOM_UPDATE = "ROOM_UPDATE";

    // Bookings
    public static final String BOOKING_VIEW_OWN = "BOOKING_VIEW_OWN";
    public static final String BOOKING_VIEW_ALL = "BOOKING_VIEW_ALL";
    public static final String BOOKING_CREATE = "BOOKING_CREATE";
    public static final String BOOKING_APPROVE = "BOOKING_APPROVE";
    public static final String BOOKING_REJECT = "BOOKING_REJECT";
    public static final String BOOKING_CANCEL_OWN = "BOOKING_CANCEL_OWN";
    public static final String BOOKING_CANCEL_ANY = "BOOKING_CANCEL_ANY";

    // Account / invoices
    public static final String ACCOUNT_READ = "ACCOUNT_READ";
    public static final String ACCOUNT_UPDATE = "ACCOUNT_UPDATE";
    public static final String INVOICE_VIEW = "INVOICE_VIEW";
    public static final String INVOICE_EXPORT = "INVOICE_EXPORT";

    // Notifications
    public static final String NOTIFICATION_READ = "NOTIFICATION_READ";
    public static final String NOTIFICATION_MARK_READ = "NOTIFICATION_MARK_READ";

    // Departments
    public static final String DEPARTMENT_VIEW_OWN = "DEPARTMENT_VIEW_OWN";
    public static final String DEPT_CHANGE_VIEW_OWN = "DEPT_CHANGE_VIEW_OWN";
    public static final String DEPT_CHANGE_REQUEST = "DEPT_CHANGE_REQUEST";
    public static final String DEPT_CHANGE_VIEW_ALL = "DEPT_CHANGE_VIEW_ALL";
    public static final String DEPT_CHANGE_APPROVE = "DEPT_CHANGE_APPROVE";
    public static final String DEPT_CHANGE_REJECT = "DEPT_CHANGE_REJECT";

    // Admin users
    public static final String USER_VIEW = "USER_VIEW";
    public static final String USER_CREATE = "USER_CREATE";
    public static final String USER_UPDATE = "USER_UPDATE";
    public static final String USER_DEACTIVATE = "USER_DEACTIVATE";

    // Revenue
    public static final String REVENUE_VIEW = "REVENUE_VIEW";
    public static final String REVENUE_EXPORT = "REVENUE_EXPORT";
}
