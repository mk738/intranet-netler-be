-- =============================================================================
-- Netler Intranet — test environment seed data
-- =============================================================================
-- Runs on every Railway test deployment via spring.sql.init.data-locations.
-- All inserts are fully idempotent — NO DELETE statements anywhere.
-- All date/time values use now() ± interval so data always looks current.
-- All UUIDs are hardcoded — no gen_random_uuid().
--
-- Firebase-linked employees keep their exact dev UUIDs:
--   marcus.karlsson  → 00000000-0000-0000-0000-000000000001
--   philip.olsson    → 00000000-0000-0000-0000-000000000010
--   philip.schill    → 00000000-0000-0000-0000-000000000011
--   petra            → 00000000-0000-0000-0000-000000000013
--
-- All other employees use aa-prefixed UUIDs distinct from dev.
-- =============================================================================


-- =============================================================================
-- ONBOARDING TEMPLATE ITEMS
-- =============================================================================

INSERT INTO onboarding_template_items
    (id, task_key, label_sv, sort_order, active, created_at, updated_at)
VALUES
    ('00000000-0000-0000-000e-000000000001', 'CREATE_CV',             'Skapa CV',                              1,  true, now(), now()),
    ('00000000-0000-0000-000e-000000000002', 'NETLER_MAIL',           'Netler-mail via Google Admin',          2,  true, now(), now()),
    ('00000000-0000-0000-000e-000000000003', 'BIRTHDAY_IN_SLACK',     'Lägg in födelsedag i Slack',            3,  true, now(), now()),
    ('00000000-0000-0000-000e-000000000004', 'SLACK_INVITATION',      'Slack-inbjudan till Netler-mail',       4,  true, now(), now()),
    ('00000000-0000-0000-000e-000000000005', 'FORTNOX_ADD_USER',      'Fortnox: Lägg till i kvitton & utlägg', 5, true, now(), now()),
    ('00000000-0000-0000-000e-000000000006', 'FORTNOX_RECEIPT_GROUP', 'Fortnox: Lägg till i kvittogruppen',   6,  true, now(), now()),
    ('00000000-0000-0000-000e-000000000007', 'SLACK_WELCOME_MESSAGE', 'Skriv välkommen i Slack-kanalen',       7,  true, now(), now()),
    ('00000000-0000-0000-000e-000000000008', 'SLACK_PROFILE_PHOTO',   'Be om profilbild till Slack',           8,  true, now(), now()),
    ('00000000-0000-0000-000e-000000000009', 'SEND_WELCOME_LETTER',   'Skicka välkomstbrev till Netler-mail',  9,  true, now(), now()),
    ('00000000-0000-0000-000e-000000000010', 'REQUEST_BANK_DETAILS',  'Be om bankuppgifter',                   10, true, now(), now()),
    ('00000000-0000-0000-000e-000000000011', 'NOTIFY_PAYROLL',        'Meddela löneavdelningen',               11, true, now(), now()),
    ('00000000-0000-0000-000e-000000000012', 'SETUP_PENSION',         'Lägg upp pension',                      12, true, now(), now())
ON CONFLICT (task_key) DO NOTHING;


-- =============================================================================
-- EMPLOYEES
-- =============================================================================
-- Firebase UIDs kept only for the four Firebase-linked accounts.
-- All other employees have firebase_uid = NULL in this environment.

INSERT INTO employees (id, firebase_uid, email, role, is_active)
VALUES
    ('00000000-0000-0000-0000-000000000001', 'UqhXiWJBfZYSlGZyj34PPaj8jJY2', 'marcus.karlsson@netler.com', 'SUPERADMIN', TRUE),
    ('aa000000-0000-0000-0000-000000000002', NULL,                             'erik.lindqvist@netler.com',  'EMPLOYEE',   TRUE),
    ('aa000000-0000-0000-0000-000000000003', NULL,                             'sara.berg@netler.com',        'EMPLOYEE',   TRUE),
    ('aa000000-0000-0000-0000-000000000004', NULL,                             'johan.petersson@netler.com',  'EMPLOYEE',   TRUE),
    ('aa000000-0000-0000-0000-000000000005', NULL,                             'lina.eriksson@netler.com',    'EMPLOYEE',   TRUE),
    ('aa000000-0000-0000-0000-000000000006', NULL,                             'mikael.svensson@netler.com',  'EMPLOYEE',   TRUE),
    ('aa000000-0000-0000-0000-000000000007', NULL,                             'anna.johansson@netler.com',   'EMPLOYEE',   TRUE),
    ('aa000000-0000-0000-0000-000000000008', NULL,                             'test.employee@netler.com',    'EMPLOYEE',   TRUE),
    ('aa000000-0000-0000-0000-000000000009', NULL,                             'norling.fre@gmail.com',        'EMPLOYEE',  TRUE),
    ('00000000-0000-0000-0000-000000000010', 'WPFQhIsBrvTG2ve5fplw26sfdbC2', 'philip.olsson@netler.com',  'SUPERADMIN', TRUE),
    ('00000000-0000-0000-0000-000000000011', '1OGSEIXFLoYL063TLgEqZ3zb8rX2', 'philip.schill@netler.com', 'SUPERADMIN', TRUE),
    ('aa000000-0000-0000-0000-000000000012', NULL,                             'emma.holmgren@netler.com',    'EMPLOYEE',   TRUE),
    ('00000000-0000-0000-0000-000000000013', 'jdMOkk4CJzXWVB0qPvmICLRrink1',  'petra@netler.com',           'ADMIN',      TRUE),
    ('aa000000-0000-0000-0000-000000000014', NULL,                             'klara.lindstrom@netler.com',  'EMPLOYEE',   TRUE),
    ('aa000000-0000-0000-0000-000000000015', NULL,                             'oscar.hansson@netler.com',    'EMPLOYEE',   TRUE)
ON CONFLICT (id) DO NOTHING;


-- =============================================================================
-- EMPLOYEE PROFILES
-- =============================================================================

INSERT INTO employee_profiles
    (id, employee_id, first_name, last_name, job_title, phone, address, emergency_contact, start_date, birth_date)
VALUES
    -- Marcus Karlsson (CTO, started ~5 years ago, born ~38 years ago)
    ('00000000-0000-0000-0002-000000000001',
     '00000000-0000-0000-0000-000000000001',
     'Marcus', 'Karlsson',
     'CTO & Co-founder',
     '+46 70 123 45 67',
     'Storgatan 12, 111 23 Stockholm',
     'Emma Karlsson – +46 73 987 65 43',
     now() - interval '5 years 2 months', now() - interval '38 years'),

    -- Erik Lindqvist (started ~3 years 8 months ago, born ~35 years ago)
    ('aa000000-0000-0000-0002-000000000002',
     'aa000000-0000-0000-0000-000000000002',
     'Erik', 'Lindqvist',
     'Senior Backend Developer',
     '+46 70 234 56 78',
     'Vasagatan 5, 411 24 Göteborg',
     'Maria Lindqvist – +46 73 876 54 32',
     now() - interval '3 years 8 months', now() - interval '35 years'),

    -- Sara Berg (started ~3 years 1 month ago, born ~32 years ago)
    ('aa000000-0000-0000-0002-000000000003',
     'aa000000-0000-0000-0000-000000000003',
     'Sara', 'Berg',
     'Backend Developer',
     '+46 70 345 67 89',
     'Kungsgatan 18, 753 21 Uppsala',
     'Lars Berg – +46 73 765 43 21',
     now() - interval '3 years 1 month', now() - interval '32 years'),

    -- Johan Petersson (started ~2 years 10 months ago, born ~33 years ago)
    ('aa000000-0000-0000-0002-000000000004',
     'aa000000-0000-0000-0000-000000000004',
     'Johan', 'Petersson',
     'Frontend Developer',
     '+46 70 456 78 90',
     'Drottninggatan 44, 211 41 Malmö',
     'Karin Petersson – +46 73 654 32 10',
     now() - interval '2 years 10 months', now() - interval '33 years'),

    -- Lina Eriksson (started ~3 years 5 months ago, born ~36 years ago)
    ('aa000000-0000-0000-0002-000000000005',
     'aa000000-0000-0000-0000-000000000005',
     'Lina', 'Eriksson',
     'DevOps Engineer',
     '+46 70 567 89 01',
     'Östra Hamngatan 7, 411 10 Göteborg',
     'Peter Eriksson – +46 73 543 21 09',
     now() - interval '3 years 5 months', now() - interval '36 years'),

    -- Mikael Svensson (started ~2 years 3 months ago, born ~31 years ago)
    ('aa000000-0000-0000-0002-000000000006',
     'aa000000-0000-0000-0000-000000000006',
     'Mikael', 'Svensson',
     'Full Stack Developer',
     '+46 70 678 90 12',
     'Stortorget 3, 211 34 Malmö',
     'Ingrid Svensson – +46 73 432 10 98',
     now() - interval '2 years 3 months', now() - interval '31 years'),

    -- Anna Johansson (started ~2 years 7 months ago, born ~34 years ago)
    ('aa000000-0000-0000-0002-000000000007',
     'aa000000-0000-0000-0000-000000000007',
     'Anna', 'Johansson',
     'Data Engineer',
     '+46 70 789 01 23',
     'Sveavägen 22, 113 59 Stockholm',
     'Björn Johansson – +46 73 321 09 87',
     now() - interval '2 years 7 months', now() - interval '34 years'),

    -- Test Employee (started ~3 months ago)
    ('aa000000-0000-0000-0002-000000000008',
     'aa000000-0000-0000-0000-000000000008',
     'Test', 'Employee',
     'Test User',
     NULL, NULL, NULL,
     now() - interval '3 months', NULL),

    -- Fredrik Norling (started ~15 months ago, born ~31 years ago)
    ('aa000000-0000-0000-0002-000000000009',
     'aa000000-0000-0000-0000-000000000009',
     'Fredrik', 'Norling',
     'Backend Developer',
     '+46 73 456 78 90',
     'Pilgatan 9, 413 13 Göteborg',
     'Lisa Norling – +46 70 234 56 78',
     now() - interval '15 months', now() - interval '31 years'),

    -- Philip Olsson (started ~2 years 11 months ago, born ~36 years ago)
    ('00000000-0000-0000-0002-000000000010',
     '00000000-0000-0000-0000-000000000010',
     'Philip', 'Olsson', 'Account Manager',
     '+46 70 111 22 33',
     'Birger Jarlsgatan 8, 114 34 Stockholm',
     'Anna Olsson – +46 73 111 22 44',
     now() - interval '2 years 11 months', now() - interval '36 years'),

    -- Philip Schill (started ~1 year ago, born ~29 years ago)
    ('00000000-0000-0000-0002-000000000011',
     '00000000-0000-0000-0000-000000000011',
     'Philip', 'Schill', 'Frontend Developer',
     '+46 70 222 33 44',
     'Linnégatan 14, 413 04 Göteborg',
     'Sofia Schill – +46 73 222 33 55',
     now() - interval '1 year', now() - interval '29 years'),

    -- Emma Holmgren (started ~2 years 3 months ago, born ~30 years ago)
    ('aa000000-0000-0000-0002-000000000012',
     'aa000000-0000-0000-0000-000000000012',
     'Emma', 'Holmgren',
     'Frontend Developer',
     '+46 70 555 12 34',
     'Linnégatan 22, 413 04 Göteborg',
     'Lars Holmgren – +46 73 555 43 21',
     now() - interval '2 years 3 months', now() - interval '30 years'),

    -- Petra Lichtenecker (started ~4 years 2 months ago, born ~41 years ago)
    ('00000000-0000-0000-0002-000000000013',
     '00000000-0000-0000-0000-000000000013',
     'Petra', 'Lichtenecker', 'Operations Manager',
     '+46 70 444 55 66',
     'Drottninggatan 30, 111 51 Stockholm',
     'Anders Lindström – +46 73 444 55 77',
     now() - interval '4 years 2 months', now() - interval '41 years'),

    -- Klara Lindström (recently started ~3 weeks ago, born ~29 years ago)
    ('aa000000-0000-0000-0002-000000000014',
     'aa000000-0000-0000-0000-000000000014',
     'Klara', 'Lindström',
     'Frontend Developer',
     '+46 70 111 22 33',
     'Linnégatan 14, 413 04 Göteborg',
     'Mats Lindström – +46 73 111 22 33',
     now() - interval '3 weeks', now() - interval '29 years'),

    -- Oscar Hansson (recently started ~10 days ago, born ~27 years ago)
    ('aa000000-0000-0000-0002-000000000015',
     'aa000000-0000-0000-0000-000000000015',
     'Oscar', 'Hansson',
     'Backend Developer',
     '+46 70 444 55 66',
     'Dalavägen 8, 753 10 Uppsala',
     'Karin Hansson – +46 73 444 55 66',
     now() - interval '10 days', now() - interval '27 years')
ON CONFLICT (id) DO NOTHING;


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
    ('00000000-0000-0000-000c-000000000020', 'Data Engineering')
ON CONFLICT (id) DO NOTHING;


-- =============================================================================
-- ONBOARDING ITEMS
-- =============================================================================
-- UUID scheme: dd000000-0000-0000-{EE}{TT}-000000000000
--   EE = employee index (01–11), TT = task index (01–12)
--   01=marcus  02=erik  03=sara  04=johan  05=lina  06=mikael
--   07=anna    08=test  09=fredrik  10=klara  11=oscar
--
-- Employees 01–09: all 12 tasks completed
-- Employee 10 (Klara): tasks 1–6 completed, 7–12 open
-- Employee 11 (Oscar): tasks 1–3 completed, 4–12 open
--
-- ON CONFLICT (employee_id, task_key) DO NOTHING preserves any
-- completion state already recorded in the database.

INSERT INTO onboarding_items
    (id, employee_id, task_key, label_sv, sort_order, completed, completed_at, completed_by,
     created_at, updated_at, created_by, updated_by)
VALUES

    -- ── 01 Marcus Karlsson — all complete (started ~5 years 2 months ago) ──────
    ('dd000000-0000-0000-0101-000000000000', '00000000-0000-0000-0000-000000000001', 'CREATE_CV',             'Skapa CV',                              1,  true, now() - interval '5 years 2 months 2 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '5 years 2 months 2 weeks', now() - interval '5 years 2 months 2 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0102-000000000000', '00000000-0000-0000-0000-000000000001', 'NETLER_MAIL',           'Netler-mail via Google Admin',          2,  true, now() - interval '5 years 2 months 2 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '5 years 2 months 2 weeks', now() - interval '5 years 2 months 2 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0103-000000000000', '00000000-0000-0000-0000-000000000001', 'BIRTHDAY_IN_SLACK',     'Lägg in födelsedag i Slack',            3,  true, now() - interval '5 years 2 months 2 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '5 years 2 months 2 weeks', now() - interval '5 years 2 months 2 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0104-000000000000', '00000000-0000-0000-0000-000000000001', 'SLACK_INVITATION',      'Slack-inbjudan till Netler-mail',       4,  true, now() - interval '5 years 2 months 1 week',  '00000000-0000-0000-0000-000000000001', now() - interval '5 years 2 months 1 week',  now() - interval '5 years 2 months 1 week',  'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0105-000000000000', '00000000-0000-0000-0000-000000000001', 'FORTNOX_ADD_USER',      'Fortnox: Lägg till i kvitton & utlägg', 5,  true, now() - interval '5 years 2 months 1 week',  '00000000-0000-0000-0000-000000000001', now() - interval '5 years 2 months 1 week',  now() - interval '5 years 2 months 1 week',  'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0106-000000000000', '00000000-0000-0000-0000-000000000001', 'FORTNOX_RECEIPT_GROUP', 'Fortnox: Lägg till i kvittogruppen',   6,  true, now() - interval '5 years 2 months 1 week',  '00000000-0000-0000-0000-000000000001', now() - interval '5 years 2 months 1 week',  now() - interval '5 years 2 months 1 week',  'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0107-000000000000', '00000000-0000-0000-0000-000000000001', 'SLACK_WELCOME_MESSAGE', 'Skriv välkommen i Slack-kanalen',       7,  true, now() - interval '5 years 2 months',         '00000000-0000-0000-0000-000000000001', now() - interval '5 years 2 months',         now() - interval '5 years 2 months',         'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0108-000000000000', '00000000-0000-0000-0000-000000000001', 'SLACK_PROFILE_PHOTO',   'Be om profilbild till Slack',           8,  true, now() - interval '5 years 2 months',         '00000000-0000-0000-0000-000000000001', now() - interval '5 years 2 months',         now() - interval '5 years 2 months',         'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0109-000000000000', '00000000-0000-0000-0000-000000000001', 'SEND_WELCOME_LETTER',   'Skicka välkomstbrev till Netler-mail',  9,  true, now() - interval '5 years 2 months',         '00000000-0000-0000-0000-000000000001', now() - interval '5 years 2 months',         now() - interval '5 years 2 months',         'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0110-000000000000', '00000000-0000-0000-0000-000000000001', 'REQUEST_BANK_DETAILS',  'Be om bankuppgifter',                   10, true, now() - interval '5 years 1 month 3 weeks',  '00000000-0000-0000-0000-000000000001', now() - interval '5 years 1 month 3 weeks',  now() - interval '5 years 1 month 3 weeks',  'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0111-000000000000', '00000000-0000-0000-0000-000000000001', 'NOTIFY_PAYROLL',        'Meddela löneavdelningen',               11, true, now() - interval '5 years 1 month 3 weeks',  '00000000-0000-0000-0000-000000000001', now() - interval '5 years 1 month 3 weeks',  now() - interval '5 years 1 month 3 weeks',  'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0112-000000000000', '00000000-0000-0000-0000-000000000001', 'SETUP_PENSION',         'Lägg upp pension',                      12, true, now() - interval '5 years 1 month 2 weeks',  '00000000-0000-0000-0000-000000000001', now() - interval '5 years 1 month 2 weeks',  now() - interval '5 years 1 month 2 weeks',  'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),

    -- ── 02 Erik Lindqvist — all complete (started ~3 years 8 months ago) ───────
    ('dd000000-0000-0000-0201-000000000000', 'aa000000-0000-0000-0000-000000000002', 'CREATE_CV',             'Skapa CV',                              1,  true, now() - interval '3 years 8 months 2 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '3 years 8 months 2 weeks', now() - interval '3 years 8 months 2 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0202-000000000000', 'aa000000-0000-0000-0000-000000000002', 'NETLER_MAIL',           'Netler-mail via Google Admin',          2,  true, now() - interval '3 years 8 months 2 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '3 years 8 months 2 weeks', now() - interval '3 years 8 months 2 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0203-000000000000', 'aa000000-0000-0000-0000-000000000002', 'BIRTHDAY_IN_SLACK',     'Lägg in födelsedag i Slack',            3,  true, now() - interval '3 years 8 months 2 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '3 years 8 months 2 weeks', now() - interval '3 years 8 months 2 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0204-000000000000', 'aa000000-0000-0000-0000-000000000002', 'SLACK_INVITATION',      'Slack-inbjudan till Netler-mail',       4,  true, now() - interval '3 years 8 months 1 week',  '00000000-0000-0000-0000-000000000001', now() - interval '3 years 8 months 1 week',  now() - interval '3 years 8 months 1 week',  'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0205-000000000000', 'aa000000-0000-0000-0000-000000000002', 'FORTNOX_ADD_USER',      'Fortnox: Lägg till i kvitton & utlägg', 5,  true, now() - interval '3 years 8 months 1 week',  '00000000-0000-0000-0000-000000000001', now() - interval '3 years 8 months 1 week',  now() - interval '3 years 8 months 1 week',  'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0206-000000000000', 'aa000000-0000-0000-0000-000000000002', 'FORTNOX_RECEIPT_GROUP', 'Fortnox: Lägg till i kvittogruppen',   6,  true, now() - interval '3 years 8 months 1 week',  '00000000-0000-0000-0000-000000000001', now() - interval '3 years 8 months 1 week',  now() - interval '3 years 8 months 1 week',  'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0207-000000000000', 'aa000000-0000-0000-0000-000000000002', 'SLACK_WELCOME_MESSAGE', 'Skriv välkommen i Slack-kanalen',       7,  true, now() - interval '3 years 8 months',         '00000000-0000-0000-0000-000000000001', now() - interval '3 years 8 months',         now() - interval '3 years 8 months',         'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0208-000000000000', 'aa000000-0000-0000-0000-000000000002', 'SLACK_PROFILE_PHOTO',   'Be om profilbild till Slack',           8,  true, now() - interval '3 years 8 months',         '00000000-0000-0000-0000-000000000001', now() - interval '3 years 8 months',         now() - interval '3 years 8 months',         'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0209-000000000000', 'aa000000-0000-0000-0000-000000000002', 'SEND_WELCOME_LETTER',   'Skicka välkomstbrev till Netler-mail',  9,  true, now() - interval '3 years 8 months',         '00000000-0000-0000-0000-000000000001', now() - interval '3 years 8 months',         now() - interval '3 years 8 months',         'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0210-000000000000', 'aa000000-0000-0000-0000-000000000002', 'REQUEST_BANK_DETAILS',  'Be om bankuppgifter',                   10, true, now() - interval '3 years 7 months 3 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '3 years 7 months 3 weeks', now() - interval '3 years 7 months 3 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0211-000000000000', 'aa000000-0000-0000-0000-000000000002', 'NOTIFY_PAYROLL',        'Meddela löneavdelningen',               11, true, now() - interval '3 years 7 months 3 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '3 years 7 months 3 weeks', now() - interval '3 years 7 months 3 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0212-000000000000', 'aa000000-0000-0000-0000-000000000002', 'SETUP_PENSION',         'Lägg upp pension',                      12, true, now() - interval '3 years 7 months 2 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '3 years 7 months 2 weeks', now() - interval '3 years 7 months 2 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),

    -- ── 03 Sara Berg — all complete (started ~3 years 1 month ago) ─────────────
    ('dd000000-0000-0000-0301-000000000000', 'aa000000-0000-0000-0000-000000000003', 'CREATE_CV',             'Skapa CV',                              1,  true, now() - interval '3 years 1 month 2 weeks',  '00000000-0000-0000-0000-000000000001', now() - interval '3 years 1 month 2 weeks',  now() - interval '3 years 1 month 2 weeks',  'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0302-000000000000', 'aa000000-0000-0000-0000-000000000003', 'NETLER_MAIL',           'Netler-mail via Google Admin',          2,  true, now() - interval '3 years 1 month 2 weeks',  '00000000-0000-0000-0000-000000000001', now() - interval '3 years 1 month 2 weeks',  now() - interval '3 years 1 month 2 weeks',  'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0303-000000000000', 'aa000000-0000-0000-0000-000000000003', 'BIRTHDAY_IN_SLACK',     'Lägg in födelsedag i Slack',            3,  true, now() - interval '3 years 1 month 2 weeks',  '00000000-0000-0000-0000-000000000001', now() - interval '3 years 1 month 2 weeks',  now() - interval '3 years 1 month 2 weeks',  'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0304-000000000000', 'aa000000-0000-0000-0000-000000000003', 'SLACK_INVITATION',      'Slack-inbjudan till Netler-mail',       4,  true, now() - interval '3 years 1 month 1 week',   '00000000-0000-0000-0000-000000000001', now() - interval '3 years 1 month 1 week',   now() - interval '3 years 1 month 1 week',   'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0305-000000000000', 'aa000000-0000-0000-0000-000000000003', 'FORTNOX_ADD_USER',      'Fortnox: Lägg till i kvitton & utlägg', 5,  true, now() - interval '3 years 1 month 1 week',   '00000000-0000-0000-0000-000000000001', now() - interval '3 years 1 month 1 week',   now() - interval '3 years 1 month 1 week',   'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0306-000000000000', 'aa000000-0000-0000-0000-000000000003', 'FORTNOX_RECEIPT_GROUP', 'Fortnox: Lägg till i kvittogruppen',   6,  true, now() - interval '3 years 1 month 1 week',   '00000000-0000-0000-0000-000000000001', now() - interval '3 years 1 month 1 week',   now() - interval '3 years 1 month 1 week',   'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0307-000000000000', 'aa000000-0000-0000-0000-000000000003', 'SLACK_WELCOME_MESSAGE', 'Skriv välkommen i Slack-kanalen',       7,  true, now() - interval '3 years 1 month',          '00000000-0000-0000-0000-000000000001', now() - interval '3 years 1 month',          now() - interval '3 years 1 month',          'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0308-000000000000', 'aa000000-0000-0000-0000-000000000003', 'SLACK_PROFILE_PHOTO',   'Be om profilbild till Slack',           8,  true, now() - interval '3 years 1 month',          '00000000-0000-0000-0000-000000000001', now() - interval '3 years 1 month',          now() - interval '3 years 1 month',          'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0309-000000000000', 'aa000000-0000-0000-0000-000000000003', 'SEND_WELCOME_LETTER',   'Skicka välkomstbrev till Netler-mail',  9,  true, now() - interval '3 years 1 month',          '00000000-0000-0000-0000-000000000001', now() - interval '3 years 1 month',          now() - interval '3 years 1 month',          'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0310-000000000000', 'aa000000-0000-0000-0000-000000000003', 'REQUEST_BANK_DETAILS',  'Be om bankuppgifter',                   10, true, now() - interval '3 years 3 weeks',          '00000000-0000-0000-0000-000000000001', now() - interval '3 years 3 weeks',          now() - interval '3 years 3 weeks',          'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0311-000000000000', 'aa000000-0000-0000-0000-000000000003', 'NOTIFY_PAYROLL',        'Meddela löneavdelningen',               11, true, now() - interval '3 years 3 weeks',          '00000000-0000-0000-0000-000000000001', now() - interval '3 years 3 weeks',          now() - interval '3 years 3 weeks',          'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0312-000000000000', 'aa000000-0000-0000-0000-000000000003', 'SETUP_PENSION',         'Lägg upp pension',                      12, true, now() - interval '3 years 2 weeks',          '00000000-0000-0000-0000-000000000001', now() - interval '3 years 2 weeks',          now() - interval '3 years 2 weeks',          'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),

    -- ── 04 Johan Petersson — all complete (started ~2 years 10 months ago) ─────
    ('dd000000-0000-0000-0401-000000000000', 'aa000000-0000-0000-0000-000000000004', 'CREATE_CV',             'Skapa CV',                              1,  true, now() - interval '2 years 10 months 2 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '2 years 10 months 2 weeks', now() - interval '2 years 10 months 2 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0402-000000000000', 'aa000000-0000-0000-0000-000000000004', 'NETLER_MAIL',           'Netler-mail via Google Admin',          2,  true, now() - interval '2 years 10 months 2 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '2 years 10 months 2 weeks', now() - interval '2 years 10 months 2 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0403-000000000000', 'aa000000-0000-0000-0000-000000000004', 'BIRTHDAY_IN_SLACK',     'Lägg in födelsedag i Slack',            3,  true, now() - interval '2 years 10 months 2 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '2 years 10 months 2 weeks', now() - interval '2 years 10 months 2 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0404-000000000000', 'aa000000-0000-0000-0000-000000000004', 'SLACK_INVITATION',      'Slack-inbjudan till Netler-mail',       4,  true, now() - interval '2 years 10 months 1 week',  '00000000-0000-0000-0000-000000000001', now() - interval '2 years 10 months 1 week',  now() - interval '2 years 10 months 1 week',  'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0405-000000000000', 'aa000000-0000-0000-0000-000000000004', 'FORTNOX_ADD_USER',      'Fortnox: Lägg till i kvitton & utlägg', 5,  true, now() - interval '2 years 10 months 1 week',  '00000000-0000-0000-0000-000000000001', now() - interval '2 years 10 months 1 week',  now() - interval '2 years 10 months 1 week',  'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0406-000000000000', 'aa000000-0000-0000-0000-000000000004', 'FORTNOX_RECEIPT_GROUP', 'Fortnox: Lägg till i kvittogruppen',   6,  true, now() - interval '2 years 10 months 1 week',  '00000000-0000-0000-0000-000000000001', now() - interval '2 years 10 months 1 week',  now() - interval '2 years 10 months 1 week',  'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0407-000000000000', 'aa000000-0000-0000-0000-000000000004', 'SLACK_WELCOME_MESSAGE', 'Skriv välkommen i Slack-kanalen',       7,  true, now() - interval '2 years 10 months',         '00000000-0000-0000-0000-000000000001', now() - interval '2 years 10 months',         now() - interval '2 years 10 months',         'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0408-000000000000', 'aa000000-0000-0000-0000-000000000004', 'SLACK_PROFILE_PHOTO',   'Be om profilbild till Slack',           8,  true, now() - interval '2 years 10 months',         '00000000-0000-0000-0000-000000000001', now() - interval '2 years 10 months',         now() - interval '2 years 10 months',         'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0409-000000000000', 'aa000000-0000-0000-0000-000000000004', 'SEND_WELCOME_LETTER',   'Skicka välkomstbrev till Netler-mail',  9,  true, now() - interval '2 years 10 months',         '00000000-0000-0000-0000-000000000001', now() - interval '2 years 10 months',         now() - interval '2 years 10 months',         'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0410-000000000000', 'aa000000-0000-0000-0000-000000000004', 'REQUEST_BANK_DETAILS',  'Be om bankuppgifter',                   10, true, now() - interval '2 years 9 months 3 weeks',  '00000000-0000-0000-0000-000000000001', now() - interval '2 years 9 months 3 weeks',  now() - interval '2 years 9 months 3 weeks',  'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0411-000000000000', 'aa000000-0000-0000-0000-000000000004', 'NOTIFY_PAYROLL',        'Meddela löneavdelningen',               11, true, now() - interval '2 years 9 months 3 weeks',  '00000000-0000-0000-0000-000000000001', now() - interval '2 years 9 months 3 weeks',  now() - interval '2 years 9 months 3 weeks',  'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0412-000000000000', 'aa000000-0000-0000-0000-000000000004', 'SETUP_PENSION',         'Lägg upp pension',                      12, true, now() - interval '2 years 9 months 2 weeks',  '00000000-0000-0000-0000-000000000001', now() - interval '2 years 9 months 2 weeks',  now() - interval '2 years 9 months 2 weeks',  'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),

    -- ── 05 Lina Eriksson — all complete (started ~3 years 5 months ago) ────────
    ('dd000000-0000-0000-0501-000000000000', 'aa000000-0000-0000-0000-000000000005', 'CREATE_CV',             'Skapa CV',                              1,  true, now() - interval '3 years 5 months 2 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '3 years 5 months 2 weeks', now() - interval '3 years 5 months 2 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0502-000000000000', 'aa000000-0000-0000-0000-000000000005', 'NETLER_MAIL',           'Netler-mail via Google Admin',          2,  true, now() - interval '3 years 5 months 2 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '3 years 5 months 2 weeks', now() - interval '3 years 5 months 2 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0503-000000000000', 'aa000000-0000-0000-0000-000000000005', 'BIRTHDAY_IN_SLACK',     'Lägg in födelsedag i Slack',            3,  true, now() - interval '3 years 5 months 2 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '3 years 5 months 2 weeks', now() - interval '3 years 5 months 2 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0504-000000000000', 'aa000000-0000-0000-0000-000000000005', 'SLACK_INVITATION',      'Slack-inbjudan till Netler-mail',       4,  true, now() - interval '3 years 5 months 1 week',  '00000000-0000-0000-0000-000000000001', now() - interval '3 years 5 months 1 week',  now() - interval '3 years 5 months 1 week',  'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0505-000000000000', 'aa000000-0000-0000-0000-000000000005', 'FORTNOX_ADD_USER',      'Fortnox: Lägg till i kvitton & utlägg', 5,  true, now() - interval '3 years 5 months 1 week',  '00000000-0000-0000-0000-000000000001', now() - interval '3 years 5 months 1 week',  now() - interval '3 years 5 months 1 week',  'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0506-000000000000', 'aa000000-0000-0000-0000-000000000005', 'FORTNOX_RECEIPT_GROUP', 'Fortnox: Lägg till i kvittogruppen',   6,  true, now() - interval '3 years 5 months 1 week',  '00000000-0000-0000-0000-000000000001', now() - interval '3 years 5 months 1 week',  now() - interval '3 years 5 months 1 week',  'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0507-000000000000', 'aa000000-0000-0000-0000-000000000005', 'SLACK_WELCOME_MESSAGE', 'Skriv välkommen i Slack-kanalen',       7,  true, now() - interval '3 years 5 months',         '00000000-0000-0000-0000-000000000001', now() - interval '3 years 5 months',         now() - interval '3 years 5 months',         'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0508-000000000000', 'aa000000-0000-0000-0000-000000000005', 'SLACK_PROFILE_PHOTO',   'Be om profilbild till Slack',           8,  true, now() - interval '3 years 5 months',         '00000000-0000-0000-0000-000000000001', now() - interval '3 years 5 months',         now() - interval '3 years 5 months',         'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0509-000000000000', 'aa000000-0000-0000-0000-000000000005', 'SEND_WELCOME_LETTER',   'Skicka välkomstbrev till Netler-mail',  9,  true, now() - interval '3 years 5 months',         '00000000-0000-0000-0000-000000000001', now() - interval '3 years 5 months',         now() - interval '3 years 5 months',         'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0510-000000000000', 'aa000000-0000-0000-0000-000000000005', 'REQUEST_BANK_DETAILS',  'Be om bankuppgifter',                   10, true, now() - interval '3 years 4 months 3 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '3 years 4 months 3 weeks', now() - interval '3 years 4 months 3 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0511-000000000000', 'aa000000-0000-0000-0000-000000000005', 'NOTIFY_PAYROLL',        'Meddela löneavdelningen',               11, true, now() - interval '3 years 4 months 3 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '3 years 4 months 3 weeks', now() - interval '3 years 4 months 3 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0512-000000000000', 'aa000000-0000-0000-0000-000000000005', 'SETUP_PENSION',         'Lägg upp pension',                      12, true, now() - interval '3 years 4 months 2 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '3 years 4 months 2 weeks', now() - interval '3 years 4 months 2 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),

    -- ── 06 Mikael Svensson — all complete (started ~2 years 3 months ago) ──────
    ('dd000000-0000-0000-0601-000000000000', 'aa000000-0000-0000-0000-000000000006', 'CREATE_CV',             'Skapa CV',                              1,  true, now() - interval '2 years 3 months 2 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '2 years 3 months 2 weeks', now() - interval '2 years 3 months 2 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0602-000000000000', 'aa000000-0000-0000-0000-000000000006', 'NETLER_MAIL',           'Netler-mail via Google Admin',          2,  true, now() - interval '2 years 3 months 2 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '2 years 3 months 2 weeks', now() - interval '2 years 3 months 2 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0603-000000000000', 'aa000000-0000-0000-0000-000000000006', 'BIRTHDAY_IN_SLACK',     'Lägg in födelsedag i Slack',            3,  true, now() - interval '2 years 3 months 2 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '2 years 3 months 2 weeks', now() - interval '2 years 3 months 2 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0604-000000000000', 'aa000000-0000-0000-0000-000000000006', 'SLACK_INVITATION',      'Slack-inbjudan till Netler-mail',       4,  true, now() - interval '2 years 3 months 1 week',  '00000000-0000-0000-0000-000000000001', now() - interval '2 years 3 months 1 week',  now() - interval '2 years 3 months 1 week',  'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0605-000000000000', 'aa000000-0000-0000-0000-000000000006', 'FORTNOX_ADD_USER',      'Fortnox: Lägg till i kvitton & utlägg', 5,  true, now() - interval '2 years 3 months 1 week',  '00000000-0000-0000-0000-000000000001', now() - interval '2 years 3 months 1 week',  now() - interval '2 years 3 months 1 week',  'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0606-000000000000', 'aa000000-0000-0000-0000-000000000006', 'FORTNOX_RECEIPT_GROUP', 'Fortnox: Lägg till i kvittogruppen',   6,  true, now() - interval '2 years 3 months 1 week',  '00000000-0000-0000-0000-000000000001', now() - interval '2 years 3 months 1 week',  now() - interval '2 years 3 months 1 week',  'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0607-000000000000', 'aa000000-0000-0000-0000-000000000006', 'SLACK_WELCOME_MESSAGE', 'Skriv välkommen i Slack-kanalen',       7,  true, now() - interval '2 years 3 months',         '00000000-0000-0000-0000-000000000001', now() - interval '2 years 3 months',         now() - interval '2 years 3 months',         'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0608-000000000000', 'aa000000-0000-0000-0000-000000000006', 'SLACK_PROFILE_PHOTO',   'Be om profilbild till Slack',           8,  true, now() - interval '2 years 3 months',         '00000000-0000-0000-0000-000000000001', now() - interval '2 years 3 months',         now() - interval '2 years 3 months',         'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0609-000000000000', 'aa000000-0000-0000-0000-000000000006', 'SEND_WELCOME_LETTER',   'Skicka välkomstbrev till Netler-mail',  9,  true, now() - interval '2 years 3 months',         '00000000-0000-0000-0000-000000000001', now() - interval '2 years 3 months',         now() - interval '2 years 3 months',         'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0610-000000000000', 'aa000000-0000-0000-0000-000000000006', 'REQUEST_BANK_DETAILS',  'Be om bankuppgifter',                   10, true, now() - interval '2 years 2 months 3 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '2 years 2 months 3 weeks', now() - interval '2 years 2 months 3 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0611-000000000000', 'aa000000-0000-0000-0000-000000000006', 'NOTIFY_PAYROLL',        'Meddela löneavdelningen',               11, true, now() - interval '2 years 2 months 3 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '2 years 2 months 3 weeks', now() - interval '2 years 2 months 3 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0612-000000000000', 'aa000000-0000-0000-0000-000000000006', 'SETUP_PENSION',         'Lägg upp pension',                      12, true, now() - interval '2 years 2 months 2 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '2 years 2 months 2 weeks', now() - interval '2 years 2 months 2 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),

    -- ── 07 Anna Johansson — all complete (started ~2 years 7 months ago) ───────
    ('dd000000-0000-0000-0701-000000000000', 'aa000000-0000-0000-0000-000000000007', 'CREATE_CV',             'Skapa CV',                              1,  true, now() - interval '2 years 7 months 2 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '2 years 7 months 2 weeks', now() - interval '2 years 7 months 2 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0702-000000000000', 'aa000000-0000-0000-0000-000000000007', 'NETLER_MAIL',           'Netler-mail via Google Admin',          2,  true, now() - interval '2 years 7 months 2 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '2 years 7 months 2 weeks', now() - interval '2 years 7 months 2 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0703-000000000000', 'aa000000-0000-0000-0000-000000000007', 'BIRTHDAY_IN_SLACK',     'Lägg in födelsedag i Slack',            3,  true, now() - interval '2 years 7 months 2 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '2 years 7 months 2 weeks', now() - interval '2 years 7 months 2 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0704-000000000000', 'aa000000-0000-0000-0000-000000000007', 'SLACK_INVITATION',      'Slack-inbjudan till Netler-mail',       4,  true, now() - interval '2 years 7 months 1 week',  '00000000-0000-0000-0000-000000000001', now() - interval '2 years 7 months 1 week',  now() - interval '2 years 7 months 1 week',  'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0705-000000000000', 'aa000000-0000-0000-0000-000000000007', 'FORTNOX_ADD_USER',      'Fortnox: Lägg till i kvitton & utlägg', 5,  true, now() - interval '2 years 7 months 1 week',  '00000000-0000-0000-0000-000000000001', now() - interval '2 years 7 months 1 week',  now() - interval '2 years 7 months 1 week',  'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0706-000000000000', 'aa000000-0000-0000-0000-000000000007', 'FORTNOX_RECEIPT_GROUP', 'Fortnox: Lägg till i kvittogruppen',   6,  true, now() - interval '2 years 7 months 1 week',  '00000000-0000-0000-0000-000000000001', now() - interval '2 years 7 months 1 week',  now() - interval '2 years 7 months 1 week',  'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0707-000000000000', 'aa000000-0000-0000-0000-000000000007', 'SLACK_WELCOME_MESSAGE', 'Skriv välkommen i Slack-kanalen',       7,  true, now() - interval '2 years 7 months',         '00000000-0000-0000-0000-000000000001', now() - interval '2 years 7 months',         now() - interval '2 years 7 months',         'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0708-000000000000', 'aa000000-0000-0000-0000-000000000007', 'SLACK_PROFILE_PHOTO',   'Be om profilbild till Slack',           8,  true, now() - interval '2 years 7 months',         '00000000-0000-0000-0000-000000000001', now() - interval '2 years 7 months',         now() - interval '2 years 7 months',         'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0709-000000000000', 'aa000000-0000-0000-0000-000000000007', 'SEND_WELCOME_LETTER',   'Skicka välkomstbrev till Netler-mail',  9,  true, now() - interval '2 years 7 months',         '00000000-0000-0000-0000-000000000001', now() - interval '2 years 7 months',         now() - interval '2 years 7 months',         'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0710-000000000000', 'aa000000-0000-0000-0000-000000000007', 'REQUEST_BANK_DETAILS',  'Be om bankuppgifter',                   10, true, now() - interval '2 years 6 months 3 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '2 years 6 months 3 weeks', now() - interval '2 years 6 months 3 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0711-000000000000', 'aa000000-0000-0000-0000-000000000007', 'NOTIFY_PAYROLL',        'Meddela löneavdelningen',               11, true, now() - interval '2 years 6 months 3 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '2 years 6 months 3 weeks', now() - interval '2 years 6 months 3 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0712-000000000000', 'aa000000-0000-0000-0000-000000000007', 'SETUP_PENSION',         'Lägg upp pension',                      12, true, now() - interval '2 years 6 months 2 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '2 years 6 months 2 weeks', now() - interval '2 years 6 months 2 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),

    -- ── 08 Test Employee — all complete (started ~3 months ago) ─────────────────
    ('dd000000-0000-0000-0801-000000000000', 'aa000000-0000-0000-0000-000000000008', 'CREATE_CV',             'Skapa CV',                              1,  true, now() - interval '3 months 2 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '3 months 2 weeks', now() - interval '3 months 2 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0802-000000000000', 'aa000000-0000-0000-0000-000000000008', 'NETLER_MAIL',           'Netler-mail via Google Admin',          2,  true, now() - interval '3 months 2 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '3 months 2 weeks', now() - interval '3 months 2 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0803-000000000000', 'aa000000-0000-0000-0000-000000000008', 'BIRTHDAY_IN_SLACK',     'Lägg in födelsedag i Slack',            3,  true, now() - interval '3 months 2 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '3 months 2 weeks', now() - interval '3 months 2 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0804-000000000000', 'aa000000-0000-0000-0000-000000000008', 'SLACK_INVITATION',      'Slack-inbjudan till Netler-mail',       4,  true, now() - interval '3 months 1 week',  '00000000-0000-0000-0000-000000000001', now() - interval '3 months 1 week',  now() - interval '3 months 1 week',  'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0805-000000000000', 'aa000000-0000-0000-0000-000000000008', 'FORTNOX_ADD_USER',      'Fortnox: Lägg till i kvitton & utlägg', 5,  true, now() - interval '3 months 1 week',  '00000000-0000-0000-0000-000000000001', now() - interval '3 months 1 week',  now() - interval '3 months 1 week',  'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0806-000000000000', 'aa000000-0000-0000-0000-000000000008', 'FORTNOX_RECEIPT_GROUP', 'Fortnox: Lägg till i kvittogruppen',   6,  true, now() - interval '3 months 1 week',  '00000000-0000-0000-0000-000000000001', now() - interval '3 months 1 week',  now() - interval '3 months 1 week',  'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0807-000000000000', 'aa000000-0000-0000-0000-000000000008', 'SLACK_WELCOME_MESSAGE', 'Skriv välkommen i Slack-kanalen',       7,  true, now() - interval '3 months',         '00000000-0000-0000-0000-000000000001', now() - interval '3 months',         now() - interval '3 months',         'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0808-000000000000', 'aa000000-0000-0000-0000-000000000008', 'SLACK_PROFILE_PHOTO',   'Be om profilbild till Slack',           8,  true, now() - interval '3 months',         '00000000-0000-0000-0000-000000000001', now() - interval '3 months',         now() - interval '3 months',         'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0809-000000000000', 'aa000000-0000-0000-0000-000000000008', 'SEND_WELCOME_LETTER',   'Skicka välkomstbrev till Netler-mail',  9,  true, now() - interval '3 months',         '00000000-0000-0000-0000-000000000001', now() - interval '3 months',         now() - interval '3 months',         'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0810-000000000000', 'aa000000-0000-0000-0000-000000000008', 'REQUEST_BANK_DETAILS',  'Be om bankuppgifter',                   10, true, now() - interval '2 months 3 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '2 months 3 weeks', now() - interval '2 months 3 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0811-000000000000', 'aa000000-0000-0000-0000-000000000008', 'NOTIFY_PAYROLL',        'Meddela löneavdelningen',               11, true, now() - interval '2 months 3 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '2 months 3 weeks', now() - interval '2 months 3 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0812-000000000000', 'aa000000-0000-0000-0000-000000000008', 'SETUP_PENSION',         'Lägg upp pension',                      12, true, now() - interval '2 months 2 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '2 months 2 weeks', now() - interval '2 months 2 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),

    -- ── 09 Fredrik Norling — all complete (started ~15 months ago) ──────────────
    ('dd000000-0000-0000-0901-000000000000', 'aa000000-0000-0000-0000-000000000009', 'CREATE_CV',             'Skapa CV',                              1,  true, now() - interval '15 months 2 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '15 months 2 weeks', now() - interval '15 months 2 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0902-000000000000', 'aa000000-0000-0000-0000-000000000009', 'NETLER_MAIL',           'Netler-mail via Google Admin',          2,  true, now() - interval '15 months 2 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '15 months 2 weeks', now() - interval '15 months 2 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0903-000000000000', 'aa000000-0000-0000-0000-000000000009', 'BIRTHDAY_IN_SLACK',     'Lägg in födelsedag i Slack',            3,  true, now() - interval '15 months 2 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '15 months 2 weeks', now() - interval '15 months 2 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0904-000000000000', 'aa000000-0000-0000-0000-000000000009', 'SLACK_INVITATION',      'Slack-inbjudan till Netler-mail',       4,  true, now() - interval '15 months 1 week',  '00000000-0000-0000-0000-000000000001', now() - interval '15 months 1 week',  now() - interval '15 months 1 week',  'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0905-000000000000', 'aa000000-0000-0000-0000-000000000009', 'FORTNOX_ADD_USER',      'Fortnox: Lägg till i kvitton & utlägg', 5,  true, now() - interval '15 months 1 week',  '00000000-0000-0000-0000-000000000001', now() - interval '15 months 1 week',  now() - interval '15 months 1 week',  'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0906-000000000000', 'aa000000-0000-0000-0000-000000000009', 'FORTNOX_RECEIPT_GROUP', 'Fortnox: Lägg till i kvittogruppen',   6,  true, now() - interval '15 months 1 week',  '00000000-0000-0000-0000-000000000001', now() - interval '15 months 1 week',  now() - interval '15 months 1 week',  'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0907-000000000000', 'aa000000-0000-0000-0000-000000000009', 'SLACK_WELCOME_MESSAGE', 'Skriv välkommen i Slack-kanalen',       7,  true, now() - interval '15 months',         '00000000-0000-0000-0000-000000000001', now() - interval '15 months',         now() - interval '15 months',         'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0908-000000000000', 'aa000000-0000-0000-0000-000000000009', 'SLACK_PROFILE_PHOTO',   'Be om profilbild till Slack',           8,  true, now() - interval '15 months',         '00000000-0000-0000-0000-000000000001', now() - interval '15 months',         now() - interval '15 months',         'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0909-000000000000', 'aa000000-0000-0000-0000-000000000009', 'SEND_WELCOME_LETTER',   'Skicka välkomstbrev till Netler-mail',  9,  true, now() - interval '15 months',         '00000000-0000-0000-0000-000000000001', now() - interval '15 months',         now() - interval '15 months',         'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0910-000000000000', 'aa000000-0000-0000-0000-000000000009', 'REQUEST_BANK_DETAILS',  'Be om bankuppgifter',                   10, true, now() - interval '14 months 3 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '14 months 3 weeks', now() - interval '14 months 3 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0911-000000000000', 'aa000000-0000-0000-0000-000000000009', 'NOTIFY_PAYROLL',        'Meddela löneavdelningen',               11, true, now() - interval '14 months 3 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '14 months 3 weeks', now() - interval '14 months 3 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-0912-000000000000', 'aa000000-0000-0000-0000-000000000009', 'SETUP_PENSION',         'Lägg upp pension',                      12, true, now() - interval '14 months 2 weeks', '00000000-0000-0000-0000-000000000001', now() - interval '14 months 2 weeks', now() - interval '14 months 2 weeks', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),

    -- ── 10 Klara Lindström — tasks 1–6 complete, 7–12 open (started ~3 weeks ago)
    ('dd000000-0000-0000-1001-000000000000', 'aa000000-0000-0000-0000-000000000014', 'CREATE_CV',             'Skapa CV',                              1,  true,  now() - interval '2 weeks 5 days', '00000000-0000-0000-0000-000000000001', now() - interval '3 weeks', now() - interval '2 weeks 5 days', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-1002-000000000000', 'aa000000-0000-0000-0000-000000000014', 'NETLER_MAIL',           'Netler-mail via Google Admin',          2,  true,  now() - interval '2 weeks 4 days', '00000000-0000-0000-0000-000000000001', now() - interval '3 weeks', now() - interval '2 weeks 4 days', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-1003-000000000000', 'aa000000-0000-0000-0000-000000000014', 'BIRTHDAY_IN_SLACK',     'Lägg in födelsedag i Slack',            3,  true,  now() - interval '2 weeks 3 days', '00000000-0000-0000-0000-000000000001', now() - interval '3 weeks', now() - interval '2 weeks 3 days', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-1004-000000000000', 'aa000000-0000-0000-0000-000000000014', 'SLACK_INVITATION',      'Slack-inbjudan till Netler-mail',       4,  true,  now() - interval '2 weeks 1 day',  '00000000-0000-0000-0000-000000000001', now() - interval '3 weeks', now() - interval '2 weeks 1 day',  'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-1005-000000000000', 'aa000000-0000-0000-0000-000000000014', 'FORTNOX_ADD_USER',      'Fortnox: Lägg till i kvitton & utlägg', 5,  true,  now() - interval '1 week 4 days',  '00000000-0000-0000-0000-000000000001', now() - interval '3 weeks', now() - interval '1 week 4 days',  'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-1006-000000000000', 'aa000000-0000-0000-0000-000000000014', 'FORTNOX_RECEIPT_GROUP', 'Fortnox: Lägg till i kvittogruppen',   6,  true,  now() - interval '3 days',         '00000000-0000-0000-0000-000000000001', now() - interval '3 weeks', now() - interval '3 days',         'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-1007-000000000000', 'aa000000-0000-0000-0000-000000000014', 'SLACK_WELCOME_MESSAGE', 'Skriv välkommen i Slack-kanalen',       7,  false, NULL,                             NULL,                                    now() - interval '3 weeks', now() - interval '3 weeks',        'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-1008-000000000000', 'aa000000-0000-0000-0000-000000000014', 'SLACK_PROFILE_PHOTO',   'Be om profilbild till Slack',           8,  false, NULL,                             NULL,                                    now() - interval '3 weeks', now() - interval '3 weeks',        'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-1009-000000000000', 'aa000000-0000-0000-0000-000000000014', 'SEND_WELCOME_LETTER',   'Skicka välkomstbrev till Netler-mail',  9,  false, NULL,                             NULL,                                    now() - interval '3 weeks', now() - interval '3 weeks',        'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-1010-000000000000', 'aa000000-0000-0000-0000-000000000014', 'REQUEST_BANK_DETAILS',  'Be om bankuppgifter',                   10, false, NULL,                             NULL,                                    now() - interval '3 weeks', now() - interval '3 weeks',        'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-1011-000000000000', 'aa000000-0000-0000-0000-000000000014', 'NOTIFY_PAYROLL',        'Meddela löneavdelningen',               11, false, NULL,                             NULL,                                    now() - interval '3 weeks', now() - interval '3 weeks',        'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-1012-000000000000', 'aa000000-0000-0000-0000-000000000014', 'SETUP_PENSION',         'Lägg upp pension',                      12, false, NULL,                             NULL,                                    now() - interval '3 weeks', now() - interval '3 weeks',        'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),

    -- ── 11 Oscar Hansson — tasks 1–3 complete, 4–12 open (started ~10 days ago) ─
    ('dd000000-0000-0000-1101-000000000000', 'aa000000-0000-0000-0000-000000000015', 'CREATE_CV',             'Skapa CV',                              1,  true,  now() - interval '1 week',  '00000000-0000-0000-0000-000000000001', now() - interval '10 days', now() - interval '1 week',  'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-1102-000000000000', 'aa000000-0000-0000-0000-000000000015', 'NETLER_MAIL',           'Netler-mail via Google Admin',          2,  true,  now() - interval '6 days',  '00000000-0000-0000-0000-000000000001', now() - interval '10 days', now() - interval '6 days',  'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-1103-000000000000', 'aa000000-0000-0000-0000-000000000015', 'BIRTHDAY_IN_SLACK',     'Lägg in födelsedag i Slack',            3,  true,  now() - interval '2 days',  '00000000-0000-0000-0000-000000000001', now() - interval '10 days', now() - interval '2 days',  'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-1104-000000000000', 'aa000000-0000-0000-0000-000000000015', 'SLACK_INVITATION',      'Slack-inbjudan till Netler-mail',       4,  false, NULL,                       NULL,                                    now() - interval '10 days', now() - interval '10 days', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-1105-000000000000', 'aa000000-0000-0000-0000-000000000015', 'FORTNOX_ADD_USER',      'Fortnox: Lägg till i kvitton & utlägg', 5,  false, NULL,                       NULL,                                    now() - interval '10 days', now() - interval '10 days', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-1106-000000000000', 'aa000000-0000-0000-0000-000000000015', 'FORTNOX_RECEIPT_GROUP', 'Fortnox: Lägg till i kvittogruppen',   6,  false, NULL,                       NULL,                                    now() - interval '10 days', now() - interval '10 days', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-1107-000000000000', 'aa000000-0000-0000-0000-000000000015', 'SLACK_WELCOME_MESSAGE', 'Skriv välkommen i Slack-kanalen',       7,  false, NULL,                       NULL,                                    now() - interval '10 days', now() - interval '10 days', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-1108-000000000000', 'aa000000-0000-0000-0000-000000000015', 'SLACK_PROFILE_PHOTO',   'Be om profilbild till Slack',           8,  false, NULL,                       NULL,                                    now() - interval '10 days', now() - interval '10 days', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-1109-000000000000', 'aa000000-0000-0000-0000-000000000015', 'SEND_WELCOME_LETTER',   'Skicka välkomstbrev till Netler-mail',  9,  false, NULL,                       NULL,                                    now() - interval '10 days', now() - interval '10 days', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-1110-000000000000', 'aa000000-0000-0000-0000-000000000015', 'REQUEST_BANK_DETAILS',  'Be om bankuppgifter',                   10, false, NULL,                       NULL,                                    now() - interval '10 days', now() - interval '10 days', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-1111-000000000000', 'aa000000-0000-0000-0000-000000000015', 'NOTIFY_PAYROLL',        'Meddela löneavdelningen',               11, false, NULL,                       NULL,                                    now() - interval '10 days', now() - interval '10 days', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com'),
    ('dd000000-0000-0000-1112-000000000000', 'aa000000-0000-0000-0000-000000000015', 'SETUP_PENSION',         'Lägg upp pension',                      12, false, NULL,                       NULL,                                    now() - interval '10 days', now() - interval '10 days', 'marcus.karlsson@netler.com', 'marcus.karlsson@netler.com')

ON CONFLICT (employee_id, task_key) DO NOTHING;


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
     'Vacation', 5, '00000000-0000-0000-0000-000000000001'),

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
     'General', 11, '00000000-0000-0000-0000-000000000001')

ON CONFLICT (id) DO NOTHING;


-- =============================================================================
-- NEWS POSTS
-- =============================================================================

INSERT INTO news_posts (id, title, body, author_id, published_at, pinned)
VALUES
    ('00000000-0000-0000-0007-000000000001',
     'Welcome to the Netler Intranet',
     'We built this platform to make everyday work easier for everyone at Netler. Use it to manage your profile and bank details, submit vacation requests, follow client assignments, and stay up to date with company news and events. Explore the sections in the menu and let us know if you have feedback or ideas for improvements.',
     '00000000-0000-0000-0000-000000000001',
     now() - interval '7 months', TRUE),

    ('00000000-0000-0000-0007-000000000002',
     'New Client: Volvo Cars',
     'We are excited to announce that Netler has started a new multi-year engagement with Volvo Cars. Mikael Svensson will be leading work on their Connected Car Platform, focusing on backend services and cloud infrastructure for next-generation vehicle connectivity. This is exactly the kind of complex, impactful work we love to do. Welcome aboard, Volvo Cars!',
     '00000000-0000-0000-0000-000000000001',
     now() - interval '4 months', FALSE),

    ('00000000-0000-0000-0007-000000000003',
     'Q1 Company Update',
     'We are off to a strong start this year. Revenue is up 24% compared to Q1 last year, and we have grown the team by three new consultants. All six of our consultants are currently on active assignments or in final negotiation for their next placement. A huge thank you to everyone who has contributed — client satisfaction scores are at an all-time high across all accounts.',
     '00000000-0000-0000-0000-000000000001',
     now() - interval '2 months', FALSE),

    ('00000000-0000-0000-0007-000000000004',
     'Assignment Ending Soon — Johan Petersson',
     'A heads-up that Johan Petersson''s current assignment at IKEA Digital concludes soon. Johan has done great work building out their Design System and Component Library. We are actively in conversations with several clients about Johan''s next placement. If you have any leads or connections, reach out to Marcus directly.',
     '00000000-0000-0000-0000-000000000001',
     now() - interval '6 weeks', FALSE),

    -- Draft — not published, only visible to admins
    ('00000000-0000-0000-0007-000000000005',
     'Summer Party — Save the Date!',
     'We are planning an amazing summer party this year. Block off the last Friday in June — more details to follow on location, activities, and whether partners are welcome. We are looking at venues in Stockholm.',
     '00000000-0000-0000-0000-000000000001',
     NULL, FALSE),

    ('00000000-0000-0000-0007-000000000006',
     'New Client Partnership: Ericsson',
     '<p>We are excited to share that Netler has signed a new framework agreement with Ericsson. This opens the door for multiple consultant placements within their cloud and network infrastructure teams over the coming years.</p><p>Lina Eriksson''s previous work at Ericsson was instrumental in building this relationship. A big thank you to Lina for her outstanding work and professionalism throughout that engagement.</p>',
     '00000000-0000-0000-0000-000000000001',
     now() - interval '3 months', FALSE),

    ('00000000-0000-0000-0007-000000000007',
     'Welcome Fredrik Norling!',
     '<p>We are thrilled to welcome Fredrik Norling to the Netler team! Fredrik joins us as a Backend Developer with a strong focus on Java, Spring Boot, and distributed systems.</p><p>Fredrik hit the ground running and is already on assignment working on an exciting project. Welcome aboard, Fredrik — glad to have you with us!</p>',
     '00000000-0000-0000-0000-000000000001',
     now() - interval '3 months', FALSE),

    ('00000000-0000-0000-0007-000000000008',
     'Salary Reviews — Book Your Slot',
     '<p>Annual salary reviews are coming up. All employees should book a 30-minute slot with Marcus before the deadline.</p><p>To prepare, think about your contributions over the past year, any skills you have developed, and your goals for the next 12 months. The review is a two-way conversation — come with questions.</p><p>Slots can be booked directly via the calendar link Marcus will send over Slack.</p>',
     '00000000-0000-0000-0000-000000000001',
     now() - interval '1 month', FALSE)

ON CONFLICT (id) DO NOTHING;


-- =============================================================================
-- EVENTS
-- =============================================================================

INSERT INTO events (id, title, description, location, event_date, end_date, all_day, author_id)
VALUES
    ('00000000-0000-0000-0008-000000000001',
     'Tech Talk: AI-Assisted Development',
     'Internal knowledge-sharing session on how to use AI coding tools effectively in consulting work. Presented by Erik Lindqvist and Anna Johansson. Bring questions — this will be interactive.',
     'Remote – Zoom link sent by email',
     now() - interval '2 weeks', now() - interval '2 weeks', FALSE,
     '00000000-0000-0000-0000-000000000001'),

    ('00000000-0000-0000-0008-000000000002',
     'Q2 All-Hands Meeting',
     'Quarterly company meeting covering financials, project updates, upcoming placements, and team news. Attendance required for all employees.',
     'Netler HQ – Storgatan 12, Stockholm',
     now() - interval '1 day', now() - interval '1 day', FALSE,
     '00000000-0000-0000-0000-000000000001'),

    ('00000000-0000-0000-0008-000000000003',
     'Salary Review Deadline',
     'All salary review discussions must be completed by this date. If you have not yet had your review with Marcus, reach out to book a slot.',
     NULL,
     now() + interval '3 weeks', NULL, TRUE,
     '00000000-0000-0000-0000-000000000001'),

    ('00000000-0000-0000-0008-000000000004',
     'Summer Party',
     'Annual Netler summer party! Food, drinks, and outdoor activities. Partners are welcome. Exact programme to be announced closer to the date.',
     'Fotografiska, Stadsgårdshamnen 22, Stockholm',
     now() + interval '2 months 2 weeks', now() + interval '2 months 2 weeks', TRUE,
     '00000000-0000-0000-0000-000000000001')

ON CONFLICT (id) DO NOTHING;
