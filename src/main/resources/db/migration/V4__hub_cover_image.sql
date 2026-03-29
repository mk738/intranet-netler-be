-- V4__hub_cover_image.sql
-- Add cover image support to news_posts (stored as Firebase Storage path).

ALTER TABLE news_posts
    ADD COLUMN cover_image_path TEXT;
