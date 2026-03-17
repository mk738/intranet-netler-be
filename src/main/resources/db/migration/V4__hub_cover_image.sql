-- V4__hub_cover_image.sql
-- Add cover image support to news_posts (stored as base64 TEXT).

ALTER TABLE news_posts
    ADD COLUMN cover_image_data TEXT,
    ADD COLUMN cover_image_type VARCHAR(50);
