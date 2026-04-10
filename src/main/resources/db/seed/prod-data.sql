-- =============================================================================
-- Netler Intranet — production seed data
-- =============================================================================
-- Contains only reference/template data — no employee data.
-- Seeded programmatically by OnboardingTemplateSeed on first startup.
-- This file serves as a manual fallback if needed.
-- =============================================================================

-- =============================================================================
-- ONBOARDING TEMPLATE
-- =============================================================================

INSERT INTO onboarding_template_items
    (id, task_key, label_sv, sort_order, active, created_at, updated_at)
VALUES
    (gen_random_uuid(), 'CREATE_CV',             'Skapa CV',                                    1,  true, now(), now()),
    (gen_random_uuid(), 'NETLER_MAIL',           'Netler-mail via Google Admin',                2,  true, now(), now()),
    (gen_random_uuid(), 'BIRTHDAY_IN_SLACK',     'Lägg in födelsedag i Slack',                  3,  true, now(), now()),
    (gen_random_uuid(), 'SLACK_INVITATION',      'Slack-inbjudan till Netler-mail',             4,  true, now(), now()),
    (gen_random_uuid(), 'FORTNOX_ADD_USER',      'Fortnox: Lägg till i kvitton & utlägg',       5,  true, now(), now()),
    (gen_random_uuid(), 'FORTNOX_RECEIPT_GROUP', 'Fortnox: Lägg till i kvittogruppen',          6,  true, now(), now()),
    (gen_random_uuid(), 'SLACK_WELCOME_MESSAGE', 'Skriv välkommen i Slack-kanalen',             7,  true, now(), now()),
    (gen_random_uuid(), 'SLACK_PROFILE_PHOTO',   'Be om profilbild till Slack',                 8,  true, now(), now()),
    (gen_random_uuid(), 'SEND_WELCOME_LETTER',   'Skicka välkomstbrev till Netler-mail',        9,  true, now(), now()),
    (gen_random_uuid(), 'REQUEST_BANK_DETAILS',  'Be om bankuppgifter',                         10, true, now(), now()),
    (gen_random_uuid(), 'NOTIFY_PAYROLL',        'Meddela löneavdelningen',                     11, true, now(), now()),
    (gen_random_uuid(), 'SETUP_PENSION',         'Lägg upp pension',                            12, true, now(), now())
ON CONFLICT (task_key) DO NOTHING;
