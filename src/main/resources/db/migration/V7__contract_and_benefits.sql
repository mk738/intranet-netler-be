-- V7__contract_and_benefits.sql

-- ── Employment Contract ────────────────────────────────────────────────────────
CREATE TABLE employee_contract (
    id           UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    employee_id  UUID        NOT NULL UNIQUE REFERENCES employees(id) ON DELETE CASCADE,
    content_type VARCHAR(100) NOT NULL,
    data         BYTEA       NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by   VARCHAR(255),
    updated_by   VARCHAR(255)
);

-- ── Employee Benefits ──────────────────────────────────────────────────────────
CREATE TABLE employee_benefit (
    id           UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    employee_id  UUID        NOT NULL REFERENCES employees(id) ON DELETE CASCADE,
    name         VARCHAR(255) NOT NULL,
    description  TEXT,
    sort_order   INT         NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by   VARCHAR(255),
    updated_by   VARCHAR(255)
);

CREATE INDEX idx_employee_benefit_employee ON employee_benefit(employee_id, sort_order ASC);
