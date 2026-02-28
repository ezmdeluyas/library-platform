-- 01-create-dbs.sql
SELECT 'CREATE DATABASE authdb'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'authdb')\gexec;

SELECT 'CREATE DATABASE librarydb'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'librarydb')\gexec;