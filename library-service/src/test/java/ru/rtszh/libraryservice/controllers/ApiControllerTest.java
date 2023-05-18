package ru.rtszh.libraryservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.rtszh.dto.*;
import ru.rtszh.libraryservice.dto.*;
import ru.rtszh.libraryservice.repository.BookRepository;
import ru.rtszh1.dto.*;
import ru.rtszh.libraryservice.repository.PageRepository;
import ru.rtszh.libraryservice.service.BookService;
import ru.rtszh.libraryservice.service.PageService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApiController.class)
class ApiControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookService bookService;
    @MockBean
    private BookRepository bookRepository;
    @MockBean
    private PageService pageService;
    @MockBean
    private PageRepository pageRepository;

    @WithMockUser(
            username = "admin",
            authorities = {"ROLE_ADMIN"}
    )
    @Test
    void getBooks() throws Exception {
        List<BookDto> books = List.of(
                createBookDtoData1(), createBookDtoData2()
        );

        given(bookService.getAllBooks()).willReturn(books);

        mvc.perform(get("/api/v1/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                        content().json(objectMapper.writeValueAsString(books))
                );
    }

    @WithMockUser(
            username = "admin",
            authorities = {"ROLE_ADMIN"}
    )
    @Test
    void getBookById() throws Exception {
        BookDto book = createBookDtoData1();

        given(bookService.getBookById("1")).willReturn(book);

        mvc.perform(get("/api/v1/books/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                        content().json(objectMapper.writeValueAsString(book))
                );
    }

    @WithMockUser(
            username = "admin",
            authorities = {"ROLE_ADMIN"}
    )
    @Test
    void addBook() throws Exception {
        BookDto expectedBookDto = createBookDtoData1();

        given(bookService.insertBook(any(BookDto.class))).willReturn(expectedBookDto);

        mvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedBookDto))
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(
                        content().json(objectMapper.writeValueAsString(expectedBookDto))
                );
    }

    @WithMockUser(
            username = "admin",
            authorities = {"ROLE_ADMIN"}
    )
    @Test
    void deleteBook() throws Exception {
        mvc.perform(
                        delete("/api/v1/books/{id}", "1")
                                .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @WithMockUser(
            username = "admin",
            authorities = {"ROLE_ADMIN"}
    )
    @Test
    void updateBook() throws Exception {
        BookDto updatedBook = BookDto.builder()
                .id("1")
                .title("title1")
                .authorsDto(
                        List.of(
                                AuthorDto.builder().name("author").build()
                        )
                )
                .genresDto(
                        List.of(
                                GenreDto.builder().name("genre").build()
                        )
                )
                .build();

        given(bookService.updateBook(any(BookUpdateDto.class))).willReturn(updatedBook);

        mvc.perform(put("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBook))
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(
                        content().json(objectMapper.writeValueAsString(updatedBook))
                );
    }

    @WithMockUser(
            username = "admin",
            authorities = {"ROLE_ADMIN"}
    )
    @Test
    void bookTextTest() throws Exception {
        List<PageDto> page = List.of(
                PageDto.builder()
                        .text("text1")
                        .chapter(1)
                        .pageNumber(1)
                        .build()
        );

        given(pageService.findPagesByBookId("1", 1, 1)).willReturn(page);

        mvc.perform(get("/api/v1/books/{id}/text", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                        content().json(objectMapper.writeValueAsString(page))
                );
    }

    private BookDto createBookDtoData1() {
        return BookDto.builder()
                .id("1")
                .title("title1")
                .authorsDto(List.of(
                                AuthorDto.builder().name("author1").build(),
                                AuthorDto.builder().name("author3").build()
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
                .id("2")
                .title("title2")
                .authorsDto(List.of(
                                AuthorDto.builder().name("author2").build()
                        )
                )
                .genresDto(List.of(
                                GenreDto.builder().name("genre2").build()
                        )
                )
                .build();
    }
}