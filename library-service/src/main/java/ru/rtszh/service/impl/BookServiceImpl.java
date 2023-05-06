package ru.rtszh.service.impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import ru.rtszh.domain.Author;
import ru.rtszh.domain.Book;
import ru.rtszh.domain.Comment;
import ru.rtszh.domain.Genre;
import ru.rtszh.dto.*;
import ru.rtszh.exceptions.BookNotFoundException;
import ru.rtszh.exceptions.IncorrectInputDataException;
import ru.rtszh.repository.BookRepository;
import ru.rtszh.service.BookService;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;


@Service
public class BookServiceImpl implements BookService {

    private static final String CIRCUIT_BREAKER_NAME = "bookService";
    private static final String FALLBACK_METHOD_ALL_BOOKS = "fallbackBooks";
    private static final String FALLBACK_METHOD_BOOK_BY_ID = "fallbackBookById";

    private final BookRepository bookRepository;
    private final ModelMapper mapper;

    public BookServiceImpl(BookRepository bookRepository, ModelMapper mapper) {
        this.bookRepository = bookRepository;
        this.mapper = mapper;
    }

    @Override
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = FALLBACK_METHOD_ALL_BOOKS)
    public List<BookDto> getAllBooks() {

        var books = bookRepository.findAll();

        return books.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = FALLBACK_METHOD_BOOK_BY_ID)
    public BookDto getBookById(String id) {

        var book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(
                                String.format("Book with id='%s' not found", id)
                        )
                );

        return toDto(book);
    }

    @Override
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME)
    public void deleteBookById(String id) {
        bookRepository.deleteById(id);
    }

    @Override
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME)
    public BookDto insertBook(BookDto bookDto) {

        checkBookTitleToInsert(bookDto.getTitle());

        List<Author> authors = bookDto.getAuthorsDto().stream()
                .map(authorDto -> mapper.map(authorDto, Author.class))
                .collect(Collectors.toList());

        List<Genre> genres = bookDto.getGenresDto().stream()
                .map(genreDto -> mapper.map(genreDto, Genre.class))
                .collect(Collectors.toList());

        var savedEntity = bookRepository.save(
                Book.builder()
                        .title(bookDto.getTitle())
                        .authors(authors)
                        .genres(genres)
                        .build()
        );

        return toDto(savedEntity);
    }

    @Override
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME)
    public BookDto updateBook(BookUpdateDto bookUpdateDto) {

        var currentBook = getBookIfExists(bookUpdateDto.getId());
        currentBook.setTitle(bookUpdateDto.getTitle());

        List<Author> authors = bookUpdateDto.getAuthorsDto().stream()
                .map(authorDto -> mapper.map(authorDto, Author.class))
                .collect(Collectors.toList());
        currentBook.setAuthors(authors);

        List<Genre> genres = bookUpdateDto.getGenresDto().stream()
                .map(genreDto -> mapper.map(genreDto, Genre.class))
                .collect(Collectors.toList());
        currentBook.setGenres(genres);

        if (nonNull(bookUpdateDto.getCommentsDto()) && !bookUpdateDto.getCommentsDto().isEmpty()) {
            List<Comment> comments = bookUpdateDto.getCommentsDto().stream()
                    .map(commentDto -> mapper.map(commentDto, Comment.class))
                    .collect(Collectors.toList());
            currentBook.setComments(comments);
        }

        var savedEntity = bookRepository.save(currentBook);

        return toDto(savedEntity);
    }

    private void checkBookTitleToInsert(String title) {
        Optional<Book> bookOptional = Optional.ofNullable(bookRepository.findByTitle(title));

        bookOptional.ifPresent(book -> {
                    throw new IncorrectInputDataException("Book with same name is already exists in database");
                }
        );
    }

    private Book getBookIfExists(String id) {
        Optional<Book> bookOptional = bookRepository.findById(id);

        if (bookOptional.isPresent()) {
            return bookOptional.get();
        } else {
            throw new IncorrectInputDataException(
                    String.format("Book with id '%s' doesn't exists in database", id)
            );
        }
    }

    private BookDto toDto(Book book) {
        BookDto bookDto = mapper.map(book, BookDto.class);

        if (Objects.nonNull(book.getAuthors())) {
            bookDto.setAuthorsDto(
                    mapper.map(book.getAuthors(), new TypeToken<List<AuthorDto>>() {
                    }.getType())
            );
        }

        if (Objects.nonNull(book.getGenres())) {
            bookDto.setGenresDto(
                    mapper.map(book.getGenres(), new TypeToken<List<GenreDto>>() {
                    }.getType())
            );
        }

        if (Objects.nonNull(book.getComments())) {
            bookDto.setCommentsDto(
                    mapper.map(book.getComments(), new TypeToken<List<CommentDto>>() {
                    }.getType())
            );
        } else {
            bookDto.setCommentsDto(new ArrayList<>());
        }

        return bookDto;
    }

    private List<BookDto> fallbackBooks(Exception ex) {
        return List.of(
                fallbackBookById(ex)
        );
    }

    private BookDto fallbackBookById(Exception ex) {
        return BookDto.builder()
                .id("-1")
                .title("N/A")
                .authorsDto(Collections.emptyList())
                .genresDto(Collections.emptyList())
                .commentsDto(Collections.emptyList())
                .build();
    }

}
