-- V3__library_indexes.sql
-- Library schema: indexes optimized for MVP queries

-- Books
CREATE INDEX IF NOT EXISTS idx_books_title ON books(title);
CREATE INDEX IF NOT EXISTS idx_books_category_id ON books(category_id);
CREATE INDEX IF NOT EXISTS idx_books_active ON books(deleted_at) WHERE deleted_at IS NULL;

-- Authors (optional but useful)
CREATE INDEX IF NOT EXISTS idx_authors_name ON authors(name);
CREATE INDEX IF NOT EXISTS idx_authors_active ON authors(deleted_at) WHERE deleted_at IS NULL;

-- Book authors
CREATE INDEX IF NOT EXISTS idx_book_authors_author_id ON book_authors(author_id);

-- Copies
CREATE INDEX IF NOT EXISTS idx_book_copies_book_id ON book_copies(book_id);
CREATE INDEX IF NOT EXISTS idx_book_copies_status ON book_copies(status);
CREATE INDEX IF NOT EXISTS idx_book_copies_book_id_status ON book_copies(book_id, status);
CREATE INDEX IF NOT EXISTS idx_book_copies_active ON book_copies(deleted_at) WHERE deleted_at IS NULL;

-- Loans
CREATE INDEX IF NOT EXISTS idx_loans_book_copy_id ON loans(book_copy_id);
CREATE INDEX IF NOT EXISTS idx_loans_user_id ON loans(user_id);
CREATE INDEX IF NOT EXISTS idx_loans_user_id_returned_at ON loans(user_id, returned_at);

-- Fast lookup: active loans per user (for max 5 rule)
CREATE INDEX IF NOT EXISTS idx_loans_user_active ON loans(user_id) WHERE returned_at IS NULL;

-- Enforce: only 1 active loan per copy (partial unique index)
CREATE UNIQUE INDEX IF NOT EXISTS uq_loans_active_copy ON loans(book_copy_id) WHERE returned_at IS NULL;