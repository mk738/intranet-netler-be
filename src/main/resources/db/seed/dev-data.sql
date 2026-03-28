-- =============================================================================
-- Netler Intranet — comprehensive dev seed data
-- =============================================================================
-- Run manually to reset the database to a known state:
--
--   psql -U intranet -d intranet -f scripts/seed_dev_data.sql
--
-- ⚠  NOT a Flyway migration — do NOT put this in db/migration/
-- ⚠  Do NOT run in production
--
-- After running this script, update your own Firebase UID:
--
--   UPDATE employees
--   SET    firebase_uid = '<paste-your-real-uid-here>'
--   WHERE  email = 'marcus.karlsson@netler.com';
--
-- Your real UID is shown in the Firebase console under Authentication,
-- or you can grab it from the backend log when you first attempt to log in.
-- =============================================================================


-- ── Delete in FK-safe order (no TRUNCATE CASCADE to avoid surprises) ─────────
DELETE FROM employee_skills;
DELETE FROM skills;
DELETE FROM education;
DELETE FROM bank_info;
DELETE FROM employee_contract;
DELETE FROM employee_cv;
DELETE FROM employee_benefit;
DELETE FROM employee_avatars;
DELETE FROM vacation_requests;
DELETE FROM event_rsvp;
DELETE FROM faq;
DELETE FROM candidates;
DELETE FROM assignments;
DELETE FROM news_posts;
DELETE FROM events;
DELETE FROM board_comments;
DELETE FROM board_cards;
DELETE FROM board_columns;
DELETE FROM boards;
DELETE FROM employee_profiles;
DELETE FROM clients;
DELETE FROM employees;


-- =============================================================================
-- EMPLOYEES
-- =============================================================================
-- firebase_uid for marcus.karlsson@netler.com is a placeholder.
-- Replace it with your real UID using the UPDATE above.
-- All other UIDs are placeholders for local dev only.

INSERT INTO employees (id, firebase_uid, email, role, is_active)
VALUES
    ('00000000-0000-0000-0000-000000000001', 'UqhXiWJBfZYSlGZyj34PPaj8jJY2', 'marcus.karlsson@netler.com', 'SUPERADMIN', TRUE),
    ('00000000-0000-0000-0000-000000000002', 'erik-netler-uid',               'erik.lindqvist@netler.com',  'EMPLOYEE', TRUE),
    ('00000000-0000-0000-0000-000000000003', 'sara-netler-uid',               'sara.berg@netler.com',        'EMPLOYEE', TRUE),
    ('00000000-0000-0000-0000-000000000004', 'johan-netler-uid',              'johan.petersson@netler.com',  'EMPLOYEE', TRUE),
    ('00000000-0000-0000-0000-000000000005', 'lina-netler-uid',               'lina.eriksson@netler.com',    'EMPLOYEE', TRUE),
    ('00000000-0000-0000-0000-000000000006', 'mikael-netler-uid',             'mikael.svensson@netler.com',  'EMPLOYEE', TRUE),
    ('00000000-0000-0000-0000-000000000007', 'anna-netler-uid',               'anna.johansson@netler.com',   'EMPLOYEE', TRUE),
    ('00000000-0000-0000-0000-000000000008', 'test-employee-uid-placeholder', 'test.employee@netler.com',    'EMPLOYEE', TRUE),
    ('00000000-0000-0000-0000-000000000009', 'sq3ezUDBzSZ6RV16FJG68OOuC7t2',  'norling.fre@gmail.com',        'EMPLOYEE', TRUE);


-- =============================================================================
-- EMPLOYEE PROFILES
-- =============================================================================

INSERT INTO employee_profiles
    (id, employee_id, first_name, last_name, job_title, phone, address, emergency_contact, start_date, birth_date)
VALUES
    -- Marcus Karlsson (admin / CTO)
    ('00000000-0000-0000-0002-000000000001',
     '00000000-0000-0000-0000-000000000001',
     'Marcus', 'Karlsson',
     'CTO & Co-founder',
     '+46 70 123 45 67',
     'Storgatan 12, 111 23 Stockholm',
     'Emma Karlsson – +46 73 987 65 43',
     '2021-01-15', '1988-04-22'),

    -- Erik Lindqvist
    ('00000000-0000-0000-0002-000000000002',
     '00000000-0000-0000-0000-000000000002',
     'Erik', 'Lindqvist',
     'Senior Backend Developer',
     '+46 70 234 56 78',
     'Vasagatan 5, 411 24 Göteborg',
     'Maria Lindqvist – +46 73 876 54 32',
     '2022-08-01', '1991-11-03'),

    -- Sara Berg
    ('00000000-0000-0000-0002-000000000003',
     '00000000-0000-0000-0000-000000000003',
     'Sara', 'Berg',
     'Backend Developer',
     '+46 70 345 67 89',
     'Kungsgatan 18, 753 21 Uppsala',
     'Lars Berg – +46 73 765 43 21',
     '2023-03-15', '1994-07-19'),

    -- Johan Petersson
    ('00000000-0000-0000-0002-000000000004',
     '00000000-0000-0000-0000-000000000004',
     'Johan', 'Petersson',
     'Frontend Developer',
     '+46 70 456 78 90',
     'Drottninggatan 44, 211 41 Malmö',
     'Karin Petersson – +46 73 654 32 10',
     '2023-06-01', '1993-02-28'),

    -- Lina Eriksson
    ('00000000-0000-0000-0002-000000000005',
     '00000000-0000-0000-0000-000000000005',
     'Lina', 'Eriksson',
     'DevOps Engineer',
     '+46 70 567 89 01',
     'Östra Hamngatan 7, 411 10 Göteborg',
     'Peter Eriksson – +46 73 543 21 09',
     '2022-11-01', '1990-09-14'),

    -- Mikael Svensson
    ('00000000-0000-0000-0002-000000000006',
     '00000000-0000-0000-0000-000000000006',
     'Mikael', 'Svensson',
     'Full Stack Developer',
     '+46 70 678 90 12',
     'Stortorget 3, 211 34 Malmö',
     'Ingrid Svensson – +46 73 432 10 98',
     '2024-01-10', '1995-05-07'),

    -- Anna Johansson
    ('00000000-0000-0000-0002-000000000007',
     '00000000-0000-0000-0000-000000000007',
     'Anna', 'Johansson',
     'Data Engineer',
     '+46 70 789 01 23',
     'Sveavägen 22, 113 59 Stockholm',
     'Björn Johansson – +46 73 321 09 87',
     '2023-09-01', '1992-12-11'),

    -- Test Employee (firebase_uid updated separately once known)
    ('00000000-0000-0000-0002-000000000008',
     '00000000-0000-0000-0000-000000000008',
     'Test', 'Employee',
     'Test User',
     NULL, NULL, NULL,
     '2026-01-01', NULL),

    -- Fredrik Norling
    ('00000000-0000-0000-0002-000000000009',
     '00000000-0000-0000-0000-000000000009',
     'Fredrik', 'Norling',
     'Backend Developer',
     '+46 73 456 78 90',
     'Pilgatan 9, 413 13 Göteborg',
     'Lisa Norling – +46 70 234 56 78',
     '2025-01-15', '1995-08-22');


-- =============================================================================
-- BANK INFO
-- =============================================================================
-- These values are stored as plaintext in this seed script.
-- Once an employee saves their bank info through the app the values will be
-- written back through the application layer (with Jasypt encryption if wired).

INSERT INTO bank_info (id, employee_id, bank_name, account_number, clearing_number)
VALUES
    ('00000000-0000-0000-0003-000000000001',
     '00000000-0000-0000-0000-000000000001',
     'Swedbank', '12345678', '8327-9'),

    ('00000000-0000-0000-0003-000000000002',
     '00000000-0000-0000-0000-000000000002',
     'SEB', '98765432', '5000-2'),

    ('00000000-0000-0000-0003-000000000003',
     '00000000-0000-0000-0000-000000000003',
     'Handelsbanken', '55512345', '6000-4'),

    ('00000000-0000-0000-0003-000000000004',
     '00000000-0000-0000-0000-000000000004',
     'Nordea', '44498765', '3300-3'),

    ('00000000-0000-0000-0003-000000000005',
     '00000000-0000-0000-0000-000000000005',
     'SEB', '77712345', '5000-2'),

    ('00000000-0000-0000-0003-000000000006',
     '00000000-0000-0000-0000-000000000006',
     'Swedbank', '33398765', '8327-9'),

    ('00000000-0000-0000-0003-000000000007',
     '00000000-0000-0000-0000-000000000007',
     'ICA Banken', '88812345', '9270-6');


-- =============================================================================
-- EDUCATION
-- =============================================================================

INSERT INTO education (id, employee_id, institution, degree, field, start_year, end_year, description)
VALUES
    -- ── Marcus Karlsson ──────────────────────────────────────────────────────
    ('00000000-0000-0000-0004-000000000001',
     '00000000-0000-0000-0000-000000000001',
     'KTH Royal Institute of Technology',
     'M.Sc.', 'Computer Science & Engineering',
     2006, 2011,
     'Focus on distributed systems and software architecture.'),

    ('00000000-0000-0000-0004-000000000002',
     '00000000-0000-0000-0000-000000000001',
     'Stockholm School of Economics',
     'Executive Education', 'Technology Leadership',
     2018, 2019,
     'Part-time programme for technical founders and CTOs.'),

    -- ── Erik Lindqvist ───────────────────────────────────────────────────────
    ('00000000-0000-0000-0004-000000000003',
     '00000000-0000-0000-0000-000000000002',
     'Chalmers University of Technology',
     'B.Sc.', 'Software Engineering',
     2009, 2012, NULL),

    ('00000000-0000-0000-0004-000000000004',
     '00000000-0000-0000-0000-000000000002',
     'Chalmers University of Technology',
     'M.Sc.', 'Computer Science',
     2012, 2014,
     'Thesis on distributed stream processing with Apache Kafka.'),

    -- ── Sara Berg ────────────────────────────────────────────────────────────
    ('00000000-0000-0000-0004-000000000005',
     '00000000-0000-0000-0000-000000000003',
     'Uppsala University',
     'B.Sc.', 'Computer Science',
     2011, 2014, NULL),

    ('00000000-0000-0000-0004-000000000006',
     '00000000-0000-0000-0000-000000000003',
     'Uppsala University',
     'M.Sc.', 'Software & Computer Systems',
     2014, 2016,
     'Specialised in API design and microservices architecture.'),

    -- ── Johan Petersson ──────────────────────────────────────────────────────
    ('00000000-0000-0000-0004-000000000007',
     '00000000-0000-0000-0000-000000000004',
     'Malmö University',
     'B.Sc.', 'Web Development',
     2011, 2014, NULL),

    ('00000000-0000-0000-0004-000000000008',
     '00000000-0000-0000-0000-000000000004',
     'Lund University',
     'M.Sc.', 'Interaction Design',
     2014, 2016,
     'Thesis on accessibility and inclusive design in web applications.'),

    -- ── Lina Eriksson ────────────────────────────────────────────────────────
    ('00000000-0000-0000-0004-000000000009',
     '00000000-0000-0000-0000-000000000005',
     'Chalmers University of Technology',
     'B.Sc.', 'Computer Networks',
     2008, 2011, NULL),

    ('00000000-0000-0000-0004-000000000010',
     '00000000-0000-0000-0000-000000000005',
     'Chalmers University of Technology',
     'M.Sc.', 'Networks & Distributed Systems',
     2011, 2013,
     'Cloud infrastructure and Kubernetes before it was mainstream.'),

    -- ── Mikael Svensson ──────────────────────────────────────────────────────
    ('00000000-0000-0000-0004-000000000011',
     '00000000-0000-0000-0000-000000000006',
     'Linköping University',
     'B.Sc.', 'Information Technology',
     2013, 2016, NULL),

    ('00000000-0000-0000-0004-000000000012',
     '00000000-0000-0000-0000-000000000006',
     'Linköping University',
     'M.Sc.', 'Software Engineering',
     2016, 2018,
     'Focus on modern web architectures and cloud-native development.'),

    -- ── Anna Johansson ───────────────────────────────────────────────────────
    ('00000000-0000-0000-0004-000000000013',
     '00000000-0000-0000-0000-000000000007',
     'KTH Royal Institute of Technology',
     'M.Sc.', 'Data Science',
     2010, 2015,
     'Focus on large-scale data pipelines and ML infrastructure.'),

    ('00000000-0000-0000-0004-000000000014',
     '00000000-0000-0000-0000-000000000007',
     'Coursera / Stanford University',
     'Certificate', 'Machine Learning',
     2016, 2016,
     'Andrew Ng''s Machine Learning specialisation.'),

    -- ── Test Employee ─────────────────────────────────────────────────────────
    ('00000000-0000-0000-0004-000000000015',
     '00000000-0000-0000-0000-000000000008',
     'Örebro University',
     'B.Sc.', 'Computer Science',
     2018, 2021, NULL),

    ('00000000-0000-0000-0004-000000000016',
     '00000000-0000-0000-0000-000000000008',
     'Örebro University',
     'M.Sc.', 'Software Engineering',
     2021, 2023,
     'Thesis on containerised microservice deployments with Kubernetes.'),

    -- ── Fredrik Norling ───────────────────────────────────────────────────────
    ('00000000-0000-0000-0004-000000000017',
     '00000000-0000-0000-0000-000000000009',
     'Chalmers University of Technology',
     'B.Sc.', 'Software Engineering',
     2014, 2017, NULL),

    ('00000000-0000-0000-0004-000000000018',
     '00000000-0000-0000-0000-000000000009',
     'Chalmers University of Technology',
     'M.Sc.', 'Computer Science',
     2017, 2019,
     'Specialisation in backend systems and distributed architecture.');


-- =============================================================================
-- CLIENTS
-- =============================================================================

INSERT INTO clients (id, company_name, contact_name, contact_email, phone, org_number, status)
VALUES
    ('00000000-0000-0000-0001-000000000001',
     'Spotify AB', 'Johan Svensson', 'johan.svensson@spotify.com',
     '+46 8 123 456 78', '556703-7485', 'ACTIVE'),

    ('00000000-0000-0000-0001-000000000002',
     'Klarna Bank AB', 'Lisa Kaminsky', 'lisa.kaminsky@klarna.com',
     '+46 8 234 567 89', '556737-0431', 'ACTIVE'),

    ('00000000-0000-0000-0001-000000000003',
     'IKEA Digital', 'Anna Ikonen', 'anna.ikonen@inter.ikea.com',
     '+46 42 267 00 00', '556452-3440', 'ACTIVE'),

    ('00000000-0000-0000-0001-000000000004',
     'Volvo Cars', 'Peter Lindberg', 'peter.lindberg@volvocars.com',
     '+46 31 590 00 00', '556074-3089', 'ACTIVE'),

    ('00000000-0000-0000-0001-000000000005',
     'H&M Group', 'Sofia Hernandez', 'sofia.hernandez@hm.com',
     '+46 8 796 55 00', '556042-7220', 'ACTIVE'),

    ('00000000-0000-0000-0001-000000000006',
     'Ericsson AB', 'Magnus Strand', 'magnus.strand@ericsson.com',
     '+46 10 719 00 00', '556016-0680', 'PROSPECT');


-- =============================================================================
-- ASSIGNMENTS
-- =============================================================================
-- At most one ACTIVE assignment per employee (enforced by partial unique index).
-- ENDED assignments are historical and can be multiple per employee.
--
-- Current status (as of 2026-03-16):
--   Erik       → Spotify (ACTIVE, ends 2026-08-31)
--   Sara       → Klarna  (ACTIVE, ends 2026-02-28)  ← renewal expected
--   Johan      → IKEA    (ACTIVE, ends 2026-04-01)  ← ENDING SOON
--   Lina       → unplaced (last assignment ENDED 2025-10-31)
--   Mikael     → Volvo   (ACTIVE, ends 2026-12-31)
--   Anna       → H&M     (ACTIVE, ends 2026-09-14)

INSERT INTO assignments (id, employee_id, client_id, project_name, start_date, end_date, status)
VALUES
    -- Erik @ Spotify (active)
    ('00000000-0000-0000-0005-000000000001',
     '00000000-0000-0000-0000-000000000002',
     '00000000-0000-0000-0001-000000000001',
     'Data Platform Modernisation',
     '2024-09-01', '2026-08-31', 'ACTIVE'),

    -- Erik @ Klarna (historical, ended)
    ('00000000-0000-0000-0005-000000000002',
     '00000000-0000-0000-0000-000000000002',
     '00000000-0000-0000-0001-000000000002',
     'Payments API Refactor',
     '2022-08-01', '2024-08-31', 'ENDED'),

    -- Sara @ Klarna (active)
    ('00000000-0000-0000-0005-000000000003',
     '00000000-0000-0000-0000-000000000003',
     '00000000-0000-0000-0001-000000000002',
     'Open Banking Integration',
     '2024-03-01', '2026-02-28', 'ACTIVE'),

    -- Johan @ IKEA (active, ending within 30 days → shows as ENDING_SOON)
    ('00000000-0000-0000-0005-000000000004',
     '00000000-0000-0000-0000-000000000004',
     '00000000-0000-0000-0001-000000000003',
     'Design System & Component Library',
     '2024-01-15', '2026-04-01', 'ACTIVE'),

    -- Lina @ Ericsson (ended — she is currently unplaced)
    ('00000000-0000-0000-0005-000000000005',
     '00000000-0000-0000-0000-000000000005',
     '00000000-0000-0000-0001-000000000006',
     'Cloud Migration Programme',
     '2022-11-01', '2025-10-31', 'ENDED'),

    -- Mikael @ Volvo (active)
    ('00000000-0000-0000-0005-000000000006',
     '00000000-0000-0000-0000-000000000006',
     '00000000-0000-0000-0001-000000000004',
     'Connected Car Platform',
     '2025-02-01', '2026-12-31', 'ACTIVE'),

    -- Anna @ H&M (active)
    ('00000000-0000-0000-0005-000000000007',
     '00000000-0000-0000-0000-000000000007',
     '00000000-0000-0000-0001-000000000005',
     'Supply Chain Analytics',
     '2024-09-15', '2026-09-14', 'ACTIVE'),

    -- Sara @ Spotify (historical — before Klarna)
    ('00000000-0000-0000-0005-000000000008',
     '00000000-0000-0000-0000-000000000003',
     '00000000-0000-0000-0001-000000000001',
     'Personalisation Engine',
     '2023-03-15', '2024-02-29', 'ENDED'),

    -- Johan @ Volvo Cars (historical — before IKEA)
    ('00000000-0000-0000-0005-000000000009',
     '00000000-0000-0000-0000-000000000004',
     '00000000-0000-0000-0001-000000000004',
     'Fleet Management Portal',
     '2023-06-01', '2024-01-14', 'ENDED'),

    -- Mikael @ Klarna (historical — before Volvo)
    ('00000000-0000-0000-0005-000000000010',
     '00000000-0000-0000-0000-000000000006',
     '00000000-0000-0000-0001-000000000002',
     'Risk & Fraud Detection Dashboard',
     '2024-01-10', '2025-01-31', 'ENDED'),

    -- Anna @ Spotify (historical — before H&M)
    ('00000000-0000-0000-0005-000000000011',
     '00000000-0000-0000-0000-000000000007',
     '00000000-0000-0000-0001-000000000001',
     'Creator Analytics Platform',
     '2023-09-01', '2024-09-14', 'ENDED'),

    -- Test Employee @ Ericsson (historical)
    ('00000000-0000-0000-0005-000000000012',
     '00000000-0000-0000-0000-000000000008',
     '00000000-0000-0000-0001-000000000006',
     'OSS/BSS Modernisation',
     '2023-06-01', '2024-05-31', 'ENDED'),

    -- Test Employee @ IKEA Digital (historical)
    ('00000000-0000-0000-0005-000000000013',
     '00000000-0000-0000-0000-000000000008',
     '00000000-0000-0000-0001-000000000003',
     'Product Catalogue API',
     '2024-07-01', '2025-06-30', 'ENDED'),

    -- Fredrik Norling @ H&M (historical)
    ('00000000-0000-0000-0005-000000000014',
     '00000000-0000-0000-0000-000000000009',
     '00000000-0000-0000-0001-000000000005',
     'E-commerce Backend Rebuild',
     '2025-01-15', '2025-09-30', 'ENDED'),

    -- Fredrik Norling @ Klarna (active)
    ('00000000-0000-0000-0005-000000000015',
     '00000000-0000-0000-0000-000000000009',
     '00000000-0000-0000-0001-000000000002',
     'Checkout Flow Optimisation',
     '2025-10-15', '2026-10-14', 'ACTIVE');


-- =============================================================================
-- VACATION REQUESTS
-- =============================================================================

ALTER TABLE vacation_requests ADD COLUMN IF NOT EXISTS reason VARCHAR(255) NOT NULL DEFAULT 'Semester';
ALTER TABLE vacation_requests ADD COLUMN IF NOT EXISTS rejection_reason TEXT;

INSERT INTO vacation_requests
    (id, employee_id, start_date, end_date, days_count, reason, status, reviewed_by, reviewed_at, rejection_reason)
VALUES
    -- Erik: approved summer vacation 2025 (historical)
    ('00000000-0000-0000-0006-000000000001',
     '00000000-0000-0000-0000-000000000002',
     '2025-07-07', '2025-07-25', 15, 'Semester', 'APPROVED',
     '00000000-0000-0000-0000-000000000001',
     '2025-05-12 10:30:00+00', NULL),

    -- Erik: approved upcoming vacation
    ('00000000-0000-0000-0006-000000000002',
     '00000000-0000-0000-0000-000000000002',
     '2026-06-15', '2026-06-26', 10, 'Semester', 'APPROVED',
     '00000000-0000-0000-0000-000000000001',
     '2026-03-01 09:00:00+00', NULL),

    -- Sara: approved Christmas 2025 (historical)
    ('00000000-0000-0000-0006-000000000003',
     '00000000-0000-0000-0000-000000000003',
     '2025-12-22', '2026-01-02', 8, 'Semester', 'APPROVED',
     '00000000-0000-0000-0000-000000000001',
     '2025-10-08 09:15:00+00', NULL),

    -- Sara: pending request for Easter week
    ('00000000-0000-0000-0006-000000000004',
     '00000000-0000-0000-0000-000000000003',
     '2026-04-06', '2026-04-10', 5, 'Semester', 'PENDING',
     NULL, NULL, NULL),

    -- Johan: pending vacation (week after his IKEA assignment ends)
    ('00000000-0000-0000-0006-000000000005',
     '00000000-0000-0000-0000-000000000004',
     '2026-04-13', '2026-04-17', 5, 'Semester', 'PENDING',
     NULL, NULL, NULL),

    -- Lina: pending upcoming vacation
    ('00000000-0000-0000-0006-000000000006',
     '00000000-0000-0000-0000-000000000005',
     '2026-05-04', '2026-05-15', 10, 'Semester', 'PENDING',
     NULL, NULL, NULL),

    -- Mikael: rejected request (too close to delivery milestone)
    ('00000000-0000-0000-0006-000000000007',
     '00000000-0000-0000-0000-000000000006',
     '2026-03-23', '2026-03-27', 5, 'Semester', 'REJECTED',
     '00000000-0000-0000-0000-000000000001',
     '2026-03-10 14:00:00+00', 'Vi har för få tillgängliga konsulter den perioden.'),

    -- Mikael: approved summer vacation
    ('00000000-0000-0000-0006-000000000008',
     '00000000-0000-0000-0000-000000000006',
     '2026-07-13', '2026-07-24', 10, 'Semester', 'APPROVED',
     '00000000-0000-0000-0000-000000000001',
     '2026-03-01 11:30:00+00', NULL),

    -- Anna: pending upcoming vacation
    ('00000000-0000-0000-0006-000000000009',
     '00000000-0000-0000-0000-000000000007',
     '2026-04-20', '2026-04-30', 9, 'Semester', 'PENDING',
     NULL, NULL, NULL),

    -- Marcus: approved summer vacation
    ('00000000-0000-0000-0006-000000000010',
     '00000000-0000-0000-0000-000000000001',
     '2026-07-06', '2026-07-24', 15, 'Semester', 'APPROVED',
     '00000000-0000-0000-0000-000000000001',
     '2026-02-20 11:00:00+00', NULL),

    -- Johan: approved summer vacation (in addition to his pending April week)
    ('00000000-0000-0000-0006-000000000018',
     '00000000-0000-0000-0000-000000000004',
     '2026-07-27', '2026-08-07', 10, 'Semester', 'APPROVED',
     '00000000-0000-0000-0000-000000000001',
     '2026-03-20 13:00:00+00', NULL);


-- =============================================================================
-- NEWS POSTS
-- =============================================================================
-- The last post is a draft (published_at IS NULL) so only admins see it.

INSERT INTO news_posts (id, title, body, author_id, published_at, pinned)
VALUES
    ('00000000-0000-0000-0007-000000000001',
     'Welcome to the Netler Intranet',
     'We built this platform to make everyday work easier for everyone at Netler. Use it to manage your profile and bank details, submit vacation requests, follow client assignments, and stay up to date with company news and events. Explore the sections in the menu and let us know if you have feedback or ideas for improvements.',
     '00000000-0000-0000-0000-000000000001',
     '2025-09-01 08:00:00+00', TRUE),

    ('00000000-0000-0000-0007-000000000002',
     'New Client: Volvo Cars',
     'We are excited to announce that Netler has started a new multi-year engagement with Volvo Cars. Mikael Svensson will be leading work on their Connected Car Platform, focusing on backend services and cloud infrastructure for next-generation vehicle connectivity. This is exactly the kind of complex, impactful work we love to do. Welcome aboard, Volvo Cars!',
     '00000000-0000-0000-0000-000000000001',
     '2025-12-10 10:00:00+00', FALSE),

    ('00000000-0000-0000-0007-000000000003',
     'Q1 2026 Company Update',
     'We are off to a strong start in 2026. Revenue is up 24% compared to Q1 last year, and we have grown the team by three new consultants. All six of our consultants are currently on active assignments or in final negotiation for their next placement. A huge thank you to everyone who has contributed — client satisfaction scores are at an all-time high across all accounts. The full Q1 report is available in the shared drive.',
     '00000000-0000-0000-0000-000000000001',
     '2026-02-15 09:00:00+00', FALSE),

    ('00000000-0000-0000-0007-000000000004',
     'Johan''s Assignment at IKEA Ending Soon',
     'A heads-up that Johan Petersson''s current assignment at IKEA Digital concludes on April 1st. Johan has done great work building out their Design System and Component Library. We are actively in conversations with several clients about Johan''s next placement. If you have any leads or connections, reach out to Marcus directly.',
     '00000000-0000-0000-0000-000000000001',
     '2026-03-01 08:30:00+00', FALSE),

    -- Draft — not published, only visible to admins
    ('00000000-0000-0000-0007-000000000005',
     'Summer Party 2026 — Save the Date!',
     'We are planning an amazing summer party this year. Block off the last Friday in June — more details to follow on location, activities, and whether partners are welcome. We are looking at venues in Stockholm.',
     '00000000-0000-0000-0000-000000000001',
     NULL, FALSE);


-- =============================================================================
-- EVENTS
-- =============================================================================
-- author_id references the employee who created the event (see V3 migration).

INSERT INTO events (id, title, description, location, event_date, end_date, all_day, author_id)
VALUES
    ('00000000-0000-0000-0008-000000000001',
     'Tech Talk: AI-Assisted Development',
     'Internal knowledge-sharing session on how to use AI coding tools effectively in consulting work. Presented by Erik Lindqvist and Anna Johansson. Bring questions — this will be interactive.',
     'Remote – Zoom link sent by email',
     '2026-03-26', '2026-03-26', FALSE,
     '00000000-0000-0000-0000-000000000001'),

    ('00000000-0000-0000-0008-000000000002',
     'Q2 All-Hands Meeting',
     'Quarterly company meeting covering financials, project updates, upcoming placements, and team news. Attendance required for all employees.',
     'Netler HQ – Storgatan 12, Stockholm',
     '2026-04-10', '2026-04-10', FALSE,
     '00000000-0000-0000-0000-000000000001'),

    ('00000000-0000-0000-0008-000000000003',
     'Salary Review Deadline',
     'All salary review discussions must be completed by this date. If you have not yet had your review with Marcus, reach out to book a slot.',
     NULL,
     '2026-04-30', NULL, TRUE,
     '00000000-0000-0000-0000-000000000001'),

    ('00000000-0000-0000-0008-000000000004',
     'Summer Party 2026',
     'Annual Netler summer party! Food, drinks, and outdoor activities. Partners are welcome. Exact programme to be announced closer to the date.',
     'Fotografiska, Stadsgårdshamnen 22, Stockholm',
     '2026-06-26', '2026-06-26', TRUE,
     '00000000-0000-0000-0000-000000000001');


-- =============================================================================
-- FAQ
-- =============================================================================

INSERT INTO faq (id, question, answer, category, sort_order, author_id) VALUES
    ('00000000-0000-0000-0009-000000000001',
     'How do I submit a vacation request?',
     'Go to the Vacation section in the menu, click "New Request", choose your start and end dates, and submit. Your request will be reviewed by an admin within a few days and you will be notified by email once a decision has been made.',
     'Vacation', 0, '00000000-0000-0000-0000-000000000001'),

    ('00000000-0000-0000-0009-000000000002',
     'Can I cancel a vacation request after submitting it?',
     'Yes, you can cancel a pending request from the Vacation section as long as it has not yet been approved or rejected. Once approved, you will need to contact Marcus directly to cancel.',
     'Vacation', 1, '00000000-0000-0000-0000-000000000001'),

    ('00000000-0000-0000-0009-000000000003',
     'How do I update my personal information?',
     'Navigate to your Profile from the top-right menu. You can update your contact details, address, and emergency contact directly. Your start date can only be updated by an admin.',
     'Profile', 2, '00000000-0000-0000-0000-000000000001'),

    ('00000000-0000-0000-0009-000000000004',
     'How do I update my bank details for salary payments?',
     'Go to Profile → Bank Info. Enter your bank name, clearing number, and account number. This information is stored securely and is only visible to you and admins.',
     'Profile', 3, '00000000-0000-0000-0000-000000000001'),

    ('00000000-0000-0000-0009-000000000005',
     'Who do I contact if I have an issue with my assignment?',
     'Reach out to Marcus Karlsson directly via email or Slack. For urgent matters, call +46 70 123 45 67.',
     'Assignments', 4, '00000000-0000-0000-0000-000000000001'),

    ('00000000-0000-0000-0009-000000000006',
     'How many vacation days am I entitled to per year?',
     'All Netler consultants are entitled to 25 days of paid vacation per calendar year in accordance with Swedish law. Days not taken by December 31st are carried over for up to one additional year.',
     'Vacation', 5, '00000000-0000-0000-0000-000000000001');


-- =============================================================================
-- EVENT RSVP
-- =============================================================================
-- RSVPs for the Tech Talk event (00000000-0000-0000-0008-000000000001)

INSERT INTO event_rsvp (id, event_id, employee_id, status) VALUES
    ('00000000-0000-0000-0013-000000000001',
     '00000000-0000-0000-0008-000000000001', '00000000-0000-0000-0000-000000000001', 'GOING'),
    ('00000000-0000-0000-0013-000000000002',
     '00000000-0000-0000-0008-000000000001', '00000000-0000-0000-0000-000000000002', 'GOING'),
    ('00000000-0000-0000-0013-000000000003',
     '00000000-0000-0000-0008-000000000001', '00000000-0000-0000-0000-000000000003', 'GOING'),
    ('00000000-0000-0000-0013-000000000004',
     '00000000-0000-0000-0008-000000000001', '00000000-0000-0000-0000-000000000004', 'MAYBE'),
    ('00000000-0000-0000-0013-000000000005',
     '00000000-0000-0000-0008-000000000001', '00000000-0000-0000-0000-000000000005', 'NOT_GOING');


-- =============================================================================
-- EMPLOYEE BENEFITS
-- =============================================================================
-- Each employee gets a realistic set of Swedish IT-consultant benefits.
-- Benefits are admin-managed and only visible to admins (and the employee via
-- the admin view). sort_order determines display order.

INSERT INTO employee_benefit (id, employee_id, name, description, sort_order)
VALUES
    -- ── Marcus Karlsson ──────────────────────────────────────────────────────
    ('00000000-0000-0000-000b-000000000001', '00000000-0000-0000-0000-000000000001',
     'ITP1 Pension', 'Defined-contribution pension via Collectum.',                 0),
    ('00000000-0000-0000-000b-000000000002', '00000000-0000-0000-0000-000000000001',
     'Health Insurance', 'Full private health insurance via Bliwa.',                1),
    ('00000000-0000-0000-000b-000000000003', '00000000-0000-0000-0000-000000000001',
     'Wellness Allowance', '5 000 SEK/year friskvårdsbidrag.',                      2),
    ('00000000-0000-0000-000b-000000000004', '00000000-0000-0000-0000-000000000001',
     'Company Car', 'Electric company car — Tesla Model Y.',                        3),
    ('00000000-0000-0000-000b-000000000005', '00000000-0000-0000-0000-000000000001',
     'Phone Allowance', '500 SEK/month towards mobile subscription.',               4),

    -- ── Erik Lindqvist ───────────────────────────────────────────────────────
    ('00000000-0000-0000-000b-000000000006', '00000000-0000-0000-0000-000000000002',
     'ITP1 Pension', 'Defined-contribution pension via Collectum.',                 0),
    ('00000000-0000-0000-000b-000000000007', '00000000-0000-0000-0000-000000000002',
     'Health Insurance', 'Full private health insurance via Bliwa.',                1),
    ('00000000-0000-0000-000b-000000000008', '00000000-0000-0000-0000-000000000002',
     'Wellness Allowance', '5 000 SEK/year friskvårdsbidrag.',                      2),
    ('00000000-0000-0000-000b-000000000009', '00000000-0000-0000-0000-000000000002',
     'Home Office Equipment', 'Annual budget of 10 000 SEK for equipment.',         3),
    ('00000000-0000-0000-000b-000000000010', '00000000-0000-0000-0000-000000000002',
     'Phone Allowance', '500 SEK/month towards mobile subscription.',               4),

    -- ── Sara Berg ────────────────────────────────────────────────────────────
    ('00000000-0000-0000-000b-000000000011', '00000000-0000-0000-0000-000000000003',
     'ITP1 Pension', 'Defined-contribution pension via Collectum.',                 0),
    ('00000000-0000-0000-000b-000000000012', '00000000-0000-0000-0000-000000000003',
     'Health Insurance', 'Full private health insurance via Bliwa.',                1),
    ('00000000-0000-0000-000b-000000000013', '00000000-0000-0000-0000-000000000003',
     'Wellness Allowance', '5 000 SEK/year friskvårdsbidrag.',                      2),
    ('00000000-0000-0000-000b-000000000014', '00000000-0000-0000-0000-000000000003',
     'Home Office Equipment', 'Annual budget of 10 000 SEK for equipment.',         3),

    -- ── Johan Petersson ──────────────────────────────────────────────────────
    ('00000000-0000-0000-000b-000000000015', '00000000-0000-0000-0000-000000000004',
     'ITP1 Pension', 'Defined-contribution pension via Collectum.',                 0),
    ('00000000-0000-0000-000b-000000000016', '00000000-0000-0000-0000-000000000004',
     'Health Insurance', 'Full private health insurance via Bliwa.',                1),
    ('00000000-0000-0000-000b-000000000017', '00000000-0000-0000-0000-000000000004',
     'Wellness Allowance', '5 000 SEK/year friskvårdsbidrag.',                      2),
    ('00000000-0000-0000-000b-000000000018', '00000000-0000-0000-0000-000000000004',
     'Phone Allowance', '500 SEK/month towards mobile subscription.',               3),

    -- ── Lina Eriksson ────────────────────────────────────────────────────────
    ('00000000-0000-0000-000b-000000000019', '00000000-0000-0000-0000-000000000005',
     'ITP1 Pension', 'Defined-contribution pension via Collectum.',                 0),
    ('00000000-0000-0000-000b-000000000020', '00000000-0000-0000-0000-000000000005',
     'Health Insurance', 'Full private health insurance via Bliwa.',                1),
    ('00000000-0000-0000-000b-000000000021', '00000000-0000-0000-0000-000000000005',
     'Wellness Allowance', '5 000 SEK/year friskvårdsbidrag.',                      2),
    ('00000000-0000-0000-000b-000000000022', '00000000-0000-0000-0000-000000000005',
     'Home Office Equipment', 'Annual budget of 10 000 SEK for equipment.',         3),
    ('00000000-0000-0000-000b-000000000023', '00000000-0000-0000-0000-000000000005',
     'Phone Allowance', '500 SEK/month towards mobile subscription.',               4),

    -- ── Mikael Svensson ──────────────────────────────────────────────────────
    ('00000000-0000-0000-000b-000000000024', '00000000-0000-0000-0000-000000000006',
     'ITP1 Pension', 'Defined-contribution pension via Collectum.',                 0),
    ('00000000-0000-0000-000b-000000000025', '00000000-0000-0000-0000-000000000006',
     'Health Insurance', 'Full private health insurance via Bliwa.',                1),
    ('00000000-0000-0000-000b-000000000026', '00000000-0000-0000-0000-000000000006',
     'Wellness Allowance', '5 000 SEK/year friskvårdsbidrag.',                      2),
    ('00000000-0000-0000-000b-000000000027', '00000000-0000-0000-0000-000000000006',
     'Home Office Equipment', 'Annual budget of 10 000 SEK for equipment.',         3),

    -- ── Anna Johansson ───────────────────────────────────────────────────────
    ('00000000-0000-0000-000b-000000000028', '00000000-0000-0000-0000-000000000007',
     'ITP1 Pension', 'Defined-contribution pension via Collectum.',                 0),
    ('00000000-0000-0000-000b-000000000029', '00000000-0000-0000-0000-000000000007',
     'Health Insurance', 'Full private health insurance via Bliwa.',                1),
    ('00000000-0000-0000-000b-000000000030', '00000000-0000-0000-0000-000000000007',
     'Wellness Allowance', '5 000 SEK/year friskvårdsbidrag.',                      2),
    ('00000000-0000-0000-000b-000000000031', '00000000-0000-0000-0000-000000000007',
     'Conference Budget', '20 000 SEK/year for tech conferences and training.',     3),
    ('00000000-0000-0000-000b-000000000032', '00000000-0000-0000-0000-000000000007',
     'Phone Allowance', '500 SEK/month towards mobile subscription.',               4),

    -- ── Test Employee ─────────────────────────────────────────────────────────
    ('00000000-0000-0000-000b-000000000033', '00000000-0000-0000-0000-000000000008',
     'ITP1 Pension', 'Defined-contribution pension via Collectum.',                 0),
    ('00000000-0000-0000-000b-000000000034', '00000000-0000-0000-0000-000000000008',
     'Health Insurance', 'Full private health insurance via Bliwa.',                1),
    ('00000000-0000-0000-000b-000000000035', '00000000-0000-0000-0000-000000000008',
     'Wellness Allowance', '5 000 SEK/year friskvårdsbidrag.',                      2),

    -- ── Fredrik Norling ───────────────────────────────────────────────────────
    ('00000000-0000-0000-000b-000000000036', '00000000-0000-0000-0000-000000000009',
     'ITP1 Pension', 'Defined-contribution pension via Collectum.',                 0),
    ('00000000-0000-0000-000b-000000000037', '00000000-0000-0000-0000-000000000009',
     'Health Insurance', 'Full private health insurance via Bliwa.',                1),
    ('00000000-0000-0000-000b-000000000038', '00000000-0000-0000-0000-000000000009',
     'Wellness Allowance', '5 000 SEK/year friskvårdsbidrag.',                      2),
    ('00000000-0000-0000-000b-000000000039', '00000000-0000-0000-0000-000000000009',
     'Home Office Equipment', 'Annual budget of 10 000 SEK for equipment.',         3),
    ('00000000-0000-0000-000b-000000000040', '00000000-0000-0000-0000-000000000009',
     'Phone Allowance', '500 SEK/month towards mobile subscription.',               4);


-- =============================================================================
-- SKILLS
-- =============================================================================

INSERT INTO skills (id, name) VALUES
    ('00000000-0000-0000-000c-000000000001', 'Java'),
    ('00000000-0000-0000-000c-000000000002', 'Spring Boot'),
    ('00000000-0000-0000-000c-000000000003', 'PostgreSQL'),
    ('00000000-0000-0000-000c-000000000004', 'Docker'),
    ('00000000-0000-0000-000c-000000000005', 'Kubernetes'),
    ('00000000-0000-0000-000c-000000000006', 'Terraform'),
    ('00000000-0000-0000-000c-000000000007', 'React'),
    ('00000000-0000-0000-000c-000000000008', 'TypeScript'),
    ('00000000-0000-0000-000c-000000000009', 'Node.js'),
    ('00000000-0000-0000-000c-000000000010', 'GraphQL'),
    ('00000000-0000-0000-000c-000000000011', 'Python'),
    ('00000000-0000-0000-000c-000000000012', 'Apache Kafka'),
    ('00000000-0000-0000-000c-000000000013', 'Redis'),
    ('00000000-0000-0000-000c-000000000014', 'AWS'),
    ('00000000-0000-0000-000c-000000000015', 'Azure'),
    ('00000000-0000-0000-000c-000000000016', 'CI/CD'),
    ('00000000-0000-0000-000c-000000000017', 'REST API Design'),
    ('00000000-0000-0000-000c-000000000018', 'Microservices'),
    ('00000000-0000-0000-000c-000000000019', 'Machine Learning'),
    ('00000000-0000-0000-000c-000000000020', 'Data Engineering');


-- =============================================================================
-- EMPLOYEE SKILLS
-- =============================================================================

INSERT INTO employee_skills (employee_id, skill_id) VALUES
    -- Marcus Karlsson (CTO): Java, Spring Boot, Microservices, AWS, Docker, REST API Design
    ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-000c-000000000001'),
    ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-000c-000000000002'),
    ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-000c-000000000018'),
    ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-000c-000000000014'),
    ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-000c-000000000004'),
    ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-000c-000000000017'),

    -- Erik Lindqvist (Senior Backend): Java, Spring Boot, PostgreSQL, Kafka, Microservices, Docker, REST API Design
    ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-000c-000000000001'),
    ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-000c-000000000002'),
    ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-000c-000000000003'),
    ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-000c-000000000012'),
    ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-000c-000000000018'),
    ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-000c-000000000004'),
    ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-000c-000000000017'),

    -- Sara Berg (Backend): Java, Spring Boot, PostgreSQL, Docker, Kubernetes, REST API Design
    ('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-000c-000000000001'),
    ('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-000c-000000000002'),
    ('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-000c-000000000003'),
    ('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-000c-000000000004'),
    ('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-000c-000000000005'),
    ('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-000c-000000000017'),

    -- Johan Petersson (Frontend): React, TypeScript, Node.js, GraphQL, Docker
    ('00000000-0000-0000-0000-000000000004', '00000000-0000-0000-000c-000000000007'),
    ('00000000-0000-0000-0000-000000000004', '00000000-0000-0000-000c-000000000008'),
    ('00000000-0000-0000-0000-000000000004', '00000000-0000-0000-000c-000000000009'),
    ('00000000-0000-0000-0000-000000000004', '00000000-0000-0000-000c-000000000010'),
    ('00000000-0000-0000-0000-000000000004', '00000000-0000-0000-000c-000000000004'),

    -- Lina Eriksson (DevOps): Docker, Kubernetes, Terraform, AWS, Azure, CI/CD
    ('00000000-0000-0000-0000-000000000005', '00000000-0000-0000-000c-000000000004'),
    ('00000000-0000-0000-0000-000000000005', '00000000-0000-0000-000c-000000000005'),
    ('00000000-0000-0000-0000-000000000005', '00000000-0000-0000-000c-000000000006'),
    ('00000000-0000-0000-0000-000000000005', '00000000-0000-0000-000c-000000000014'),
    ('00000000-0000-0000-0000-000000000005', '00000000-0000-0000-000c-000000000015'),
    ('00000000-0000-0000-0000-000000000005', '00000000-0000-0000-000c-000000000016'),

    -- Mikael Svensson (Full Stack): Java, Spring Boot, React, TypeScript, Docker, PostgreSQL
    ('00000000-0000-0000-0000-000000000006', '00000000-0000-0000-000c-000000000001'),
    ('00000000-0000-0000-0000-000000000006', '00000000-0000-0000-000c-000000000002'),
    ('00000000-0000-0000-0000-000000000006', '00000000-0000-0000-000c-000000000007'),
    ('00000000-0000-0000-0000-000000000006', '00000000-0000-0000-000c-000000000008'),
    ('00000000-0000-0000-0000-000000000006', '00000000-0000-0000-000c-000000000004'),
    ('00000000-0000-0000-0000-000000000006', '00000000-0000-0000-000c-000000000003'),

    -- Anna Johansson (Data Engineer): Python, Kafka, Data Engineering, Machine Learning, AWS, PostgreSQL
    ('00000000-0000-0000-0000-000000000007', '00000000-0000-0000-000c-000000000011'),
    ('00000000-0000-0000-0000-000000000007', '00000000-0000-0000-000c-000000000012'),
    ('00000000-0000-0000-0000-000000000007', '00000000-0000-0000-000c-000000000020'),
    ('00000000-0000-0000-0000-000000000007', '00000000-0000-0000-000c-000000000019'),
    ('00000000-0000-0000-0000-000000000007', '00000000-0000-0000-000c-000000000014'),
    ('00000000-0000-0000-0000-000000000007', '00000000-0000-0000-000c-000000000003'),

    -- Test Employee: Java, React, Docker
    ('00000000-0000-0000-0000-000000000008', '00000000-0000-0000-000c-000000000001'),
    ('00000000-0000-0000-0000-000000000008', '00000000-0000-0000-000c-000000000007'),
    ('00000000-0000-0000-0000-000000000008', '00000000-0000-0000-000c-000000000004'),

    -- Fredrik Norling (Backend): Java, Spring Boot, REST API Design, PostgreSQL, Docker, Kubernetes
    ('00000000-0000-0000-0000-000000000009', '00000000-0000-0000-000c-000000000001'),
    ('00000000-0000-0000-0000-000000000009', '00000000-0000-0000-000c-000000000002'),
    ('00000000-0000-0000-0000-000000000009', '00000000-0000-0000-000c-000000000017'),
    ('00000000-0000-0000-0000-000000000009', '00000000-0000-0000-000c-000000000003'),
    ('00000000-0000-0000-0000-000000000009', '00000000-0000-0000-000c-000000000004'),
    ('00000000-0000-0000-0000-000000000009', '00000000-0000-0000-000c-000000000005');


-- =============================================================================
-- EVENT RSVP — additional events
-- =============================================================================
-- Q2 All-Hands (00000000-0000-0000-0008-000000000002) — attendance required
-- Summer Party (00000000-0000-0000-0008-000000000004) — mix of responses

INSERT INTO event_rsvp (id, event_id, employee_id, status) VALUES
    -- Q2 All-Hands: everyone going
    ('00000000-0000-0000-0013-000000000006',
     '00000000-0000-0000-0008-000000000002', '00000000-0000-0000-0000-000000000001', 'GOING'),
    ('00000000-0000-0000-0013-000000000007',
     '00000000-0000-0000-0008-000000000002', '00000000-0000-0000-0000-000000000002', 'GOING'),
    ('00000000-0000-0000-0013-000000000008',
     '00000000-0000-0000-0008-000000000002', '00000000-0000-0000-0000-000000000003', 'GOING'),
    ('00000000-0000-0000-0013-000000000009',
     '00000000-0000-0000-0008-000000000002', '00000000-0000-0000-0000-000000000004', 'GOING'),
    ('00000000-0000-0000-0013-000000000010',
     '00000000-0000-0000-0008-000000000002', '00000000-0000-0000-0000-000000000005', 'GOING'),
    ('00000000-0000-0000-0013-000000000011',
     '00000000-0000-0000-0008-000000000002', '00000000-0000-0000-0000-000000000006', 'GOING'),
    ('00000000-0000-0000-0013-000000000012',
     '00000000-0000-0000-0008-000000000002', '00000000-0000-0000-0000-000000000007', 'GOING'),
    ('00000000-0000-0000-0013-000000000013',
     '00000000-0000-0000-0008-000000000002', '00000000-0000-0000-0000-000000000009', 'MAYBE'),

    -- Summer Party: mostly going, a couple of maybes
    ('00000000-0000-0000-0013-000000000014',
     '00000000-0000-0000-0008-000000000004', '00000000-0000-0000-0000-000000000001', 'GOING'),
    ('00000000-0000-0000-0013-000000000015',
     '00000000-0000-0000-0008-000000000004', '00000000-0000-0000-0000-000000000002', 'GOING'),
    ('00000000-0000-0000-0013-000000000016',
     '00000000-0000-0000-0008-000000000004', '00000000-0000-0000-0000-000000000003', 'GOING'),
    ('00000000-0000-0000-0013-000000000017',
     '00000000-0000-0000-0008-000000000004', '00000000-0000-0000-0000-000000000004', 'GOING'),
    ('00000000-0000-0000-0013-000000000018',
     '00000000-0000-0000-0008-000000000004', '00000000-0000-0000-0000-000000000005', 'MAYBE'),
    ('00000000-0000-0000-0013-000000000019',
     '00000000-0000-0000-0008-000000000004', '00000000-0000-0000-0000-000000000006', 'GOING'),
    ('00000000-0000-0000-0013-000000000020',
     '00000000-0000-0000-0008-000000000004', '00000000-0000-0000-0000-000000000007', 'GOING'),
    ('00000000-0000-0000-0013-000000000021',
     '00000000-0000-0000-0008-000000000004', '00000000-0000-0000-0000-000000000009', 'MAYBE');


-- =============================================================================
-- FAQ — additional entries
-- =============================================================================

INSERT INTO faq (id, question, answer, category, sort_order, author_id) VALUES
    ('00000000-0000-0000-0009-000000000007',
     'How do I add or update my skills in my profile?',
     'Go to your Profile and scroll to the Skills section. You can add skills from the list or ask Marcus to add a new skill that is missing. Skills are visible to admins and used to match consultants to upcoming client assignments.',
     'Profile', 6, '00000000-0000-0000-0000-000000000001'),

    ('00000000-0000-0000-0009-000000000008',
     'How are assignments typically structured at Netler?',
     'Assignments are client engagements where you work on-site or remotely for a specific client. Most run between 6 and 24 months. Your assignment details, including project name, start date, and end date, are visible in the Placements section of the intranet.',
     'Assignments', 7, '00000000-0000-0000-0000-000000000001'),

    ('00000000-0000-0000-0009-000000000009',
     'What happens when my assignment ends?',
     'When your assignment concludes you will appear as available in the system. Marcus will be in touch well in advance to discuss your next placement. Aim to flag any concerns about an upcoming end date at least 60 days before it happens.',
     'Assignments', 8, '00000000-0000-0000-0000-000000000001'),

    ('00000000-0000-0000-0009-000000000010',
     'How do I RSVP to a company event?',
     'Open the Events section from the menu and click on the event. You will see options to mark yourself as Going, Maybe, or Not Going. RSVPs help us plan catering, venue space, and logistics, so please respond as early as possible.',
     'Events', 9, '00000000-0000-0000-0000-000000000001'),

    ('00000000-0000-0000-0009-000000000011',
     'What benefits am I entitled to?',
     'Your benefits are listed under Profile → Benefits. Standard benefits for all Netler employees include ITP1 pension via Collectum, private health insurance via Bliwa, and a 5 000 SEK annual wellness allowance. Individual benefits such as a home office budget, conference budget, or phone allowance may also apply depending on your contract.',
     'Benefits', 10, '00000000-0000-0000-0000-000000000001'),

    ('00000000-0000-0000-0009-000000000012',
     'Who do I contact about salary or contract questions?',
     'Contact Marcus Karlsson directly at marcus.karlsson@netler.com or on Slack. Salary reviews are conducted annually, typically in April. Your employment contract is available under Profile → Contract.',
     'General', 11, '00000000-0000-0000-0000-000000000001');


-- =============================================================================
-- NEWS POSTS — additional entries
-- =============================================================================

INSERT INTO news_posts (id, title, body, author_id, published_at, pinned)
VALUES
    ('00000000-0000-0000-0007-000000000006',
     'New Client Partnership: Ericsson',
     '<p>We are excited to share that Netler has signed a new framework agreement with Ericsson. This opens the door for multiple consultant placements within their cloud and network infrastructure teams over the coming years.</p><p>Lina Eriksson''s previous work at Ericsson was instrumental in building this relationship. A big thank you to Lina for her outstanding work and professionalism throughout that engagement.</p><p>We are actively scoping the first placement — if you have a background in cloud infrastructure, Kubernetes, or CI/CD, keep an eye out for more details soon.</p>',
     '00000000-0000-0000-0000-000000000001',
     '2026-01-20 09:00:00+00', FALSE),

    ('00000000-0000-0000-0007-000000000007',
     'Welcome Fredrik Norling!',
     '<p>We are thrilled to welcome Fredrik Norling to the Netler team! Fredrik joins us as a Backend Developer with a strong focus on Java, Spring Boot, and distributed systems.</p><p>Fredrik hit the ground running and is already on assignment at Klarna working on the Checkout Flow Optimisation project. Welcome aboard, Fredrik — glad to have you with us!</p>',
     '00000000-0000-0000-0000-000000000001',
     '2026-01-22 10:00:00+00', FALSE),

    ('00000000-0000-0000-0007-000000000008',
     'Salary Reviews — Book Your Slot for April',
     '<p>Annual salary reviews are coming up in April. All employees should book a 30-minute slot with Marcus before April 15th.</p><p>To prepare, think about your contributions over the past year, any skills you have developed, and your goals for the next 12 months. The review is a two-way conversation — come with questions.</p><p>Slots can be booked directly via the calendar link Marcus will send over Slack. The deadline for completing all reviews is April 30th.</p>',
     '00000000-0000-0000-0000-000000000001',
     '2026-03-10 08:00:00+00', FALSE);


-- =============================================================================
-- DEMO USERS — Philip Olsson (admin), Philip Schill (admin) & Petra (admin)
-- =============================================================================

INSERT INTO employees (id, firebase_uid, email, role, is_active) VALUES
    ('00000000-0000-0000-0000-000000000010', 'WPFQhIsBrvTG2ve5fplw26sfdbC2', 'philip.olsson@netler.com',  'SUPERADMIN', TRUE),
    ('00000000-0000-0000-0000-000000000011', '1OGSEIXFLoYL063TLgEqZ3zb8rX2', 'philip.schill@netler.com', 'SUPERADMIN', TRUE),
    ('00000000-0000-0000-0000-000000000013', 'petra-netler-uid',              'petra@netler.com',          'ADMIN', TRUE);

INSERT INTO employee_profiles
    (id, employee_id, first_name, last_name, job_title, phone, address, emergency_contact, start_date, birth_date)
VALUES
    ('00000000-0000-0000-0002-000000000010',
     '00000000-0000-0000-0000-000000000010',
     'Philip', 'Olsson', 'Account Manager',
     '+46 70 111 22 33',
     'Birger Jarlsgatan 8, 114 34 Stockholm',
     'Anna Olsson – +46 73 111 22 44',
     '2023-05-01', '1990-06-15'),

    ('00000000-0000-0000-0002-000000000011',
     '00000000-0000-0000-0000-000000000011',
     'Philip', 'Schill', 'Frontend Developer',
     '+46 70 222 33 44',
     'Linnégatan 14, 413 04 Göteborg',
     'Sofia Schill – +46 73 222 33 55',
     '2024-04-01', '1997-03-08'),

    ('00000000-0000-0000-0002-000000000013',
     '00000000-0000-0000-0000-000000000013',
     'Petra', 'Lichtenecker', 'Operations Manager',
     '+46 70 444 55 66',
     'Drottninggatan 30, 111 51 Stockholm',
     'Anders Lindström – +46 73 444 55 77',
     '2022-02-01', '1985-11-28');

INSERT INTO bank_info (id, employee_id, bank_name, account_number, clearing_number) VALUES
    ('00000000-0000-0000-0003-000000000010',
     '00000000-0000-0000-0000-000000000010',
     'Swedbank', '91234567', '8327-9'),

    ('00000000-0000-0000-0003-000000000011',
     '00000000-0000-0000-0000-000000000011',
     'Nordea', '82345678', '3300-3'),

    ('00000000-0000-0000-0003-000000000013',
     '00000000-0000-0000-0000-000000000013',
     'Handelsbanken', '63219876', '6000-0');

INSERT INTO education (id, employee_id, institution, degree, field, start_year, end_year, description) VALUES
    ('00000000-0000-0000-0004-000000000019',
     '00000000-0000-0000-0000-000000000010',
     'Stockholm School of Economics',
     'B.Sc.', 'Business Administration',
     2009, 2012,
     'Focus on sales, marketing, and business development.'),

    ('00000000-0000-0000-0004-000000000020',
     '00000000-0000-0000-0000-000000000010',
     'Stockholm School of Economics',
     'M.Sc.', 'Management',
     2012, 2014,
     'Specialisation in technology business and client relations.'),

    ('00000000-0000-0000-0004-000000000021',
     '00000000-0000-0000-0000-000000000011',
     'Chalmers University of Technology',
     'B.Sc.', 'Computer Science',
     2016, 2019,
     'Focus on web technologies and user interface development.'),

    ('00000000-0000-0000-0004-000000000022',
     '00000000-0000-0000-0000-000000000011',
     'Chalmers University of Technology',
     'M.Sc.', 'Interaction Design & Technologies',
     2019, 2021,
     'Thesis on component-driven design systems for large-scale web applications.'),

    ('00000000-0000-0000-0004-000000000023',
     '00000000-0000-0000-0000-000000000013',
     'Uppsala University',
     'B.Sc.', 'Business and Economics',
     2004, 2007,
     'Focus on organisational management and HR.'),

    ('00000000-0000-0000-0004-000000000024',
     '00000000-0000-0000-0000-000000000013',
     'Uppsala University',
     'M.Sc.', 'Human Resource Management',
     2007, 2009,
     'Specialisation in operational leadership and process improvement.');

-- Philip Schill: active assignment at Spotify alongside Erik
INSERT INTO assignments (id, employee_id, client_id, project_name, start_date, end_date, status) VALUES
    ('00000000-0000-0000-0005-000000000016',
     '00000000-0000-0000-0000-000000000011',
     '00000000-0000-0000-0001-000000000001',
     'Creator Dashboard Redesign',
     '2024-04-01', '2026-12-31', 'ACTIVE');

INSERT INTO employee_benefit (id, employee_id, name, description, sort_order) VALUES
    ('00000000-0000-0000-000b-000000000041', '00000000-0000-0000-0000-000000000010',
     'ITP1 Pension',       'Defined-contribution pension via Collectum.',    0),
    ('00000000-0000-0000-000b-000000000042', '00000000-0000-0000-0000-000000000010',
     'Health Insurance',   'Full private health insurance via Bliwa.',       1),
    ('00000000-0000-0000-000b-000000000043', '00000000-0000-0000-0000-000000000010',
     'Wellness Allowance', '5 000 SEK/year friskvårdsbidrag.',               2),
    ('00000000-0000-0000-000b-000000000044', '00000000-0000-0000-0000-000000000010',
     'Company Car',        'Electric company car — Volvo EX40.',             3),

    ('00000000-0000-0000-000b-000000000045', '00000000-0000-0000-0000-000000000011',
     'ITP1 Pension',       'Defined-contribution pension via Collectum.',    0),
    ('00000000-0000-0000-000b-000000000046', '00000000-0000-0000-0000-000000000011',
     'Health Insurance',   'Full private health insurance via Bliwa.',       1),
    ('00000000-0000-0000-000b-000000000047', '00000000-0000-0000-0000-000000000011',
     'Wellness Allowance', '5 000 SEK/year friskvårdsbidrag.',               2),
    ('00000000-0000-0000-000b-000000000048', '00000000-0000-0000-0000-000000000011',
     'Phone Allowance',    '500 SEK/month towards mobile subscription.',     3),

    ('00000000-0000-0000-000b-000000000051', '00000000-0000-0000-0000-000000000013',
     'ITP1 Pension',       'Defined-contribution pension via Collectum.',    0),
    ('00000000-0000-0000-000b-000000000052', '00000000-0000-0000-0000-000000000013',
     'Health Insurance',   'Full private health insurance via Bliwa.',       1),
    ('00000000-0000-0000-000b-000000000053', '00000000-0000-0000-0000-000000000013',
     'Wellness Allowance', '5 000 SEK/year friskvårdsbidrag.',               2),
    ('00000000-0000-0000-000b-000000000054', '00000000-0000-0000-0000-000000000013',
     'Company Car',        'Electric company car — Volvo EX40.',             3);

INSERT INTO employee_skills (employee_id, skill_id) VALUES
    -- Philip Schill (Frontend): React, TypeScript, Node.js, GraphQL, Docker
    ('00000000-0000-0000-0000-000000000011', '00000000-0000-0000-000c-000000000007'),
    ('00000000-0000-0000-0000-000000000011', '00000000-0000-0000-000c-000000000008'),
    ('00000000-0000-0000-0000-000000000011', '00000000-0000-0000-000c-000000000009'),
    ('00000000-0000-0000-0000-000000000011', '00000000-0000-0000-000c-000000000010'),
    ('00000000-0000-0000-0000-000000000011', '00000000-0000-0000-000c-000000000004'),
    -- Petra Lindström (Operations): Python, AWS, CI/CD, Kubernetes
    ('00000000-0000-0000-0000-000000000013', '00000000-0000-0000-000c-000000000011'),
    ('00000000-0000-0000-0000-000000000013', '00000000-0000-0000-000c-000000000014'),
    ('00000000-0000-0000-0000-000000000013', '00000000-0000-0000-000c-000000000016'),
    ('00000000-0000-0000-0000-000000000013', '00000000-0000-0000-000c-000000000005');

INSERT INTO vacation_requests
    (id, employee_id, start_date, end_date, days_count, reason, status, reviewed_by, reviewed_at, rejection_reason)
VALUES
    -- Philip Olsson: approved midsummer week
    ('00000000-0000-0000-0006-000000000011',
     '00000000-0000-0000-0000-000000000010',
     '2026-06-19', '2026-06-26', 6, 'Semester', 'APPROVED',
     '00000000-0000-0000-0000-000000000001',
     '2026-03-05 09:00:00+00', NULL),

    -- Philip Olsson: pending late summer week
    ('00000000-0000-0000-0006-000000000012',
     '00000000-0000-0000-0000-000000000010',
     '2026-08-10', '2026-08-14', 5, 'Semester', 'PENDING',
     NULL, NULL, NULL),

    -- Philip Schill: pending Easter week
    ('00000000-0000-0000-0006-000000000013',
     '00000000-0000-0000-0000-000000000011',
     '2026-04-06', '2026-04-10', 5, 'Semester', 'PENDING',
     NULL, NULL, NULL),

    -- Philip Schill: approved summer vacation
    ('00000000-0000-0000-0006-000000000014',
     '00000000-0000-0000-0000-000000000011',
     '2026-07-20', '2026-08-07', 15, 'Semester', 'APPROVED',
     '00000000-0000-0000-0000-000000000001',
     '2026-03-10 10:00:00+00', NULL),

    -- Petra: pending tjänstledighet
    ('00000000-0000-0000-0006-000000000015',
     '00000000-0000-0000-0000-000000000013',
     '2026-04-30', '2026-05-04', 3, 'Tjänstledighet', 'PENDING',
     NULL, NULL, NULL),

    -- Petra: approved föräldraledighet
    ('00000000-0000-0000-0006-000000000016',
     '00000000-0000-0000-0000-000000000013',
     '2026-06-01', '2026-06-12', 10, 'Föräldraledighet', 'APPROVED',
     '00000000-0000-0000-0000-000000000001',
     '2026-03-18 08:30:00+00', NULL);


-- =============================================================================
-- TEST CASE: Emma Holmgren @ Tele2
-- Purpose: verify that Tele2 becomes INACTIVE when her expired assignment
--          is ended (via endAssignment API or the nightly scheduler).
--          Tele2 has only this one consultant — so ending it should flip
--          the client from ACTIVE → INACTIVE immediately.
-- =============================================================================

INSERT INTO employees (id, firebase_uid, email, role, is_active) VALUES
    ('00000000-0000-0000-0000-000000000012', 'emma-netler-uid',
     'emma.holmgren@netler.com', 'EMPLOYEE', TRUE);

INSERT INTO employee_profiles
    (id, employee_id, first_name, last_name, job_title, phone, address, emergency_contact, start_date, birth_date)
VALUES
    ('00000000-0000-0000-0002-000000000012',
     '00000000-0000-0000-0000-000000000012',
     'Emma', 'Holmgren',
     'Frontend Developer',
     '+46 70 555 12 34',
     'Linnégatan 22, 413 04 Göteborg',
     'Lars Holmgren – +46 73 555 43 21',
     '2024-01-15', '1996-09-11');

INSERT INTO clients (id, company_name, contact_name, contact_email, phone, org_number, status)
VALUES
    ('00000000-0000-0000-0001-000000000007',
     'Tele2 AB', 'Sofia Lundgren', 'sofia.lundgren@tele2.com',
     '+46 8 555 90 00', '556267-5164', 'ACTIVE');

-- Assignment is ACTIVE in DB but endDate has already passed (2026-02-28).
-- The nightly scheduler or a manual PUT /api/assignments/{id}/end call
-- should set it to ENDED and flip Tele2 → INACTIVE.
INSERT INTO assignments (id, employee_id, client_id, project_name, start_date, end_date, status)
VALUES
    ('00000000-0000-0000-0005-000000000017',
     '00000000-0000-0000-0000-000000000012',
     '00000000-0000-0000-0001-000000000007',
     'My Tele2 App Redesign',
     '2025-03-01', '2026-02-28', 'ACTIVE');

INSERT INTO bank_info (id, employee_id, bank_name, account_number, clearing_number) VALUES
    ('00000000-0000-0000-0003-000000000012',
     '00000000-0000-0000-0000-000000000012',
     'SEB', '52109988776', '5000');

INSERT INTO employee_benefit (id, employee_id, name, description, sort_order) VALUES
    ('00000000-0000-0000-000b-000000000049', '00000000-0000-0000-0000-000000000012',
     'ITP1 Pension',       'Defined-contribution pension via Collectum.',  0),
    ('00000000-0000-0000-000b-000000000050', '00000000-0000-0000-0000-000000000012',
     'Wellness Allowance', '5 000 SEK/year friskvårdsbidrag.',             1);

INSERT INTO employee_skills (employee_id, skill_id) VALUES
    -- Emma (Frontend): React, TypeScript, GraphQL
    ('00000000-0000-0000-0000-000000000012', '00000000-0000-0000-000c-000000000007'),
    ('00000000-0000-0000-0000-000000000012', '00000000-0000-0000-000c-000000000008'),
    ('00000000-0000-0000-0000-000000000012', '00000000-0000-0000-000c-000000000010');

INSERT INTO vacation_requests
    (id, employee_id, start_date, end_date, days_count, reason, status, reviewed_by, reviewed_at, rejection_reason)
VALUES
    -- Emma: pending spring vacation
    ('00000000-0000-0000-0006-000000000017',
     '00000000-0000-0000-0000-000000000012',
     '2026-04-27', '2026-05-08', 10, 'Semester', 'PENDING',
     NULL, NULL, NULL);


-- =============================================================================
-- CANDIDATES
-- =============================================================================

INSERT INTO candidates (id, name, role, email, phone, notes, stage, created_at, updated_at)
VALUES
    ('00000000-0000-0000-0006-000000000001',
     'Sofia Ekström', 'Fullstack-utvecklare',
     'sofia.ekstrom@example.com', '070-111 22 33',
     'Stark React- och Spring Boot-bakgrund. Söker nytt uppdrag från maj.',
     0, NOW(), NOW()),

    ('00000000-0000-0000-0006-000000000002',
     'Mattias Holm', 'Backend Developer',
     'mattias.holm@example.com', '070-222 33 44',
     'Java/Kotlin-specialist med 6 års erfarenhet. Möte bokat nästa vecka.',
     1, NOW(), NOW()),

    ('00000000-0000-0000-0006-000000000003',
     'Rebecka Strand', 'DevOps Engineer',
     'rebecka.strand@example.com', '073-333 44 55',
     'Kubernetes, Terraform och AWS. Intervju genomförd, mycket positiv.',
     2, NOW(), NOW()),

    ('00000000-0000-0000-0006-000000000004',
     'Oscar Lindberg', 'Frontend Developer',
     'oscar.lindberg@example.com', NULL,
     'Specialiserad på React och TypeScript. Referenskoll pågår.',
     3, NOW(), NOW()),

    ('00000000-0000-0000-0006-000000000005',
     'Camilla Åberg', 'Data Engineer',
     'camilla.aberg@example.com', '076-555 66 77',
     'Python, Apache Kafka och Spark. Erbjudande skickat igår.',
     4, NOW(), NOW()),

    ('00000000-0000-0000-0006-000000000006',
     'Henrik Norén', 'Backend Developer',
     'henrik.noren@example.com', '070-666 77 88',
     'Anställd från 2026-04-01. Startdatum bekräftat.',
     5, NOW(), NOW()),

    ('00000000-0000-0000-0006-000000000007',
     'Frida Malmqvist', 'UX/UI Designer',
     'frida.malmqvist@example.com', '073-777 88 99',
     'Portfolio imponerade. Lämplig för produktteam hos kund.',
     0, NOW(), NOW()),

    ('00000000-0000-0000-0006-000000000008',
     'Jonas Bergström', 'Tech Lead',
     'jonas.bergstrom@example.com', '070-888 99 00',
     '12 års erfarenhet, van att leda team på 5–10 personer.',
     1, NOW(), NOW());


-- =============================================================================
-- BOARDS
-- =============================================================================

INSERT INTO boards (id, name, created_by, updated_by, created_at, updated_at)
VALUES
    ('00000000-0000-0000-0007-000000000001', 'Rekryteringspipeline',    'petra@netler.com',              'petra@netler.com',              NOW(), NOW()),
    ('00000000-0000-0000-0007-000000000002', 'Onboarding – Nyanställda','marcus.karlsson@netler.com',    'marcus.karlsson@netler.com',    NOW(), NOW()),
    ('00000000-0000-0000-0007-000000000003', 'Kundpipeline',            'marcus.karlsson@netler.com',    'marcus.karlsson@netler.com',    NOW(), NOW());

-- =============================================================================
-- BOARD COLUMNS
-- =============================================================================

INSERT INTO board_columns (id, board_id, title, color_index, position, created_by, updated_by, created_at, updated_at)
VALUES
    -- Rekryteringspipeline
    ('00000000-0000-0000-0008-000000000001', '00000000-0000-0000-0007-000000000001', 'Nya kandidater',      0, 0, 'petra@netler.com', 'petra@netler.com', NOW(), NOW()),
    ('00000000-0000-0000-0008-000000000002', '00000000-0000-0000-0007-000000000001', 'Första kontakt',      1, 1, 'petra@netler.com', 'petra@netler.com', NOW(), NOW()),
    ('00000000-0000-0000-0008-000000000003', '00000000-0000-0000-0007-000000000001', 'Intervju',            2, 2, 'petra@netler.com', 'petra@netler.com', NOW(), NOW()),
    ('00000000-0000-0000-0008-000000000004', '00000000-0000-0000-0007-000000000001', 'Referenskoll',        3, 3, 'petra@netler.com', 'petra@netler.com', NOW(), NOW()),
    ('00000000-0000-0000-0008-000000000005', '00000000-0000-0000-0007-000000000001', 'Erbjudande skickat',  4, 4, 'petra@netler.com', 'petra@netler.com', NOW(), NOW()),
    ('00000000-0000-0000-0008-000000000006', '00000000-0000-0000-0007-000000000001', 'Anställd',            5, 5, 'petra@netler.com', 'petra@netler.com', NOW(), NOW()),

    -- Onboarding – Nyanställda
    ('00000000-0000-0000-0008-000000000007', '00000000-0000-0000-0007-000000000002', 'Förberedelser',       0, 0, 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com', NOW(), NOW()),
    ('00000000-0000-0000-0008-000000000008', '00000000-0000-0000-0007-000000000002', 'Dag 1',               1, 1, 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com', NOW(), NOW()),
    ('00000000-0000-0000-0008-000000000009', '00000000-0000-0000-0007-000000000002', 'Vecka 1',             2, 2, 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com', NOW(), NOW()),
    ('00000000-0000-0000-0008-000000000016', '00000000-0000-0000-0007-000000000002', 'Månad 1',             3, 3, 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com', NOW(), NOW()),
    ('00000000-0000-0000-0008-000000000017', '00000000-0000-0000-0007-000000000002', 'Klart',               5, 4, 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com', NOW(), NOW()),

    -- Kundpipeline
    ('00000000-0000-0000-0008-000000000018', '00000000-0000-0000-0007-000000000003', 'Identifierade',       0, 0, 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com', NOW(), NOW()),
    ('00000000-0000-0000-0008-000000000019', '00000000-0000-0000-0007-000000000003', 'Kontaktad',           1, 1, 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com', NOW(), NOW()),
    ('00000000-0000-0000-0008-000000000020', '00000000-0000-0000-0007-000000000003', 'Dialog pågår',        2, 2, 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com', NOW(), NOW()),
    ('00000000-0000-0000-0008-000000000021', '00000000-0000-0000-0007-000000000003', 'Förhandling',         3, 3, 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com', NOW(), NOW()),
    ('00000000-0000-0000-0008-000000000022', '00000000-0000-0000-0007-000000000003', 'Avtal klart',         5, 4, 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com', NOW(), NOW());

-- =============================================================================
-- BOARD CARDS
-- =============================================================================

INSERT INTO board_cards (id, column_id, title, text, category, assigned_to, position, created_by, updated_by, created_at, updated_at)
VALUES
    -- Rekryteringspipeline → Nya kandidater
    ('00000000-0000-0000-0009-000000000001', '00000000-0000-0000-0008-000000000001',
     'Sofia Ekström', 'Fullstack-utvecklare, stark React- och Spring Boot-bakgrund. Söker nytt uppdrag från maj.', 'Fullstack', 'Petra Lindström', 0,
     'petra@netler.com', 'petra@netler.com', NOW(), NOW()),

    ('00000000-0000-0000-0009-000000000002', '00000000-0000-0000-0008-000000000001',
     'Frida Malmqvist', 'UX/UI Designer med imponerande portfolio. Lämplig för produktteam hos kund.', 'Design', 'Petra Lindström', 1,
     'petra@netler.com', 'petra@netler.com', NOW(), NOW()),

    -- Rekryteringspipeline → Första kontakt
    ('00000000-0000-0000-0009-000000000003', '00000000-0000-0000-0008-000000000002',
     'Mattias Holm', 'Java/Kotlin-specialist med 6 års erfarenhet. Möte bokat nästa vecka.', 'Backend', 'Petra Lindström', 0,
     'petra@netler.com', 'petra@netler.com', NOW(), NOW()),

    ('00000000-0000-0000-0009-000000000004', '00000000-0000-0000-0008-000000000002',
     'Jonas Bergström', 'Tech Lead, 12 års erfarenhet, van att leda team på 5–10 personer.', 'Ledning', 'Marcus Karlsson', 1,
     'petra@netler.com', 'petra@netler.com', NOW(), NOW()),

    -- Rekryteringspipeline → Intervju
    ('00000000-0000-0000-0009-000000000005', '00000000-0000-0000-0008-000000000003',
     'Rebecka Strand', 'DevOps Engineer – Kubernetes, Terraform och AWS. Intervju genomförd, mycket positiv.', 'DevOps', 'Philip Schill', 0,
     'petra@netler.com', 'petra@netler.com', NOW(), NOW()),

    -- Rekryteringspipeline → Referenskoll
    ('00000000-0000-0000-0009-000000000006', '00000000-0000-0000-0008-000000000004',
     'Oscar Lindberg', 'Frontend Developer specialiserad på React och TypeScript. Referenskoll pågår.', 'Frontend', 'Philip Olsson', 0,
     'petra@netler.com', 'petra@netler.com', NOW(), NOW()),

    -- Rekryteringspipeline → Erbjudande skickat
    ('00000000-0000-0000-0009-000000000007', '00000000-0000-0000-0008-000000000005',
     'Camilla Åberg', 'Data Engineer – Python, Apache Kafka och Spark. Erbjudande skickat, inväntar svar.', 'Data', 'Petra Lindström', 0,
     'petra@netler.com', 'petra@netler.com', NOW(), NOW()),

    -- Rekryteringspipeline → Anställd
    ('00000000-0000-0000-0009-000000000008', '00000000-0000-0000-0008-000000000006',
     'Henrik Norén', 'Backend Developer. Anställd från 2026-04-01, startdatum bekräftat.', 'Backend', 'Petra Lindström', 0,
     'petra@netler.com', 'petra@netler.com', NOW(), NOW()),

    -- Onboarding → Förberedelser
    ('00000000-0000-0000-0009-000000000009', '00000000-0000-0000-0008-000000000007',
     'Beställ dator och utrustning', 'MacBook Pro 16", extern skärm och headset. Skicka beställning till leverantör senast en vecka innan startdatum.', 'IT', 'Philip Schill', 0,
     'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com', NOW(), NOW()),

    ('00000000-0000-0000-0009-000000000027', '00000000-0000-0000-0008-000000000007',
     'Skapa konton', 'Google Workspace, Slack, GitHub, Figma och intranet. Skicka inbjudan via Firebase.', 'IT', 'Philip Schill', 1,
     'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com', NOW(), NOW()),

    ('00000000-0000-0000-0009-000000000028', '00000000-0000-0000-0008-000000000007',
     'Skicka välkomstmail', 'Inkludera schema för dag 1, parkering, kontaktperson och dresscode.', 'HR', 'Petra Lindström', 2,
     'petra@netler.com', 'petra@netler.com', NOW(), NOW()),

    -- Onboarding → Dag 1
    ('00000000-0000-0000-0009-000000000029', '00000000-0000-0000-0008-000000000008',
     'Husrundtur och presentation för teamet', 'Visa kontoret, mötesrum, kök och nödutgångar. Presentera för alla kollegor.', 'HR', 'Petra Lindström', 0,
     'petra@netler.com', 'petra@netler.com', NOW(), NOW()),

    ('00000000-0000-0000-0009-000000000030', '00000000-0000-0000-0008-000000000008',
     'Genomgång av rutiner och verktyg', 'Gå igenom hur vi jobbar – standup, sprintplanering, code review och deploy-process.', 'Process', 'Marcus Karlsson', 1,
     'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com', NOW(), NOW()),

    -- Onboarding → Vecka 1
    ('00000000-0000-0000-0009-000000000020', '00000000-0000-0000-0008-000000000009',
     'Sätt upp lokal utvecklingsmiljö', 'Klona repo, kör Docker Compose lokalt och verifiera att frontend och backend startar.', 'Teknik', 'Philip Schill', 0,
     'philip.schill@netler.com', 'philip.schill@netler.com', NOW(), NOW()),

    ('00000000-0000-0000-0009-000000000021', '00000000-0000-0000-0008-000000000009',
     'Genomgång av arkitektur och kodbas', 'Pair programming med senior kollega. Gå igenom domänmodell, säkerhet och deployflöde.', 'Teknik', 'Philip Schill', 1,
     'philip.schill@netler.com', 'philip.schill@netler.com', NOW(), NOW()),

    -- Onboarding → Månad 1
    ('00000000-0000-0000-0009-000000000010', '00000000-0000-0000-0008-000000000016',
     'Håll en teknikpresentation', 'Presentera ett valfritt ämne för teamet under ett lunch-and-learn.', 'Kompetens', NULL, 0,
     'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com', NOW(), NOW()),

    ('00000000-0000-0000-0009-000000000011', '00000000-0000-0000-0008-000000000016',
     '1:1 uppföljning med närmaste chef', 'Hur mår du? Vad fungerar bra och vad kan förbättras?', 'HR', 'Petra Lindström', 1,
     'petra@netler.com', 'petra@netler.com', NOW(), NOW()),

    -- Onboarding → Klart
    ('00000000-0000-0000-0009-000000000012', '00000000-0000-0000-0008-000000000017',
     'Henrik Norén – onboarding klar', 'Alla steg genomförda. Välkommen till teamet!', 'HR', 'Petra Lindström', 0,
     'petra@netler.com', 'petra@netler.com', NOW(), NOW()),

    -- Kundpipeline → Identifierade
    ('00000000-0000-0000-0009-000000000013', '00000000-0000-0000-0008-000000000018',
     'Volvo Cars', 'Letar efter ett React/TypeScript-team för nytt digitalt produktprogram. Kontakt via LinkedIn.', 'Fordon', 'Marcus Karlsson', 0,
     'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com', NOW(), NOW()),

    ('00000000-0000-0000-0009-000000000014', '00000000-0000-0000-0008-000000000018',
     'Saab AB', 'Cloud-migrering av legacy-system till AWS. Behöver DevOps-kompetens.', 'Försvar/Flyg', 'Philip Olsson', 1,
     'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com', NOW(), NOW()),

    -- Kundpipeline → Kontaktad
    ('00000000-0000-0000-0009-000000000015', '00000000-0000-0000-0008-000000000019',
     'Telia Company', 'Söker erfaren DevOps-konsult för CI/CD-pipeline. Inledande samtal bokat.', 'Telekom', 'Philip Olsson', 0,
     'philip.olsson@netler.com', 'philip.olsson@netler.com', NOW(), NOW()),

    ('00000000-0000-0000-0009-000000000016', '00000000-0000-0000-0008-000000000019',
     'Vattenfall', 'Dataingenjörer för energioptimerings-projekt. Kontaktperson: Anna Berg, IT-chef.', 'Energi', 'Marcus Karlsson', 1,
     'philip.olsson@netler.com', 'philip.olsson@netler.com', NOW(), NOW()),

    -- Kundpipeline → Dialog pågår
    ('00000000-0000-0000-0009-000000000017', '00000000-0000-0000-0008-000000000020',
     'Spotify', 'Söker 2 backend-konsulter (Java/Kotlin) för nytt betalningssystem. Dialog aktiv.', 'Media/Tech', 'Marcus Karlsson', 0,
     'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com', NOW(), NOW()),

    ('00000000-0000-0000-0009-000000000018', '00000000-0000-0000-0008-000000000020',
     'IKEA Digital', 'Frontend-lead till e-handelsteam. Bra kulturmatch, pris under diskussion.', 'Retail', 'Petra Lindström', 1,
     'petra@netler.com', 'petra@netler.com', NOW(), NOW()),

    -- Kundpipeline → Förhandling
    ('00000000-0000-0000-0009-000000000019', '00000000-0000-0000-0008-000000000021',
     'Scania', 'Tech Lead + 3 seniora utvecklare för nytt uppkopplat fordonsplatform. SOW under granskning.', 'Fordon', 'Marcus Karlsson', 0,
     'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com', NOW(), NOW()),

    -- Kundpipeline → Avtal klart
    ('00000000-0000-0000-0009-000000000026', '00000000-0000-0000-0008-000000000022',
     'Sandvik', 'Fullstack-team om 4 konsulter. Avtal signerat 2026-03-01, start 2026-04-07.', 'Industri', 'Marcus Karlsson', 0,
     'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com', NOW(), NOW());

-- =============================================================================
-- BOARD COMMENTS
-- =============================================================================

INSERT INTO board_comments (id, card_id, text, author_name, created_by, updated_by, created_at, updated_at)
VALUES
    ('00000000-0000-0000-0013-000000000001', '00000000-0000-0000-0009-000000000005',
     'Genomfört teknisk intervju, imponerades av AWS-kunskaperna. Rekommenderar att gå vidare.', 'Philip Schill',
     'philip.schill@netler.com', 'philip.schill@netler.com', NOW(), NOW()),

    ('00000000-0000-0000-0013-000000000002', '00000000-0000-0000-0009-000000000005',
     'Bra attityd och kommunikationsförmåga. Passar vår kultur.', 'Petra Lindström',
     'petra@netler.com', 'petra@netler.com', NOW(), NOW()),

    ('00000000-0000-0000-0013-000000000003', '00000000-0000-0000-0009-000000000007',
     'Camilla kom tillbaka med motbud på 5 % högre lön. Acceptabelt inom ram.', 'Petra Lindström',
     'petra@netler.com', 'petra@netler.com', NOW(), NOW()),

    ('00000000-0000-0000-0013-000000000004', '00000000-0000-0000-0009-000000000017',
     'Spotify vill ha startdatum 2026-05-01. Kollar om vi kan bemanna med Rebecka + en till.', 'Marcus Karlsson',
     'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com', NOW(), NOW()),

    ('00000000-0000-0000-0013-000000000005', '00000000-0000-0000-0009-000000000019',
     'Juridiken vill ha NDA signerat innan vi delar mer detaljer om teamet.', 'Marcus Karlsson',
     'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com', NOW(), NOW()),

    ('00000000-0000-0000-0013-000000000006', '00000000-0000-0000-0009-000000000019',
     'NDA skickat till Scania idag. Väntar på retur.', 'Petra Lindström',
     'petra@netler.com', 'petra@netler.com', NOW(), NOW()),

    ('00000000-0000-0000-0013-000000000007', '00000000-0000-0000-0009-000000000027',
     'GitHub-inbjudan skickad. Firebase-länk fungerar. Slack-konto aktivt.', 'Philip Schill',
     'philip.schill@netler.com', 'philip.schill@netler.com', NOW(), NOW());
