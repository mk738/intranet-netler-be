-- =============================================================================
-- V2__seed_dev_data.sql  —  comprehensive dev seed data
-- =============================================================================
-- Consolidated from V2 + V5 + V10 + V11 (Fredrik) + Philip Olsson/Schill + Petra.
-- For subsequent full resets use: scripts/seed_dev_data.sql
-- =============================================================================


-- =============================================================================
-- EMPLOYEES
-- =============================================================================
-- firebase_uid for marcus.karlsson@netler.com is a placeholder.
-- After first login run:
--   UPDATE employees SET firebase_uid = '<your-real-uid>' WHERE email = 'marcus.karlsson@netler.com';

INSERT INTO employees (id, firebase_uid, email, role, is_active)
VALUES
    ('00000000-0000-0000-0000-000000000001', 'marcus-netler-uid',             'marcus.karlsson@netler.com', 'SUPERADMIN', TRUE),
    ('00000000-0000-0000-0000-000000000002', 'erik-netler-uid',               'erik.lindqvist@netler.com',  'EMPLOYEE',   TRUE),
    ('00000000-0000-0000-0000-000000000003', 'sara-netler-uid',               'sara.berg@netler.com',        'EMPLOYEE',   TRUE),
    ('00000000-0000-0000-0000-000000000004', 'johan-netler-uid',              'johan.petersson@netler.com',  'EMPLOYEE',   TRUE),
    ('00000000-0000-0000-0000-000000000005', 'lina-netler-uid',               'lina.eriksson@netler.com',    'EMPLOYEE',   TRUE),
    ('00000000-0000-0000-0000-000000000006', 'mikael-netler-uid',             'mikael.svensson@netler.com',  'EMPLOYEE',   TRUE),
    ('00000000-0000-0000-0000-000000000007', 'anna-netler-uid',               'anna.johansson@netler.com',   'EMPLOYEE',   TRUE),
    ('00000000-0000-0000-0000-000000000008', 'test-employee-uid-placeholder', 'test.employee@netler.com',    'EMPLOYEE',   TRUE),
    ('00000000-0000-0000-0000-000000000009', 'sq3ezUDBzSZ6RV16FJG68OOuC7t2',  'norling.fre@gmail.com',       'EMPLOYEE',   TRUE),
    ('00000000-0000-0000-0000-000000000010', 'WPFQhIsBrvTG2ve5fplw26sfdbC2',  'philip.olsson@netler.com',    'SUPERADMIN', TRUE),
    ('00000000-0000-0000-0000-000000000011', '1OGSEIXFLoYL063TLgEqZ3zb8rX2',  'philip.schill@netler.com',    'SUPERADMIN', TRUE),
    ('00000000-0000-0000-0000-000000000013', 'jdMOkk4CJzXWVB0qPvmICLRrink1',  'petra@netler.com',            'ADMIN',      TRUE);


-- =============================================================================
-- EMPLOYEE PROFILES
-- =============================================================================

INSERT INTO employee_profiles
    (id, employee_id, first_name, last_name, job_title, phone, address, emergency_contact, start_date, birth_date)
VALUES
    ('00000000-0000-0000-0002-000000000001',
     '00000000-0000-0000-0000-000000000001',
     'Marcus', 'Karlsson', 'CTO & Co-founder',
     '+46 70 123 45 67', 'Storgatan 12, 111 23 Stockholm',
     'Emma Karlsson – +46 73 987 65 43', '2021-01-15', '1988-04-22'),

    ('00000000-0000-0000-0002-000000000002',
     '00000000-0000-0000-0000-000000000002',
     'Erik', 'Lindqvist', 'Senior Backend Developer',
     '+46 70 234 56 78', 'Vasagatan 5, 411 24 Göteborg',
     'Maria Lindqvist – +46 73 876 54 32', '2022-08-01', '1991-11-03'),

    ('00000000-0000-0000-0002-000000000003',
     '00000000-0000-0000-0000-000000000003',
     'Sara', 'Berg', 'Backend Developer',
     '+46 70 345 67 89', 'Kungsgatan 18, 753 21 Uppsala',
     'Lars Berg – +46 73 765 43 21', '2023-03-15', '1994-07-19'),

    ('00000000-0000-0000-0002-000000000004',
     '00000000-0000-0000-0000-000000000004',
     'Johan', 'Petersson', 'Frontend Developer',
     '+46 70 456 78 90', 'Drottninggatan 44, 211 41 Malmö',
     'Karin Petersson – +46 73 654 32 10', '2023-06-01', '1993-02-28'),

    ('00000000-0000-0000-0002-000000000005',
     '00000000-0000-0000-0000-000000000005',
     'Lina', 'Eriksson', 'DevOps Engineer',
     '+46 70 567 89 01', 'Östra Hamngatan 7, 411 10 Göteborg',
     'Peter Eriksson – +46 73 543 21 09', '2022-11-01', '1990-09-14'),

    ('00000000-0000-0000-0002-000000000006',
     '00000000-0000-0000-0000-000000000006',
     'Mikael', 'Svensson', 'Full Stack Developer',
     '+46 70 678 90 12', 'Stortorget 3, 211 34 Malmö',
     'Ingrid Svensson – +46 73 432 10 98', '2024-01-10', '1995-05-07'),

    ('00000000-0000-0000-0002-000000000007',
     '00000000-0000-0000-0000-000000000007',
     'Anna', 'Johansson', 'Data Engineer',
     '+46 70 789 01 23', 'Sveavägen 22, 113 59 Stockholm',
     'Björn Johansson – +46 73 321 09 87', '2023-09-01', '1992-12-11'),

    ('00000000-0000-0000-0002-000000000008',
     '00000000-0000-0000-0000-000000000008',
     'Test', 'Employee', 'Test User',
     NULL, NULL, NULL, '2026-01-01', NULL),

    ('00000000-0000-0000-0002-000000000009',
     '00000000-0000-0000-0000-000000000009',
     'Fredrik', 'Norling', 'Backend Developer',
     '+46 73 456 78 90', 'Pilgatan 9, 413 13 Göteborg',
     'Lisa Norling – +46 70 234 56 78', '2025-01-15', '1995-08-22'),

    ('00000000-0000-0000-0002-000000000010',
     '00000000-0000-0000-0000-000000000010',
     'Philip', 'Olsson', 'Account Manager',
     '+46 70 111 22 33', 'Birger Jarlsgatan 8, 114 34 Stockholm',
     'Anna Olsson – +46 73 111 22 44', '2023-05-01', '1990-06-15'),

    ('00000000-0000-0000-0002-000000000011',
     '00000000-0000-0000-0000-000000000011',
     'Philip', 'Schill', 'Frontend Developer',
     '+46 70 222 33 44', 'Linnégatan 14, 413 04 Göteborg',
     'Sofia Schill – +46 73 222 33 55', '2024-04-01', '1997-03-08'),

    ('00000000-0000-0000-0002-000000000013',
     '00000000-0000-0000-0000-000000000013',
     'Petra', 'Lichtenecker', 'Operations Manager',
     '+46 70 444 55 66', 'Drottninggatan 30, 111 51 Stockholm',
     'Anders Lindström – +46 73 444 55 77', '2022-02-01', '1985-11-28');


-- =============================================================================
-- BANK INFO
-- =============================================================================

INSERT INTO bank_info (id, employee_id, bank_name, account_number, clearing_number)
VALUES
    ('00000000-0000-0000-0003-000000000001', '00000000-0000-0000-0000-000000000001', 'Swedbank',      '12345678', '8327-9'),
    ('00000000-0000-0000-0003-000000000002', '00000000-0000-0000-0000-000000000002', 'SEB',           '98765432', '5000-2'),
    ('00000000-0000-0000-0003-000000000003', '00000000-0000-0000-0000-000000000003', 'Handelsbanken', '55512345', '6000-4'),
    ('00000000-0000-0000-0003-000000000004', '00000000-0000-0000-0000-000000000004', 'Nordea',        '44498765', '3300-3'),
    ('00000000-0000-0000-0003-000000000005', '00000000-0000-0000-0000-000000000005', 'SEB',           '77712345', '5000-2'),
    ('00000000-0000-0000-0003-000000000006', '00000000-0000-0000-0000-000000000006', 'Swedbank',      '33398765', '8327-9'),
    ('00000000-0000-0000-0003-000000000007', '00000000-0000-0000-0000-000000000007', 'ICA Banken',    '88812345', '9270-6'),
    ('00000000-0000-0000-0003-000000000010', '00000000-0000-0000-0000-000000000010', 'Swedbank',      '91234567', '8327-9'),
    ('00000000-0000-0000-0003-000000000011', '00000000-0000-0000-0000-000000000011', 'Nordea',        '82345678', '3300-3'),
    ('00000000-0000-0000-0003-000000000013', '00000000-0000-0000-0000-000000000013', 'Handelsbanken', '63219876', '6000-0');


-- =============================================================================
-- EDUCATION
-- =============================================================================

INSERT INTO education (id, employee_id, institution, degree, field, start_year, end_year, description)
VALUES
    -- Marcus Karlsson
    ('00000000-0000-0000-0004-000000000001', '00000000-0000-0000-0000-000000000001',
     'KTH Royal Institute of Technology', 'M.Sc.', 'Computer Science & Engineering',
     2006, 2011, 'Focus on distributed systems and software architecture.'),
    ('00000000-0000-0000-0004-000000000002', '00000000-0000-0000-0000-000000000001',
     'Stockholm School of Economics', 'Executive Education', 'Technology Leadership',
     2018, 2019, 'Part-time programme for technical founders and CTOs.'),

    -- Erik Lindqvist
    ('00000000-0000-0000-0004-000000000003', '00000000-0000-0000-0000-000000000002',
     'Chalmers University of Technology', 'B.Sc.', 'Software Engineering',
     2009, 2012, NULL),
    ('00000000-0000-0000-0004-000000000004', '00000000-0000-0000-0000-000000000002',
     'Chalmers University of Technology', 'M.Sc.', 'Computer Science',
     2012, 2014, 'Thesis on distributed stream processing with Apache Kafka.'),

    -- Sara Berg
    ('00000000-0000-0000-0004-000000000005', '00000000-0000-0000-0000-000000000003',
     'Uppsala University', 'B.Sc.', 'Computer Science',
     2011, 2014, NULL),
    ('00000000-0000-0000-0004-000000000006', '00000000-0000-0000-0000-000000000003',
     'Uppsala University', 'M.Sc.', 'Software & Computer Systems',
     2014, 2016, 'Specialised in API design and microservices architecture.'),

    -- Johan Petersson
    ('00000000-0000-0000-0004-000000000007', '00000000-0000-0000-0000-000000000004',
     'Malmö University', 'B.Sc.', 'Web Development',
     2011, 2014, NULL),
    ('00000000-0000-0000-0004-000000000008', '00000000-0000-0000-0000-000000000004',
     'Lund University', 'M.Sc.', 'Interaction Design',
     2014, 2016, 'Thesis on accessibility and inclusive design in web applications.'),

    -- Lina Eriksson
    ('00000000-0000-0000-0004-000000000009', '00000000-0000-0000-0000-000000000005',
     'Chalmers University of Technology', 'B.Sc.', 'Computer Networks',
     2008, 2011, NULL),
    ('00000000-0000-0000-0004-000000000010', '00000000-0000-0000-0000-000000000005',
     'Chalmers University of Technology', 'M.Sc.', 'Networks & Distributed Systems',
     2011, 2013, 'Cloud infrastructure and Kubernetes before it was mainstream.'),

    -- Mikael Svensson
    ('00000000-0000-0000-0004-000000000011', '00000000-0000-0000-0000-000000000006',
     'Linköping University', 'B.Sc.', 'Information Technology',
     2013, 2016, NULL),
    ('00000000-0000-0000-0004-000000000012', '00000000-0000-0000-0000-000000000006',
     'Linköping University', 'M.Sc.', 'Software Engineering',
     2016, 2018, 'Focus on modern web architectures and cloud-native development.'),

    -- Anna Johansson
    ('00000000-0000-0000-0004-000000000013', '00000000-0000-0000-0000-000000000007',
     'KTH Royal Institute of Technology', 'M.Sc.', 'Data Science',
     2010, 2015, 'Focus on large-scale data pipelines and ML infrastructure.'),
    ('00000000-0000-0000-0004-000000000014', '00000000-0000-0000-0000-000000000007',
     'Coursera / Stanford University', 'Certificate', 'Machine Learning',
     2016, 2016, 'Andrew Ng''s Machine Learning specialisation.'),

    -- Philip Olsson
    ('00000000-0000-0000-0004-000000000019', '00000000-0000-0000-0000-000000000010',
     'Stockholm School of Economics', 'B.Sc.', 'Business Administration',
     2009, 2012, 'Focus on sales, marketing, and business development.');


-- =============================================================================
-- CLIENTS
-- =============================================================================

INSERT INTO clients (id, company_name, contact_name, contact_email, phone, org_number, status)
VALUES
    ('00000000-0000-0000-0001-000000000001', 'Spotify AB',     'Johan Svensson',  'johan.svensson@spotify.com',   '+46 8 123 456 78', '556703-7485', 'ACTIVE'),
    ('00000000-0000-0000-0001-000000000002', 'Klarna Bank AB', 'Lisa Kaminsky',   'lisa.kaminsky@klarna.com',     '+46 8 234 567 89', '556737-0431', 'ACTIVE'),
    ('00000000-0000-0000-0001-000000000003', 'IKEA Digital',   'Anna Ikonen',     'anna.ikonen@inter.ikea.com',   '+46 42 267 00 00', '556452-3440', 'ACTIVE'),
    ('00000000-0000-0000-0001-000000000004', 'Volvo Cars',     'Peter Lindberg',  'peter.lindberg@volvocars.com', '+46 31 590 00 00', '556074-3089', 'ACTIVE'),
    ('00000000-0000-0000-0001-000000000005', 'H&M Group',      'Sofia Hernandez', 'sofia.hernandez@hm.com',       '+46 8 796 55 00',  '556042-7220', 'ACTIVE'),
    ('00000000-0000-0000-0001-000000000006', 'Ericsson AB',    'Magnus Strand',   'magnus.strand@ericsson.com',   '+46 10 719 00 00', '556016-0680', 'PROSPECT');


-- =============================================================================
-- ASSIGNMENTS
-- =============================================================================

INSERT INTO assignments (id, employee_id, client_id, project_name, start_date, end_date, status)
VALUES
    -- Erik @ Spotify (active)
    ('00000000-0000-0000-0005-000000000001',
     '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0001-000000000001',
     'Data Platform Modernisation', '2024-09-01', '2026-08-31', 'ACTIVE'),

    -- Erik @ Klarna (historical)
    ('00000000-0000-0000-0005-000000000002',
     '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0001-000000000002',
     'Payments API Refactor', '2022-08-01', '2024-08-31', 'ENDED'),

    -- Sara @ Klarna (active)
    ('00000000-0000-0000-0005-000000000003',
     '00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0001-000000000002',
     'Open Banking Integration', '2024-03-01', '2026-02-28', 'ACTIVE'),

    -- Johan @ IKEA (active)
    ('00000000-0000-0000-0005-000000000004',
     '00000000-0000-0000-0000-000000000004', '00000000-0000-0000-0001-000000000003',
     'Design System & Component Library', '2024-01-15', '2026-04-01', 'ACTIVE'),

    -- Lina @ Ericsson (ended — unplaced)
    ('00000000-0000-0000-0005-000000000005',
     '00000000-0000-0000-0000-000000000005', '00000000-0000-0000-0001-000000000006',
     'Cloud Migration Programme', '2022-11-01', '2025-10-31', 'ENDED'),

    -- Mikael @ Volvo (active)
    ('00000000-0000-0000-0005-000000000006',
     '00000000-0000-0000-0000-000000000006', '00000000-0000-0000-0001-000000000004',
     'Connected Car Platform', '2025-02-01', '2026-12-31', 'ACTIVE'),

    -- Anna @ H&M (active)
    ('00000000-0000-0000-0005-000000000007',
     '00000000-0000-0000-0000-000000000007', '00000000-0000-0000-0001-000000000005',
     'Supply Chain Analytics', '2024-09-15', '2026-09-14', 'ACTIVE'),

    -- Fredrik @ Klarna (active)
    ('00000000-0000-0000-0005-000000000012',
     '00000000-0000-0000-0000-000000000009', '00000000-0000-0000-0001-000000000002',
     'Checkout Flow Optimisation', '2025-02-01', '2026-07-31', 'ACTIVE');


-- =============================================================================
-- VACATION REQUESTS
-- =============================================================================

INSERT INTO vacation_requests
    (id, employee_id, start_date, end_date, days_count, status, reviewed_by, reviewed_at)
VALUES
    ('00000000-0000-0000-0006-000000000001',
     '00000000-0000-0000-0000-000000000002',
     '2025-07-07', '2025-07-25', 15, 'APPROVED',
     '00000000-0000-0000-0000-000000000001', '2025-05-12 10:30:00+00'),

    ('00000000-0000-0000-0006-000000000002',
     '00000000-0000-0000-0000-000000000002',
     '2026-06-15', '2026-06-26', 10, 'APPROVED',
     '00000000-0000-0000-0000-000000000001', '2026-03-01 09:00:00+00'),

    ('00000000-0000-0000-0006-000000000003',
     '00000000-0000-0000-0000-000000000003',
     '2025-12-22', '2026-01-02', 8, 'APPROVED',
     '00000000-0000-0000-0000-000000000001', '2025-10-08 09:15:00+00'),

    ('00000000-0000-0000-0006-000000000004',
     '00000000-0000-0000-0000-000000000003',
     '2026-04-06', '2026-04-10', 5, 'PENDING', NULL, NULL),

    ('00000000-0000-0000-0006-000000000005',
     '00000000-0000-0000-0000-000000000004',
     '2026-04-13', '2026-04-17', 5, 'PENDING', NULL, NULL),

    ('00000000-0000-0000-0006-000000000006',
     '00000000-0000-0000-0000-000000000005',
     '2026-05-04', '2026-05-15', 10, 'PENDING', NULL, NULL),

    ('00000000-0000-0000-0006-000000000007',
     '00000000-0000-0000-0000-000000000006',
     '2026-03-23', '2026-03-27', 5, 'REJECTED',
     '00000000-0000-0000-0000-000000000001', '2026-03-10 14:00:00+00'),

    ('00000000-0000-0000-0006-000000000008',
     '00000000-0000-0000-0000-000000000006',
     '2026-07-13', '2026-07-24', 10, 'APPROVED',
     '00000000-0000-0000-0000-000000000001', '2026-03-01 11:30:00+00'),

    ('00000000-0000-0000-0006-000000000009',
     '00000000-0000-0000-0000-000000000007',
     '2026-04-20', '2026-04-30', 9, 'PENDING', NULL, NULL),

    ('00000000-0000-0000-0006-000000000010',
     '00000000-0000-0000-0000-000000000001',
     '2026-07-06', '2026-07-24', 15, 'APPROVED',
     '00000000-0000-0000-0000-000000000001', '2026-02-20 11:00:00+00');


-- =============================================================================
-- NEWS POSTS
-- =============================================================================

INSERT INTO news_posts (id, title, body, author_id, published_at, pinned)
VALUES
    ('00000000-0000-0000-0007-000000000001',
     'Welcome to the Netler Intranet',
     'We built this platform to make everyday work easier for everyone at Netler. Use it to manage your profile and bank details, submit vacation requests, follow client assignments, and stay up to date with company news and events. Explore the sections in the menu and let us know if you have feedback or ideas for improvements.',
     '00000000-0000-0000-0000-000000000001', '2025-09-01 08:00:00+00', TRUE),

    ('00000000-0000-0000-0007-000000000002',
     'New Client: Volvo Cars',
     'We are excited to announce that Netler has started a new multi-year engagement with Volvo Cars. Mikael Svensson will be leading work on their Connected Car Platform, focusing on backend services and cloud infrastructure for next-generation vehicle connectivity. Welcome aboard, Volvo Cars!',
     '00000000-0000-0000-0000-000000000001', '2025-12-10 10:00:00+00', FALSE),

    ('00000000-0000-0000-0007-000000000003',
     'Q1 2026 Company Update',
     'We are off to a strong start in 2026. Revenue is up 24% compared to Q1 last year, and we have grown the team by three new consultants. All six of our consultants are currently on active assignments or in final negotiation for their next placement. A huge thank you to everyone who has contributed — client satisfaction scores are at an all-time high across all accounts.',
     '00000000-0000-0000-0000-000000000001', '2026-02-15 09:00:00+00', FALSE),

    ('00000000-0000-0000-0007-000000000004',
     'Johan''s Assignment at IKEA Ending Soon',
     'A heads-up that Johan Petersson''s current assignment at IKEA Digital concludes on April 1st. Johan has done great work building out their Design System and Component Library. We are actively in conversations with several clients about Johan''s next placement. If you have any leads or connections, reach out to Marcus directly.',
     '00000000-0000-0000-0000-000000000001', '2026-03-01 08:30:00+00', FALSE),

    -- Draft — not published
    ('00000000-0000-0000-0007-000000000005',
     'Summer Party 2026 — Save the Date!',
     'We are planning an amazing summer party this year. Block off the last Friday in June — more details to follow on location, activities, and whether partners are welcome.',
     '00000000-0000-0000-0000-000000000001', NULL, FALSE);


-- =============================================================================
-- EVENTS
-- =============================================================================

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
