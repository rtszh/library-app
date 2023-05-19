package ru.rtszh.libraryservice.service.impl;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.rtszh.libraryservice.domain.Author;
import ru.rtszh.libraryservice.domain.Book;
import ru.rtszh.libraryservice.domain.Genre;
import ru.rtszh.libraryservice.dto.AuthorDto;
import ru.rtszh.libraryservice.dto.BookDto;
import ru.rtszh.libraryservice.dto.BookUpdateDto;
import ru.rtszh.libraryservice.dto.GenreDto;
import ru.rtszh.libraryservice.repository.BookRepository;
import ru.rtszh.libraryservice.service.PageService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(
        classes = {BookServiceImpl.class, ModelMapper.class},
        properties = {"spring.cloud.config.enabled=false"}
)
class BookServiceImplTest {

    @Autowired
    private BookServiceImpl bookService;
    @Autowired
    private ModelMapper modelMapper;
    @MockBean
    private BookRepository bookRepository;
    @MockBean
    private PageService pageService;

    @Test
    void getAllBooks() {
        Book book1 = Book.builder().title("title1").authors(new ArrayList<>()).genres(new ArrayList<>()).build();
        Book book2 = Book.builder().title("title2").authors(new ArrayList<>()).genres(new ArrayList<>()).build();
        Book book3 = Book.builder().title("title3").authors(new ArrayList<>()).genres(new ArrayList<>()).build();

        BookDto bookDto1 = BookDto.builder().title("title1").authorsDto(new ArrayList<>()).genresDto(new ArrayList<>()).build();
        BookDto bookDto2 = BookDto.builder().title("title2").authorsDto(new ArrayList<>()).genresDto(new ArrayList<>()).build();
        BookDto bookDto3 = BookDto.builder().title("title3").authorsDto(new ArrayList<>()).genresDto(new ArrayList<>()).build();

        List<Book> books = List.of(book1, book2, book3);
        List<BookDto> expectedBooksDto = List.of(bookDto1, bookDto2, bookDto3);

        when(bookRepository.findAll())
                .thenReturn(books);

        var actualBooksDto = bookService.getAllBooks();

        assertThat(expectedBooksDto).hasSameElementsAs(actualBooksDto);
    }

    @Test
    void getBookById() {
        Book expectedBook = Book.builder().id("1").title("title1").authors(new ArrayList<>()).genres(new ArrayList<>()).build();

        when(bookRepository.findById(eq("1")))
                .thenReturn(Optional.of(expectedBook));

        var actualBook = bookService.getBookById("1");

        assertThat(expectedBook.getTitle()).isEqualTo(actualBook.getTitle());
    }


    @Test
    void insertBook() {
        BookDto inputBookDto = createBookDtoData1();
        BookDto expectedBookDto = createBookDtoData1();
        Book book = createBookData();

        when(bookRepository.save(any()))
                .thenReturn(book);

        bookService.insertBook(inputBookDto);

        verify(bookRepository, times(1)).save(any());

        var captor = ArgumentCaptor.forClass(Book.class);

        verify(bookRepository).save(captor.capture());

        var actualBook = captor.getValue();

        var actualBookDto = BookDto.builder()
                .id(actualBook.getId())
                .title(actualBook.getTitle())
                .authorsDto(
                        actualBook.getAuthors().stream()
                                .map(author -> AuthorDto.builder()
                                        .name(author.getName())
                                        .build())
                                .collect(Collectors.toList())
                )
                .genresDto(
                        actualBook.getGenres().stream()
                                .map(genre -> GenreDto.builder()
                                        .name(genre.getName())
                                        .build())
                                .collect(Collectors.toList())
                )
                .build();

        assertThat(actualBookDto).isEqualTo(expectedBookDto);
    }

    @Test
    void updateBook() {
        Book book = createBookData();
        BookDto expectedBookDto = createBookDtoData2();

        BookUpdateDto inputBookUpdateDto = createBookUpdateDtoData();


        when(bookRepository.findById(book.getId()))
                .thenReturn(Optional.of(book));

        when(bookRepository.save(any()))
                .thenReturn(book);

        bookService.updateBook(inputBookUpdateDto);

        verify(bookRepository, times(1)).save(any());

        var captor = ArgumentCaptor.forClass(Book.class);

        verify(bookRepository).save(captor.capture());

        var actualBook = captor.getValue();

        assertThat(actualBook.getTitle()).isEqualTo(expectedBookDto.getTitle());

        var actualGenres = actualBook.getGenres().stream()
                .map(genre -> GenreDto.builder()
                        .name(genre.getName())
                        .build())
                .collect(Collectors.toList());

        assertThat(actualGenres).hasSameElementsAs(expectedBookDto.getGenresDto());


        var actualAuthors = actualBook.getAuthors().stream()
                .map(author -> AuthorDto.builder()
                        .name(author.getName())
                        .build()
                )
                .collect(Collectors.toList());

        assertThat(actualAuthors).hasSameElementsAs(expectedBookDto.getAuthorsDto());

    }

    private BookDto createBookDtoData1() {
        return BookDto.builder()
                .title("title1")
                .authorsDto(List.of(
                                AuthorDto.builder().name("author1").build()
                        )
                )
                .genresDto(List.of(
                                GenreDto.builder().name("genre1").build()
                        )
                )
                .build();
    }

    private BookDto createBookDtoData2() {
        return BookDto.builder()
                .title("updatedTitle")
                .authorsDto(List.of(
                                AuthorDto.builder().name("updatedAuthor").build()
                        )
                )
                .genresDto(List.of(
                                GenreDto.builder().name("updatedGenre").build()
                        )
                )
                .build();
    }

    private Book createBookData() {
        return Book.builder()
                .title("title1")
                .authors(List.of(
                                Author.builder().name("author1").build()
                        )
                )
                .genres(List.of(
                                Genre.builder().name("genre1").build()
                        )
                )
                .build();
    }

    private BookUpdateDto createBookUpdateDtoData() {
        return BookUpdateDto.builder()
                .title("updatedTitle")
                .authorsDto(List.of(
                                AuthorDto.builder().name("updatedAuthor").build()
                        )
                )
                .genresDto(List.of(
                                GenreDto.builder().name("updatedGenre").build()
                        )
                )
                .build();
    }
}