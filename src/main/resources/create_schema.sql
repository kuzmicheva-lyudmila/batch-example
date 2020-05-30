CREATE SCHEMA IF NOT EXISTS library;

create table IF NOT EXISTS library.authors (
    id serial Primary Key,
    full_name varchar(240) not null,
    description varchar(2000)
);

create table IF NOT EXISTS library.book_genres (
    id serial primary key,
    genre varchar(240) unique not null
);

create table IF NOT EXISTS library.books (
    id serial primary key,
    title varchar(240) not null,
    genre_id integer not null references library.book_genres(id),
    description varchar(2000)
);

create table IF NOT EXISTS library.book_authors (
    id serial primary key,
    book_id integer not null references library.books(id),
    author_id integer not null references library.authors(id)
);

insert into library.book_genres (genre) values
    ('romance'),
    ('history'),
    ('horror'),
    ('children''s books')
ON CONFLICT DO NOTHING;

INSERT INTO library.authors (id, full_name, description) VALUES (1, 'author1', null);
INSERT INTO library.authors (id, full_name, description) VALUES (2, 'ert', '');
INSERT INTO library.authors (id, full_name, description) VALUES (3, 'sdfsdf', '');
INSERT INTO library.authors (id, full_name, description) VALUES (5, 'rwer', '');
INSERT INTO library.authors (id, full_name, description) VALUES (6, 'author1, author2', '');
INSERT INTO library.authors (id, full_name, description) VALUES (7, 'author9', '');

INSERT INTO library.books (id, full_name, genre_id, book_description) VALUES (1, 'the book', 2, 'text');
INSERT INTO library.books (id, full_name, genre_id, book_description) VALUES (3, 'The new book', 2, 'sdfsdf');
INSERT INTO library.books (id, full_name, genre_id, book_description) VALUES (4, 'dfgdfg', null, 'fgd');
INSERT INTO library.books (id, full_name, genre_id, book_description) VALUES (5, 'sdfsf', null, 'sdf');
INSERT INTO library.books (id, full_name, genre_id, book_description) VALUES (6, 'werew', 2, 'wer');
INSERT INTO library.books (id, full_name, genre_id, book_description) VALUES (7, 'book with public', 2, 'dsasdad');
INSERT INTO library.books (id, full_name, genre_id, book_description) VALUES (8, 'new book 9', 2, '');

INSERT INTO library.posts (id, book_id, description) VALUES (18, 8, 'sdfsdf');

INSERT INTO library.book_authors (book_id, author_id) VALUES (1, 1);
INSERT INTO library.book_authors (book_id, author_id) VALUES (4, 2);
INSERT INTO library.book_authors (book_id, author_id) VALUES (5, 3);
INSERT INTO library.book_authors (book_id, author_id) VALUES (6, 5);
INSERT INTO library.book_authors (book_id, author_id) VALUES (7, 1);
INSERT INTO library.book_authors (book_id, author_id) VALUES (8, 7);
INSERT INTO library.book_authors (book_id, author_id) VALUES (1, 2);
