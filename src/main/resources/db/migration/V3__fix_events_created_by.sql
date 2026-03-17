-- V3__fix_events_created_by.sql
-- events.created_by was a UUID FK, conflicting with the inherited Auditable
-- VARCHAR(255) audit column of the same name.
-- Rename the FK column to author_id and add a proper nullable audit column.

ALTER TABLE events
    RENAME COLUMN created_by TO author_id;

ALTER TABLE events
    ADD COLUMN created_by VARCHAR(255);
