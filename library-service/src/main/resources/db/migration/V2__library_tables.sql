-- V2__library_tables.sql
-- Library schema: categories, authors, books, book_authors, book_copies, loans

CREATE TABLE IF NOT EXISTS categories (
    id          UUID PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at  TIMESTAMPTZ NULL
);

CREATE TABLE IF NOT EXISTS authors (
    id          UUID PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at  TIMESTAMPTZ NULL
);

CREATE TABLE IF NOT EXISTS books (
    id              UUID PRIMARY KEY,
    isbn            VARCHAR(32) UNIQUE,
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    category_id     UUID NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMPTZ NULL,
    CONSTRAINT fk_books_category
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
);

-- Join table: a book can have multiple authors, and an author can have multiple books
CREATE TABLE IF NOT EXISTS book_authors (
    book_id         UUID NOT NULL,
    author_id       UUID NOT NULL,
    author_order    SMALLINT NULL,
    PRIMARY KEY (book_id, author_id),
    CONSTRAINT fk_book_authors_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    CONSTRAINT fk_book_authors_author FOREIGN KEY (author_id) REFERENCES authors(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS book_copies (
    id          UUID PRIMARY KEY,
    book_id     UUID NOT NULL,
    copy_code   VARCHAR(255) UNIQUE,
    status      copy_status_enum NOT NULL DEFAULT 'AVAILABLE',
    location    VARCHAR(50) NOT NULL,
    CONSTRAINT chk_book_copies_location CHECK (location IN ('SHELF', 'BRANCH')),
    version     INTEGER NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at  TIMESTAMPTZ NULL,
    CONSTRAINT fk_book_copies_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS loans (
    id              UUID PRIMARY KEY,
    book_copy_id    UUID NOT NULL,
    user_id         UUID NOT NULL, -- external reference (from JWT sub), no FK
    borrowed_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    due_at          TIMESTAMPTZ NOT NULL,
    returned_at     TIMESTAMPTZ NULL,
    renewal_count   SMALLINT NOT NULL DEFAULT 0,
    CONSTRAINT chk_loans_renewal_count CHECK (renewal_count >= 0 AND renewal_count <= 2),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_loans_book_copy FOREIGN KEY (book_copy_id) REFERENCES book_copies(id) ON DELETE RESTRICT
);