-- V13__fix_card_attachments_fk.sql
--
-- Hibernate ddl-auto=update added a second FK constraint on card_attachments.card_id
-- (fkftujggub59v4v6qu2qtaf89gc) WITHOUT ON DELETE CASCADE, shadowing the correct
-- cascade constraint created in V12.  Drop the Hibernate-generated one so that
-- deleting a board_card cascades cleanly to card_attachments.

ALTER TABLE card_attachments
    DROP CONSTRAINT IF EXISTS fkftujggub59v4v6qu2qtaf89gc;
