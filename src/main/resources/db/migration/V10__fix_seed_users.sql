-- V10__fix_seed_users.sql

-- ── Add test employee (email/password auth) ───────────────────────────────────
-- firebase_uid is a placeholder — update after first login:
--   UPDATE employees SET firebase_uid = '<real-uid>'
--   WHERE email = 'test.employee@netler.com';
INSERT INTO employees (id, firebase_uid, email, role, is_active)
VALUES ('00000000-0000-0000-0000-000000000008', 'test-employee-uid-placeholder', 'test.employee@netler.com', 'EMPLOYEE', TRUE)
ON CONFLICT (email) DO NOTHING;

INSERT INTO employee_profiles (id, employee_id, first_name, last_name, job_title, start_date)
VALUES ('00000000-0000-0000-0002-000000000008', '00000000-0000-0000-0000-000000000008', 'Test', 'Employee', 'Test User', '2026-01-01')
ON CONFLICT (employee_id) DO NOTHING;
