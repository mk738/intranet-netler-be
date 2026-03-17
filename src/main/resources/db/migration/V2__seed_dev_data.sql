-- V2__seed_dev_data.sql
-- Local development seed data — do NOT run in production
-- Admin employee (firebase_uid matches your local Firebase test user)

INSERT INTO employees (id, firebase_uid, email, role, is_active)
VALUES
  ('00000000-0000-0000-0000-000000000001', 'local-admin-uid',    'admin@company.com',   'ADMIN',    TRUE),
  ('00000000-0000-0000-0000-000000000002', 'local-employee-uid', 'erik@company.com',    'EMPLOYEE', TRUE),
  ('00000000-0000-0000-0000-000000000003', 'local-emp2-uid',     'sara@company.com',    'EMPLOYEE', TRUE),
  ('00000000-0000-0000-0000-000000000004', 'local-emp3-uid',     'marcus@company.com',  'EMPLOYEE', TRUE);

INSERT INTO employee_profiles (employee_id, first_name, last_name, job_title, start_date)
VALUES
  ('00000000-0000-0000-0000-000000000001', 'Anna',   'Admin',    'CTO',                   '2022-01-01'),
  ('00000000-0000-0000-0000-000000000002', 'Erik',   'Lindqvist','Senior Backend Dev',     '2023-03-01'),
  ('00000000-0000-0000-0000-000000000003', 'Sara',   'Berg',     'Backend Developer',      '2023-06-01'),
  ('00000000-0000-0000-0000-000000000004', 'Marcus', 'Holm',     'Full Stack Developer',   '2024-01-01');

INSERT INTO clients (id, company_name, contact_name, contact_email, status)
VALUES
  ('00000000-0000-0000-0001-000000000001', 'Spotify',     'Johan S',    'johan@spotify.com',  'ACTIVE'),
  ('00000000-0000-0000-0001-000000000002', 'Klarna',      'Lisa K',     'lisa@klarna.com',    'ACTIVE'),
  ('00000000-0000-0000-0001-000000000003', 'IKEA Digital','Anna I',     'anna@ikea.com',      'PROSPECT');

INSERT INTO assignments (employee_id, client_id, project_name, start_date, end_date, status)
VALUES
  ('00000000-0000-0000-0000-000000000002',
   '00000000-0000-0000-0001-000000000001',
   'Data platform', '2025-03-01', '2026-03-01', 'ACTIVE'),
  ('00000000-0000-0000-0000-000000000003',
   '00000000-0000-0000-0001-000000000002',
   'Payments API',  '2025-09-01', '2026-09-01', 'ACTIVE');

INSERT INTO news_posts (title, body, author_id, published_at, pinned)
VALUES
  ('Welcome to the intranet',
   'This is your company hub. Find news, events, and manage your profile here.',
   '00000000-0000-0000-0000-000000000001',
   NOW(), TRUE);
