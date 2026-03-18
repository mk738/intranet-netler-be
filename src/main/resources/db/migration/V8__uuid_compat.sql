-- V8__uuid_compat.sql
-- Railway's managed PostgreSQL may not support the uuid-ossp extension via the app user.
-- All tables created in V1–V7 use DEFAULT uuid_generate_v4().
-- This migration ensures that function always exists by defining it as a wrapper
-- around gen_random_uuid() which is built into PostgreSQL 13+ with no extension needed.

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE OR REPLACE FUNCTION public.uuid_generate_v4()
    RETURNS UUID
    LANGUAGE SQL
    VOLATILE
AS $$ SELECT gen_random_uuid() $$;
