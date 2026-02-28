-- V1__library_types.sql
-- Library schema: enum types

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'copy_status_enum') THEN
CREATE TYPE copy_status_enum AS ENUM ('available', 'borrowed', 'lost', 'damaged');
END IF;
END$$;