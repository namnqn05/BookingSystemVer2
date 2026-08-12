-- Sequence
CREATE SEQUENCE IF NOT EXISTS sequence_generator START 1 INCREMENT 50;

-- Enums
CREATE TYPE user_role AS ENUM ('ROLE_ADMIN', 'ROLE_USER');
CREATE TYPE booking_status AS ENUM ('PENDING', 'APPROVED', 'CANCELLED', 'EXPIRED');
CREATE TYPE department_change_request_status AS ENUM ('PENDING', 'APPROVED', 'REJECTED');
CREATE TYPE notification_type AS ENUM ('BOOKING_PENDING', 'BOOKING_APPROVED', 'BOOKING_REJECTED', 
'BOOKING_CANCELLED', 'BOOKING_EXPIRED', 'DEPT_CHANGE_PENDING', 'DEPT_CHANGE_APPROVED', 'DEPT_CHANGE_REJECTED');

-- 1. department
CREATE TABLE department (
    id bigint NOT NULL PRIMARY KEY,
    code varchar(50) NOT NULL UNIQUE,
    name varchar(100) NOT NULL
);

-- 2. jhi_user
CREATE TABLE jhi_user (
    id bigint NOT NULL PRIMARY KEY,
    login varchar(50) NOT NULL UNIQUE,
    password_hash varchar(60) NOT NULL,
    full_name varchar(100),
    email varchar(254) UNIQUE,
    activated boolean NOT NULL DEFAULT false,
    department_id bigint,
    role user_role NOT NULL, -- Replaces jhi_authority and jhi_user_authority
    created_by varchar(50) NOT NULL,
    created_date timestamp,
    last_modified_by varchar(50),
    last_modified_date timestamp,
    CONSTRAINT fk_jhi_user_department FOREIGN KEY (department_id) REFERENCES department(id)
);

-- 3. room
CREATE TABLE room (
    id bigint NOT NULL PRIMARY KEY,
    name varchar(100) NOT NULL,
    capacity integer NOT NULL CHECK (capacity >= 1),
    is_active boolean NOT NULL,
    locked_department_id bigint NOT NULL,
    price_per_hour decimal(19,2) NOT NULL CHECK (price_per_hour >= 0),
    CONSTRAINT fk_room_locked_department FOREIGN KEY (locked_department_id) REFERENCES department(id)
);

-- 4. booking
CREATE TABLE booking (
    id bigint NOT NULL PRIMARY KEY,
    title varchar(200) NOT NULL,
    start_time timestamp NOT NULL,
    end_time timestamp NOT NULL,
    status booking_status NOT NULL,
    room_id bigint NOT NULL,
    user_id bigint NOT NULL,
    price_per_hour decimal(19,2) NOT NULL,
    amount decimal(19,2) NOT NULL,
    CONSTRAINT fk_booking_room FOREIGN KEY (room_id) REFERENCES room(id),
    CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES jhi_user(id)
);

-- 5. department_change_requests
CREATE TABLE department_change_requests (
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
    CONSTRAINT fk_dcr_user FOREIGN KEY (user_id) REFERENCES jhi_user(id),
    CONSTRAINT fk_dcr_requested_dept FOREIGN KEY (requested_department_id) REFERENCES department(id),
    CONSTRAINT fk_dcr_reviewed_by FOREIGN KEY (reviewed_by_id) REFERENCES jhi_user(id)
);

-- 6. notification
CREATE TABLE notification (
    id bigint NOT NULL PRIMARY KEY,
    user_id bigint NOT NULL,
    type notification_type NOT NULL,
    title varchar(200) NOT NULL,
    message varchar(500) NOT NULL,
    booking_id bigint,
    read_date timestamp,
    created_by varchar(50) NOT NULL,
    created_date timestamp,
    last_modified_by varchar(50),
    last_modified_date timestamp,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES jhi_user(id),
    CONSTRAINT fk_notification_booking FOREIGN KEY (booking_id) REFERENCES booking(id)
);

CREATE INDEX idx_notification_user_created ON notification(user_id, created_date);
