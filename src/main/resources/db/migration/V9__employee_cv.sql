-- V9__employee_cv.sql

CREATE TABLE employee_cv (
    id           UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id  UUID         NOT NULL REFERENCES employees(id) ON DELETE CASCADE,
    storage_path TEXT,
    uploaded_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    UNIQUE (employee_id)
);
