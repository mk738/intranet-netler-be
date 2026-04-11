-- =============================================================================
-- Netler Intranet — test environment DB reset
-- =============================================================================
-- Drops all tables so Hibernate recreates the schema clean on next startup,
-- then the test-data.sql seed runs automatically.
--
-- Usage (Railway test environment):
--   1. Run this script against the test database
--   2. Restart the Railway pod
-- =============================================================================

-- Leaf tables first, then parent tables
DROP TABLE IF EXISTS card_attachments       CASCADE;
DROP TABLE IF EXISTS board_comments         CASCADE;
DROP TABLE IF EXISTS board_cards            CASCADE;
DROP TABLE IF EXISTS board_columns          CASCADE;
DROP TABLE IF EXISTS boards                 CASCADE;

DROP TABLE IF EXISTS event_rsvp             CASCADE;
DROP TABLE IF EXISTS events                 CASCADE;
DROP TABLE IF EXISTS news_posts             CASCADE;
DROP TABLE IF EXISTS faq                    CASCADE;

DROP TABLE IF EXISTS onboarding_template_items CASCADE;

DROP TABLE IF EXISTS candidates             CASCADE;
DROP TABLE IF EXISTS skills                 CASCADE;
DROP TABLE IF EXISTS employee_skills        CASCADE;

DROP TABLE IF EXISTS card_attachments       CASCADE;
DROP TABLE IF EXISTS employee_benefit       CASCADE;
DROP TABLE IF EXISTS employee_contract      CASCADE;
DROP TABLE IF EXISTS employee_cv            CASCADE;
DROP TABLE IF EXISTS employee_avatars       CASCADE;
DROP TABLE IF EXISTS bank_info              CASCADE;
DROP TABLE IF EXISTS education              CASCADE;
DROP TABLE IF EXISTS vacation_requests      CASCADE;
DROP TABLE IF EXISTS assignments            CASCADE;
DROP TABLE IF EXISTS clients                CASCADE;
DROP TABLE IF EXISTS employee_profiles      CASCADE;
DROP TABLE IF EXISTS employees              CASCADE;

-- Flyway history — drop so Flyway reruns migrations if re-enabled in future
DROP TABLE IF EXISTS flyway_schema_history  CASCADE;
