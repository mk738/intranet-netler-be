-- V11__add_fredrik_norling.sql

INSERT INTO employees (id, firebase_uid, email, role, is_active)
VALUES ('00000000-0000-0000-0000-000000000009', 'sq3ezUDBzSZ6RV16FJG68OOuC7t2', 'norling.fre@gmail.com', 'EMPLOYEE', TRUE)
ON CONFLICT (email) DO NOTHING;

INSERT INTO employee_profiles (id, employee_id, first_name, last_name, job_title, start_date)
VALUES ('00000000-0000-0000-0002-000000000009', '00000000-0000-0000-0000-000000000009', 'Fredrik', 'Norling', 'Backend Developer', '2025-01-15')
ON CONFLICT (employee_id) DO NOTHING;
