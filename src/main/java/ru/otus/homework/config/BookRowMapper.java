package ru.otus.homework.config;

import org.springframework.jdbc.core.RowMapper;
import ru.otus.homework.model.Author;
import ru.otus.homework.model.Book;
import ru.otus.homework.model.Genre;
import ru.otus.homework.model.Post;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BookRowMapper implements RowMapper<Book> {

    public static final String SELECT_SQL = "select b.id, b.full_name, b.book_description, upper(g.genre) genre,"
            + " string_agg(authors.id::char || \':\' || authors.full_name, \';\') authors,"
            + " string_agg(p.description, \';\') posts"
            + " from library.books b join library.book_genres g on b.genre_id = g.id"
            + " left join (select ba.book_id, a.id, a.full_name, a.description "
            + " from library.book_authors ba join library.authors a on ba.author_id = a.id) authors on b.id = authors.book_id"
            + " left join library.posts p on b.id = p.book_id"
            + " group by b.id, b.full_name, b.book_description, g.id, g.genre";

    private static final String BOOK_ID_COLUMN = "id";
    private static final String BOOK_NAME_COLUMN = "full_name";
    private static final String BOOK_DESCRIPTION_COLUMN = "book_description";
    private static final String GENRE_COLUMN = "genre";
    private static final String AUTHORS_COLUMN = "authors";
    private static final String POSTS_COLUMN = "posts";

    @Override
    public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
        Book book = new Book();
        book.setId(rs.getString(BOOK_ID_COLUMN));
        book.setFullName(rs.getString(BOOK_NAME_COLUMN));
        book.setDescription(rs.getString(BOOK_DESCRIPTION_COLUMN));
        book.setGenre(Genre.valueOf(rs.getString(GENRE_COLUMN)));

        if (rs.getString(AUTHORS_COLUMN) != null) {
            List<Author> authors = Arrays.stream(rs.getString(AUTHORS_COLUMN).split(";"))
                    .map(s -> s.split(":"))
                    .map(s -> new Author(s[0], s[1], null))
                    .collect(Collectors.toList());
            book.setAuthors(authors);
        }

        if (rs.getString(POSTS_COLUMN) != null) {
            List<Post> posts = Arrays.stream(rs.getString(POSTS_COLUMN).split(";"))
                    .map(Post::new)
                    .collect(Collectors.toList());
            book.setPosts(posts);
        }

        return book;
    }
}
