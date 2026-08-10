-- =============================================================================
-- Test Data Script for Meeting Room Booking System
-- Based on schema.sql
-- Password hash below corresponds to 'password123' encoded with BCrypt
-- =============================================================================

-- Clean up existing data (Optional / standard for re-running seed scripts)
TRUNCATE TABLE notifications, department_change_requests, bookings, rooms, users, departments RESTART IDENTITY CASCADE;

-- -----------------------------------------------------------------------------
-- 1. Insert Departments
-- -----------------------------------------------------------------------------
INSERT INTO departments (id, code, name) VALUES
(1, 'IT', 'Information Technology'),
(2, 'HR', 'Human Resources'),
(3, 'SALES', 'Sales & Marketing'),
(4, 'FIN', 'Finance & Accounting');

-- -----------------------------------------------------------------------------
-- 2. Insert Users (jhi_user)
-- Note: 'password123' hashed with BCrypt = '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a'
-- -----------------------------------------------------------------------------
INSERT INTO users (
    id, password_hash, full_name, email, activated, department_id, role, created_by, created_date, last_modified_by, last_modified_date
) VALUES
(1, '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'System Admin', 'admin@example.com', true, 1, 'ROLE_ADMIN', 'system', '2026-01-01 08:00:00', 'system', '2026-01-01 08:00:00'),
(2, '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'John Doe', 'john.doe@example.com', true, 1, 'ROLE_USER', 'system', '2026-01-02 09:00:00', 'system', '2026-01-02 09:00:00'),
(3, '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'Jane Smith', 'jane.smith@example.com', true, 2, 'ROLE_USER', 'system', '2026-01-03 09:30:00', 'system', '2026-01-03 09:30:00'),
(4, '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'Bob Wilson', 'bob.wilson@example.com', true, 3, 'ROLE_USER', 'system', '2026-01-04 10:00:00', 'system', '2026-01-04 10:00:00'),
(5, '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'Alice Brown', 'alice.brown@example.com', false, 4, 'ROLE_USER', 'system', '2026-01-05 11:00:00', 'system', '2026-01-05 11:00:00');

-- -----------------------------------------------------------------------------
-- 3. Insert Rooms
-- -----------------------------------------------------------------------------
INSERT INTO rooms (
    id, name, capacity, is_active, locked_department_id, price_per_hour
) VALUES
(1, 'Grand Conference Room', 20, true, NULL, 50.00),
(2, 'IT Lab Room', 8, true, 1, 25.00),
(3, 'HR Interview Room', 4, true, 2, 15.00),
(4, 'Executive Boardroom', 15, true, NULL, 100.00),
(5, 'Renovation Room 105', 10, false, NULL, 30.00);

-- -----------------------------------------------------------------------------
-- 4. Insert Bookings
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
-- 5. Insert Department Change Requests
-- -----------------------------------------------------------------------------
INSERT INTO department_change_requests (
    id, user_id, requested_department_id, status, reviewed_by_id, reviewed_date, created_by, created_date, last_modified_by, last_modified_date
) VALUES
(1, 3, 1, 'PENDING', NULL, NULL, 'jane_smith', '2026-08-01 10:00:00', 'jane_smith', '2026-08-01 10:00:00'),
(2, 4, 2, 'APPROVED', 1, '2026-08-02 14:00:00', 'bob_wilson', '2026-08-01 11:00:00', 'admin', '2026-08-02 14:00:00'),
(3, 5, 3, 'REJECTED', 1, '2026-08-03 16:00:00', 'alice_brown', '2026-08-02 09:00:00', 'admin', '2026-08-03 16:00:00');

-- -----------------------------------------------------------------------------
-- 6. Insert Notifications
-- -----------------------------------------------------------------------------
INSERT INTO notifications (
    id, recipient_user_id, title, message, type, reference_type, reference_id, is_read, created_at, read_at, created_by
) VALUES
(1, 2, 'Booking Approved', 'Your booking "Q3 Strategy Meeting" has been approved.', 'BOOKING_APPROVED', 'BOOKING', 1, true, '2026-08-10 08:00:00', '2026-08-10 08:30:00', 1),
(2, 3, 'Booking Pending', 'Your booking "Candidate Screening" is currently pending review.', 'BOOKING_PENDING', 'BOOKING', 3, false, '2026-08-10 09:00:00', NULL, 1),
(3, 4, 'Booking Cancelled', 'Your booking "Sales Pitch Presentation" was cancelled.', 'BOOKING_CANCELLED', 'BOOKING', 4, true, '2026-08-10 10:00:00', '2026-08-10 10:15:00', 1),
(4, 3, 'Department Change Submitted', 'Your request to change department to IT is under review.', 'DEPT_CHANGE_PENDING', 'DEPARTMENT_CHANGE_REQUEST', 1, false, '2026-08-01 10:00:00', NULL, 1),
(5, 4, 'Department Change Approved', 'Your request to transfer to Human Resources has been approved.', 'DEPT_CHANGE_APPROVED', 'DEPARTMENT_CHANGE_REQUEST', 2, true, '2026-08-02 14:00:00', '2026-08-02 15:00:00', 1),
(6, 5, 'Department Change Rejected', 'Your request to transfer to Sales & Marketing was rejected.', 'DEPT_CHANGE_REJECTED', 'DEPARTMENT_CHANGE_REQUEST', 3, false, '2026-08-03 16:00:00', NULL, 1);

-- -----------------------------------------------------------------------------
-- 7. Update Sequence Generator Value
-- -----------------------------------------------------------------------------
SELECT setval('sequence_generator', 100, true);
