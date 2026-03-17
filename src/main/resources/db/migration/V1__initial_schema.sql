-- V1__initial_schema.sql
-- Full initial schema for intranet application

-- Enable UUID generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ── Employees ─────────────────────────────────────────────────
CREATE TABLE employees (
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    firebase_uid VARCHAR(128) NOT NULL UNIQUE,
    email        VARCHAR(255) NOT NULL UNIQUE,
    role         VARCHAR(20)  NOT NULL DEFAULT 'EMPLOYEE'
                     CHECK (role IN ('ADMIN', 'EMPLOYEE')),
    is_active    BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by   VARCHAR(255),
    updated_by   VARCHAR(255)
);

CREATE INDEX idx_employees_firebase_uid ON employees(firebase_uid);
CREATE INDEX idx_employees_email        ON employees(email);
CREATE INDEX idx_employees_role         ON employees(role);

-- ── Employee profiles ─────────────────────────────────────────
CREATE TABLE employee_profiles (
    id                UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    employee_id       UUID         NOT NULL UNIQUE REFERENCES employees(id) ON DELETE CASCADE,
    first_name        VARCHAR(100) NOT NULL,
    last_name         VARCHAR(100) NOT NULL,
    job_title         VARCHAR(150),
    phone             VARCHAR(50),
    address           TEXT,
    emergency_contact TEXT,
    avatar_url        TEXT,
    start_date        DATE,
    birth_date        DATE,
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by        VARCHAR(255),
    updated_by        VARCHAR(255)
);

-- ── Bank info (sensitive — encrypted at application layer) ────
CREATE TABLE bank_info (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    employee_id     UUID         NOT NULL UNIQUE REFERENCES employees(id) ON DELETE CASCADE,
    bank_name       VARCHAR(100),
    account_number  TEXT,       -- stored as Jasypt cipher text
    clearing_number TEXT,       -- stored as Jasypt cipher text
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255)
);

-- ── Education ─────────────────────────────────────────────────
CREATE TABLE education (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    employee_id UUID         NOT NULL REFERENCES employees(id) ON DELETE CASCADE,
    institution VARCHAR(200) NOT NULL,
    degree      VARCHAR(100) NOT NULL,
    field       VARCHAR(150) NOT NULL,
    start_year  INTEGER      NOT NULL,
    end_year    INTEGER,
    description TEXT,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(255),
    updated_by  VARCHAR(255)
);

CREATE INDEX idx_education_employee ON education(employee_id);

-- ── Vacation requests ─────────────────────────────────────────
CREATE TABLE vacation_requests (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    employee_id UUID        NOT NULL REFERENCES employees(id) ON DELETE CASCADE,
    start_date  DATE        NOT NULL,
    end_date    DATE        NOT NULL,
    days_count  INTEGER     NOT NULL,
    status      VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                    CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    reviewed_by UUID        REFERENCES employees(id),
    reviewed_at TIMESTAMPTZ,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(255),
    updated_by  VARCHAR(255),
    CONSTRAINT chk_dates CHECK (end_date >= start_date)
);

CREATE INDEX idx_vacation_employee ON vacation_requests(employee_id);
CREATE INDEX idx_vacation_status   ON vacation_requests(status);

-- ── Clients ───────────────────────────────────────────────────
CREATE TABLE clients (
    id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_name  VARCHAR(200) NOT NULL,
    contact_name  VARCHAR(150),
    contact_email VARCHAR(255),
    phone         VARCHAR(50),
    org_number    VARCHAR(50),
    status        VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE'
                      CHECK (status IN ('ACTIVE', 'PROSPECT', 'INACTIVE')),
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by    VARCHAR(255),
    updated_by    VARCHAR(255)
);

CREATE INDEX idx_clients_status ON clients(status);

-- ── Assignments ───────────────────────────────────────────────
CREATE TABLE assignments (
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    employee_id  UUID        NOT NULL REFERENCES employees(id) ON DELETE CASCADE,
    client_id    UUID        NOT NULL REFERENCES clients(id)   ON DELETE RESTRICT,
    project_name VARCHAR(200) NOT NULL,
    start_date   DATE        NOT NULL,
    end_date     DATE,
    status       VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
                     CHECK (status IN ('ACTIVE', 'ENDED')),
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by   VARCHAR(255),
    updated_by   VARCHAR(255)
);

CREATE INDEX idx_assignments_employee ON assignments(employee_id);
CREATE INDEX idx_assignments_client   ON assignments(client_id);
CREATE INDEX idx_assignments_status   ON assignments(status);

-- Ensure only one active assignment per employee at a time
CREATE UNIQUE INDEX idx_one_active_assignment
    ON assignments(employee_id)
    WHERE status = 'ACTIVE';

-- ── News posts ────────────────────────────────────────────────
CREATE TABLE news_posts (
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title        VARCHAR(300) NOT NULL,
    body         TEXT         NOT NULL,
    author_id    UUID         NOT NULL REFERENCES employees(id) ON DELETE RESTRICT,
    published_at TIMESTAMPTZ,
    pinned       BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by   VARCHAR(255),
    updated_by   VARCHAR(255)
);

CREATE INDEX idx_news_published ON news_posts(published_at DESC);

-- ── Events ────────────────────────────────────────────────────
CREATE TABLE events (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title       VARCHAR(300) NOT NULL,
    description TEXT,
    location    VARCHAR(300),
    event_date  DATE         NOT NULL,
    end_date    DATE,
    all_day     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_by  UUID         NOT NULL REFERENCES employees(id) ON DELETE RESTRICT,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_by  VARCHAR(255)
);

CREATE INDEX idx_events_date ON events(event_date);
