package ru.rtszh.batch.writers.batch_book;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import ru.rtszh.config.BookProperties;
import ru.rtszh.domain.Author;
import ru.rtszh.domain.Book;
import ru.rtszh.domain.Genre;
import ru.rtszh.repository.BookRepository;

import java.util.stream.Collectors;

public class BookWriter implements Tasklet {

    private final BookProperties properties;
    private final BookRepository bookRepository;

    public BookWriter(BookProperties properties, BookRepository bookRepository) {
        this.properties = properties;
        this.bookRepository = bookRepository;
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {

        Book book = Book.builder()
                .title(properties.getTitle())
                .authors(
                        properties.getAuthors().stream()
                                .map(s -> Author.builder().name(s).build())
                                .collect(Collectors.toList())
                )
                .genres(
                        properties.getGenres().stream()
                                .map(s -> Genre.builder().name(s).build())
                                .collect(Collectors.toList())
                )
                .build();

        bookRepository.save(book);

        return RepeatStatus.FINISHED;
    }

}
