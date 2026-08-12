-- =============================================================================
-- Mock data for Meeting Room Booking System
-- Adapted from the original data.sql content to current Hibernate tables:
--   departments, users, rooms, bookings, department_change_request, notifications
--   + permissions / role_permissions
--
-- Password for all users: password123
-- BCrypt (verified): $2a$10$TVWj3hxNF3ZFhNGEwUXSmO0sQTPFOOie0BwjxhGSvPlc70UqiEKy6
--
-- How to use:
--   1) Start the app once (ddl-auto=update creates tables)
--   2) Run this script in psql / DBeaver / IntelliJ
-- =============================================================================

BEGIN;

-- Clean existing seed data (safe re-run)
TRUNCATE TABLE
    notifications,
    department_change_requests,
    bookings,
    rooms,
    role_permissions,
    permissions,
    users,
    departments
RESTART IDENTITY CASCADE;

-- -----------------------------------------------------------------------------
-- 1. Departments (from original data.sql)
-- -----------------------------------------------------------------------------
INSERT INTO departments (id, code, name) VALUES
(1, 'IT', 'Information Technology'),
(2, 'HR', 'Human Resources'),
(3, 'SALES', 'Sales & Marketing'),
(4, 'FIN', 'Finance & Accounting');

-- -----------------------------------------------------------------------------
-- 2. Permissions
-- -----------------------------------------------------------------------------
INSERT INTO permissions (id, code, description) VALUES
(1,  'ROOM_VIEW', 'View meeting rooms'),
(2,  'ROOM_CREATE', 'Create meeting rooms'),
(3,  'ROOM_UPDATE', 'Update meeting rooms'),
(4,  'BOOKING_VIEW_OWN', 'View own bookings'),
(5,  'BOOKING_VIEW_ALL', 'View all bookings'),
(6,  'BOOKING_CREATE', 'Create bookings'),
(7,  'BOOKING_APPROVE', 'Approve bookings'),
(8,  'BOOKING_REJECT', 'Reject bookings'),
(9,  'BOOKING_CANCEL_OWN', 'Cancel own bookings'),
(10, 'BOOKING_CANCEL_ANY', 'Cancel any booking'),
(11, 'ACCOUNT_READ', 'View own account'),
(12, 'ACCOUNT_UPDATE', 'Update own account'),
(13, 'INVOICE_VIEW', 'View own invoices'),
(14, 'INVOICE_EXPORT', 'Export own invoices'),
(15, 'NOTIFICATION_READ', 'View notifications'),
(16, 'NOTIFICATION_MARK_READ', 'Mark notifications as read'),
(17, 'DEPARTMENT_VIEW_OWN', 'View own department'),
(18, 'DEPT_CHANGE_VIEW_OWN', 'View own department change request'),
(19, 'DEPT_CHANGE_REQUEST', 'Request department change'),
(20, 'DEPT_CHANGE_VIEW_ALL', 'View all department change requests'),
(21, 'DEPT_CHANGE_APPROVE', 'Approve department change requests'),
(22, 'DEPT_CHANGE_REJECT', 'Reject department change requests'),
(23, 'USER_VIEW', 'View users'),
(24, 'USER_CREATE', 'Create users'),
(25, 'USER_UPDATE', 'Update users'),
(26, 'USER_DEACTIVATE', 'Deactivate users'),
(27, 'REVENUE_VIEW', 'View revenue reports'),
(28, 'REVENUE_EXPORT', 'Export revenue reports');

-- -----------------------------------------------------------------------------
-- 3. Role permissions
-- -----------------------------------------------------------------------------
INSERT INTO role_permissions (id, role, permission_id)
SELECT 100 + row_number() OVER (), 'ROLE_USER', p.id
FROM permissions p
WHERE p.code IN (
    'ROOM_VIEW', 'BOOKING_VIEW_OWN', 'BOOKING_CREATE', 'BOOKING_CANCEL_OWN',
    'ACCOUNT_READ', 'ACCOUNT_UPDATE', 'INVOICE_VIEW', 'INVOICE_EXPORT',
    'NOTIFICATION_READ', 'NOTIFICATION_MARK_READ',
    'DEPARTMENT_VIEW_OWN', 'DEPT_CHANGE_VIEW_OWN', 'DEPT_CHANGE_REQUEST'
);

INSERT INTO role_permissions (id, role, permission_id)
SELECT 200 + row_number() OVER (), 'ROLE_ADMIN', p.id
FROM permissions p
WHERE p.code IN (
    'ROOM_VIEW', 'ROOM_CREATE', 'ROOM_UPDATE',
    'BOOKING_VIEW_OWN', 'BOOKING_VIEW_ALL', 'BOOKING_CREATE',
    'BOOKING_APPROVE', 'BOOKING_REJECT', 'BOOKING_CANCEL_OWN', 'BOOKING_CANCEL_ANY',
    'ACCOUNT_READ', 'ACCOUNT_UPDATE', 'INVOICE_VIEW', 'INVOICE_EXPORT',
    'NOTIFICATION_READ', 'NOTIFICATION_MARK_READ',
    'DEPARTMENT_VIEW_OWN', 'DEPT_CHANGE_VIEW_OWN', 'DEPT_CHANGE_REQUEST',
    'DEPT_CHANGE_VIEW_ALL', 'DEPT_CHANGE_APPROVE', 'DEPT_CHANGE_REJECT',
    'USER_VIEW', 'USER_CREATE', 'USER_UPDATE', 'USER_DEACTIVATE',
    'REVENUE_VIEW', 'REVENUE_EXPORT'
);

-- -----------------------------------------------------------------------------
-- 4. Users (from original jhi_user rows; current table has no login column)
-- password = password123
-- hash     = $2a$10$TVWj3hxNF3ZFhNGEwUXSmO0sQTPFOOie0BwjxhGSvPlc70UqiEKy6
-- -----------------------------------------------------------------------------
INSERT INTO users (
    id, email, password_hash, full_name, activated, department_id, role,
    created_by, created_date, last_modified_by, last_modified_date
) VALUES
(1, 'admin@example.com',
    '$2a$10$TVWj3hxNF3ZFhNGEwUXSmO0sQTPFOOie0BwjxhGSvPlc70UqiEKy6',
    'System Admin', TRUE, 1, 'ROLE_ADMIN',
    'system', '2026-01-01 08:00:00+00', 'system', '2026-01-01 08:00:00+00'),
(2, 'john.doe@example.com',
    '$2a$10$TVWj3hxNF3ZFhNGEwUXSmO0sQTPFOOie0BwjxhGSvPlc70UqiEKy6',
    'John Doe', TRUE, 1, 'ROLE_USER',
    'system', '2026-01-02 09:00:00+00', 'system', '2026-01-02 09:00:00+00'),
(3, 'jane.smith@example.com',
    '$2a$10$TVWj3hxNF3ZFhNGEwUXSmO0sQTPFOOie0BwjxhGSvPlc70UqiEKy6',
    'Jane Smith', TRUE, 2, 'ROLE_USER',
    'system', '2026-01-03 09:30:00+00', 'system', '2026-01-03 09:30:00+00'),
(4, 'bob.wilson@example.com',
    '$2a$10$TVWj3hxNF3ZFhNGEwUXSmO0sQTPFOOie0BwjxhGSvPlc70UqiEKy6',
    'Bob Wilson', TRUE, 3, 'ROLE_USER',
    'system', '2026-01-04 10:00:00+00', 'system', '2026-01-04 10:00:00+00'),
(5, 'alice.brown@example.com',
    '$2a$10$TVWj3hxNF3ZFhNGEwUXSmO0sQTPFOOie0BwjxhGSvPlc70UqiEKy6',
    'Alice Brown', FALSE, 4, 'ROLE_USER',
    'system', '2026-01-05 11:00:00+00', 'system', '2026-01-05 11:00:00+00');

-- -----------------------------------------------------------------------------
-- 5. Rooms (from original data.sql)
-- -----------------------------------------------------------------------------
INSERT INTO rooms (id, name, capacity, is_active, locked_department_id, price_per_hour) VALUES
(1, 'Grand Conference Room', 20, TRUE, 1, 50.00),
(2, 'IT Lab Room', 8, TRUE, 1, 25.00),
(3, 'HR Interview Room', 4, TRUE, 2, 15.00),
(4, 'Executive Boardroom', 15, TRUE, 3, 100.00),
(5, 'Renovation Room 105', 10, FALSE, 4, 30.00);

-- -----------------------------------------------------------------------------
-- 6. Bookings (from original data.sql)
-- -----------------------------------------------------------------------------
INSERT INTO bookings (
    id, title, start_time, end_time, status, room_id, user_id, price_per_hour, amount
) VALUES
(1, 'Q3 Strategy Meeting', '2026-08-15 09:00:00', '2026-08-15 11:00:00', 'APPROVED', 1, 2, 50.00, 100.00),
(2, 'IT Architecture Review', '2026-08-16 14:00:00', '2026-08-16 16:00:00', 'APPROVED', 2, 2, 25.00, 50.00),
(3, 'Candidate Screening', '2026-08-17 10:00:00', '2026-08-17 11:30:00', 'PENDING', 3, 3, 15.00, 22.50),
(4, 'Sales Pitch Presentation', '2026-08-18 13:00:00', '2026-08-18 15:00:00', 'CANCELLED', 4, 4, 100.00, 200.00),
(5, 'Historical Training Session', '2026-07-01 09:00:00', '2026-07-01 12:00:00', 'EXPIRED', 1, 3, 50.00, 150.00);

-- -----------------------------------------------------------------------------
-- 7. Department change requests (from original data.sql)
-- -----------------------------------------------------------------------------
INSERT INTO department_change_requests (
    id, user_id, requested_department_id, status,
    reviewed_by_id, reviewed_date,
    created_by, created_date, last_modified_by, last_modified_date
) VALUES
(1, 3, 1, 'PENDING', NULL, NULL,
    'jane_smith', '2026-08-01 10:00:00+00', 'jane_smith', '2026-08-01 10:00:00+00'),
(2, 4, 2, 'APPROVED', 1, '2026-08-02 14:00:00+00',
    'bob_wilson', '2026-08-01 11:00:00+00', 'admin', '2026-08-02 14:00:00+00'),
(3, 5, 3, 'REJECTED', 1, '2026-08-03 16:00:00+00',
    'alice_brown', '2026-08-02 09:00:00+00', 'admin', '2026-08-03 16:00:00+00');

-- -----------------------------------------------------------------------------
-- 8. Notifications (mapped from original columns to current schema)
-- original: user_id, booking_id, read_date
-- current:  recipient_user_id, reference_type/id, is_read, read_at, created_by
-- -----------------------------------------------------------------------------
INSERT INTO notifications (
    id, recipient_user_id, title, message, type,
    reference_type, reference_id, is_read, created_at, read_at, created_by
) VALUES
(1, 2, 'Booking Approved',
    'Your booking "Q3 Strategy Meeting" has been approved.',
    'BOOKING_APPROVED', 'BOOKING', 1,
    TRUE, '2026-08-10 08:00:00+00', '2026-08-10 08:30:00+00', 1),
(2, 3, 'Booking Pending',
    'Your booking "Candidate Screening" is currently pending review.',
    'BOOKING_PENDING', 'BOOKING', 3,
    FALSE, '2026-08-10 09:00:00+00', NULL, 3),
(3, 4, 'Booking Cancelled',
    'Your booking "Sales Pitch Presentation" was cancelled.',
    'BOOKING_CANCELLED', 'BOOKING', 4,
    TRUE, '2026-08-10 10:00:00+00', '2026-08-10 10:15:00+00', 1),
(4, 3, 'Department Change Submitted',
    'Your request to change department to IT is under review.',
    'DEPT_CHANGE_PENDING', 'DEPARTMENT_CHANGE_REQUEST', 1,
    FALSE, '2026-08-01 10:00:00+00', NULL, 3),
(5, 4, 'Department Change Approved',
    'Your request to transfer to Human Resources has been approved.',
    'DEPT_CHANGE_APPROVED', 'DEPARTMENT_CHANGE_REQUEST', 2,
    TRUE, '2026-08-02 14:00:00+00', '2026-08-02 15:00:00+00', 1),
(6, 5, 'Department Change Rejected',
    'Your request to transfer to Sales & Marketing was rejected.',
    'DEPT_CHANGE_REJECTED', 'DEPARTMENT_CHANGE_REQUEST', 3,
    FALSE, '2026-08-03 16:00:00+00', NULL, 1),
-- Admin inbox for pending booking (useful when testing as admin)
(7, 1, 'New Booking Request',
    'New booking request "Candidate Screening" submitted by Jane Smith.',
    'BOOKING_PENDING', 'BOOKING', 3,
    FALSE, '2026-08-10 09:00:00+00', NULL, 3);

-- -----------------------------------------------------------------------------
-- 9. Sequence
-- -----------------------------------------------------------------------------
SELECT setval('sequence_generator', 500, true);

COMMIT;

-- =============================================================================
-- Quick login reference
--   admin@example.com       / password123  (ROLE_ADMIN, activated)
--   john.doe@example.com    / password123  (ROLE_USER, IT)
--   jane.smith@example.com  / password123  (ROLE_USER, HR)
--   bob.wilson@example.com  / password123  (ROLE_USER, SALES)
--   alice.brown@example.com / password123  (ROLE_USER, FIN, activated=false)
-- =============================================================================
