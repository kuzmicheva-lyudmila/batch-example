package ru.otus.homework.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;
import ru.otus.homework.model.Author;
import ru.otus.homework.model.Book;
import ru.otus.homework.repository.AuthorRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BookMongoEventListener extends AbstractMongoEventListener<Book> {

    private final AuthorRepository authorRepository;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Book> event) {
        super.onBeforeConvert(event);
        Book book = event.getSource();

        List<Author> authorList = new ArrayList<>();
        if (book.getAuthors() != null) {
            book.getAuthors().forEach(
                    author -> {
                        Optional<Author> authorFromRepository = authorRepository.findFirstByFullName(author.getFullName());
                        if (authorFromRepository.isPresent()) {
                            authorList.add(authorFromRepository.get());
                        } else {
                            authorList.add(authorRepository.save(author));
                        }
                    }
            );
            book.setAuthors(authorList);
        }
    }
}
