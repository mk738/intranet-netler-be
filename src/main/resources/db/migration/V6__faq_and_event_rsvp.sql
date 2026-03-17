-- V6__faq_and_event_rsvp.sql

-- ── FAQ ───────────────────────────────────────────────────────────────────────
CREATE TABLE faq (
    id          UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    question    VARCHAR(1000) NOT NULL,
    answer      TEXT         NOT NULL,
    category    VARCHAR(255),
    sort_order  INT          NOT NULL DEFAULT 0,
    author_id   UUID         NOT NULL REFERENCES employees(id) ON DELETE RESTRICT,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(255),
    updated_by  VARCHAR(255)
);

CREATE INDEX idx_faq_sort ON faq(sort_order ASC, created_at ASC);

-- ── Event RSVP ────────────────────────────────────────────────────────────────
CREATE TABLE event_rsvp (
    id          UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    event_id    UUID        NOT NULL REFERENCES events(id)    ON DELETE CASCADE,
    employee_id UUID        NOT NULL REFERENCES employees(id) ON DELETE CASCADE,
    status      VARCHAR(20) NOT NULL CHECK (status IN ('GOING', 'NOT_GOING', 'MAYBE')),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(255),
    updated_by  VARCHAR(255),
    CONSTRAINT uq_rsvp_event_employee UNIQUE (event_id, employee_id)
);

CREATE INDEX idx_rsvp_event ON event_rsvp(event_id);

-- ── FAQ seed data ─────────────────────────────────────────────────────────────
INSERT INTO faq (id, question, answer, category, sort_order, author_id) VALUES
    ('00000000-0000-0000-0009-000000000001',
     'How do I submit a vacation request?',
     'Go to the Vacation section in the menu, click "New Request", choose your start and end dates, and submit. Your request will be reviewed by an admin within a few days and you will be notified by email once a decision has been made.',
     'Vacation', 0,
     '00000000-0000-0000-0000-000000000001'),

    ('00000000-0000-0000-0009-000000000002',
     'Can I cancel a vacation request after submitting it?',
     'Yes, you can cancel a pending request from the Vacation section as long as it has not yet been approved or rejected. Once approved, you will need to contact Marcus directly to cancel.',
     'Vacation', 1,
     '00000000-0000-0000-0000-000000000001'),

    ('00000000-0000-0000-0009-000000000003',
     'How do I update my personal information?',
     'Navigate to your Profile from the top-right menu. You can update your contact details, address, and emergency contact directly. Your start date can only be updated by an admin.',
     'Profile', 2,
     '00000000-0000-0000-0000-000000000001'),

    ('00000000-0000-0000-0009-000000000004',
     'How do I update my bank details for salary payments?',
     'Go to Profile → Bank Info. Enter your bank name, clearing number, and account number. This information is stored securely and is only visible to you and admins.',
     'Profile', 3,
     '00000000-0000-0000-0000-000000000001'),

    ('00000000-0000-0000-0009-000000000005',
     'Who do I contact if I have an issue with my assignment?',
     'Reach out to Marcus Karlsson directly via email or Slack. For urgent matters, call +46 70 123 45 67.',
     'Assignments', 4,
     '00000000-0000-0000-0000-000000000001'),

    ('00000000-0000-0000-0009-000000000006',
     'How many vacation days am I entitled to per year?',
     'All Netler consultants are entitled to 25 days of paid vacation per calendar year in accordance with Swedish law. Days not taken by December 31st are carried over for up to one additional year.',
     'Vacation', 5,
     '00000000-0000-0000-0000-000000000001');

-- ── RSVP seed data ────────────────────────────────────────────────────────────
-- A few RSVPs for the Tech Talk event (00000000-0000-0000-0008-000000000001)
INSERT INTO event_rsvp (id, event_id, employee_id, status) VALUES
    ('00000000-0000-0000-000a-000000000001',
     '00000000-0000-0000-0008-000000000001',
     '00000000-0000-0000-0000-000000000001', 'GOING'),
    ('00000000-0000-0000-000a-000000000002',
     '00000000-0000-0000-0008-000000000001',
     '00000000-0000-0000-0000-000000000002', 'GOING'),
    ('00000000-0000-0000-000a-000000000003',
     '00000000-0000-0000-0008-000000000001',
     '00000000-0000-0000-0000-000000000003', 'GOING'),
    ('00000000-0000-0000-000a-000000000004',
     '00000000-0000-0000-0008-000000000001',
     '00000000-0000-0000-0000-000000000004', 'MAYBE'),
    ('00000000-0000-0000-000a-000000000005',
     '00000000-0000-0000-0008-000000000001',
     '00000000-0000-0000-0000-000000000005', 'NOT_GOING');
