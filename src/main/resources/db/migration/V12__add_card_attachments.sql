-- V12__add_card_attachments.sql

CREATE TABLE card_attachments (
    id           UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    card_id      UUID        NOT NULL REFERENCES board_cards(id) ON DELETE CASCADE,
    file_name    TEXT        NOT NULL,
    content_type TEXT        NOT NULL,
    data         BYTEA       NOT NULL,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by   TEXT,
    updated_by   TEXT
);

CREATE INDEX idx_card_attachments_card_id ON card_attachments(card_id);
