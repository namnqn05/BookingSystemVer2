-- Enums
DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'booking_status') THEN
        CREATE TYPE booking_status AS ENUM ('PENDING', 'APPROVED', 'CANCELLED', 'EXPIRED');
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'department_change_request_status') THEN
        CREATE TYPE department_change_request_status AS ENUM ('PENDING', 'APPROVED', 'REJECTED');
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'notification_type') THEN
        CREATE TYPE notification_type AS ENUM ('BOOKING_PENDING', 'BOOKING_APPROVED', 'BOOKING_REJECTED', 'BOOKING_CANCELLED', 'BOOKING_EXPIRED', 'DEPT_CHANGE_PENDING', 'DEPT_CHANGE_APPROVED', 'DEPT_CHANGE_REJECTED');
    END IF;
END $$;

-- Sequence Generator
CREATE SEQUENCE IF NOT EXISTS sequence_generator START 1 INCREMENT 50;

-- 1. departments
CREATE TABLE IF NOT EXISTS departments (
    id bigint NOT NULL PRIMARY KEY,
    code varchar(50) NOT NULL UNIQUE,
    name varchar(100) NOT NULL
);

-- 2. users
CREATE TABLE IF NOT EXISTS users (
    id bigint NOT NULL PRIMARY KEY,
    password_hash varchar(60) NOT NULL,
    full_name varchar(100),
    email varchar(254) UNIQUE,
    activated boolean NOT NULL DEFAULT false,
    department_id bigint,
    role varchar(50) NOT NULL,
    created_by varchar(50) NOT NULL,
    created_date timestamp,
    last_modified_by varchar(50),
    last_modified_date timestamp,
    CONSTRAINT fk_user_department FOREIGN KEY (department_id) REFERENCES departments(id)
);

-- 3. rooms
CREATE TABLE IF NOT EXISTS rooms (
    id bigint NOT NULL PRIMARY KEY,
    name varchar(100) NOT NULL,
    capacity integer NOT NULL CHECK (capacity >= 1),
    is_active boolean NOT NULL,
    locked_department_id bigint NOT NULL,
    price_per_hour decimal(19,2) NOT NULL DEFAULT 0.00 CHECK (price_per_hour >= 0),
    CONSTRAINT fk_room_locked_department FOREIGN KEY (locked_department_id) REFERENCES departments(id)
);

-- 4. bookings
CREATE TABLE IF NOT EXISTS bookings (
    id bigint NOT NULL PRIMARY KEY,
    title varchar(200) NOT NULL,
    start_time timestamp NOT NULL,
    end_time timestamp NOT NULL,
    status booking_status NOT NULL,
    room_id bigint NOT NULL,
    user_id bigint NOT NULL,
    price_per_hour decimal(19,2) NOT NULL,
    amount decimal(19,2) NOT NULL,
    CONSTRAINT fk_booking_room FOREIGN KEY (room_id) REFERENCES rooms(id),
    CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 5. department_change_requests
CREATE TABLE IF NOT EXISTS department_change_requests (
    id bigint NOT NULL PRIMARY KEY,
    user_id bigint NOT NULL,
    requested_department_id bigint NOT NULL,
    status department_change_request_status NOT NULL DEFAULT 'PENDING',
    reviewed_by_id bigint,
    reviewed_date timestamp,
    created_by varchar(50) NOT NULL,
    created_date timestamp,
    last_modified_by varchar(50),
    last_modified_date timestamp,
    CONSTRAINT fk_dcr_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_dcr_requested_dept FOREIGN KEY (requested_department_id) REFERENCES departments(id),
    CONSTRAINT fk_dcr_reviewed_by FOREIGN KEY (reviewed_by_id) REFERENCES users(id)
);

-- 6. notifications
CREATE TABLE IF NOT EXISTS notifications (
    id bigint NOT NULL PRIMARY KEY,
    recipient_user_id bigint NOT NULL,
    title varchar(255) NOT NULL,
    message text NOT NULL,
    type notification_type NOT NULL,
    reference_type varchar(50),
    reference_id bigint,
    is_read boolean NOT NULL DEFAULT false,
    created_at timestamp NOT NULL,
    read_at timestamp,
    created_by bigint,
    CONSTRAINT fk_notification_recipient FOREIGN KEY (recipient_user_id) REFERENCES users(id)
);

CREATE INDEX IF NOT EXISTS idx_notification_user_created ON notifications(recipient_user_id, created_at);

-- 7. permissions
CREATE TABLE IF NOT EXISTS permissions (
    id bigint NOT NULL PRIMARY KEY,
    code varchar(100) NOT NULL UNIQUE,
    description varchar(255)
);

-- 8. role_permissions
CREATE TABLE IF NOT EXISTS role_permissions (
    id bigint NOT NULL PRIMARY KEY,
    role varchar(50) NOT NULL,
    permission_id bigint NOT NULL,
    CONSTRAINT uk_role_permission UNIQUE (role, permission_id),
    CONSTRAINT fk_role_permission_permission FOREIGN KEY (permission_id) REFERENCES permissions(id)
);
