package ru.rtszh.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.rtszh.dto.*;
import ru.rtszh.forms.BookForm;
import ru.rtszh.forms.CommentForm;
import ru.rtszh.repository.BookRepository;
import ru.rtszh.service.BookService;
import ru.rtszh.service.CommentService;

import java.util.ArrayList;
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
    private CommentService commentService;

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

    @Test
    void addBookForbidden() throws Exception {
        BookForm bookForm = BookForm.builder()
                .title("title")
                .authors("author1;author3")
                .genres("genre1")
                .build();
        BookDto book = createBookDtoData1();

        given(bookService.insertBook(any(BookDto.class))).willReturn(book);

        mvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookForm))
                        .with(csrf().useInvalidToken())
                )
                .andExpect(status().isForbidden());
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
    void deleteBookForbidden() throws Exception {
        mvc.perform(
                        delete("/api/v1/books/{id}", "1")
                                .with(csrf().useInvalidToken())
                )
                .andExpect(status().isForbidden());
    }

    @WithMockUser(
            username = "admin",
            authorities = {"ROLE_ADMIN"}
    )
    @Test
    void updateBook() throws Exception {
        BookForm bookForm = BookForm.builder()
                .title("title1")
                .authors("author")
                .genres("genre")
                .build();

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
                .commentsDto(new ArrayList<>())
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
    void updateBookForbidden() throws Exception {
        BookForm bookForm = BookForm.builder()
                .title("title1")
                .authors("author")
                .genres("genre")
                .build();

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
                .commentsDto(new ArrayList<>())
                .build();

        given(bookService.updateBook(any(BookUpdateDto.class))).willReturn(updatedBook);

        mvc.perform(put("/api/v1/books/{id}", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookForm))
                        .with(csrf().useInvalidToken())
                )
                .andExpect(status().isForbidden());
    }

    @WithMockUser(
            username = "admin",
            authorities = {"ROLE_ADMIN"}
    )
    @Test
    void getCommentsByBookId() throws Exception {
        CommentDto commentDto1 = CommentDto.builder()
                .content("comment content1")
                .bookId("1")
                .orderNumber(1)
                .build();

        CommentDto commentDto2 = CommentDto.builder()
                .content("comment content2")
                .bookId("1")
                .orderNumber(2)
                .build();

        CommentDto commentDto3 = CommentDto.builder()
                .content("comment content3")
                .bookId("1")
                .orderNumber(3)
                .build();

        List<CommentDto> commentDtoList = List.of(
                commentDto1, commentDto2, commentDto3
        );

        given(commentService.getBookComments("1")).willReturn(commentDtoList);

        mvc.perform(get("/api/v1/books/{id}/comments", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                        content().json(objectMapper.writeValueAsString(commentDtoList))
                );

    }

    @WithMockUser(
            username = "admin",
            authorities = {"ROLE_ADMIN"}
    )
    @Test
    void getCommentsByIdAndOrderNumber() throws Exception {
        CommentDto commentDto1 = CommentDto.builder()
                .content("comment content1")
                .bookId("1")
                .orderNumber(1)
                .build();

        given(commentService.getBookComment("1", 1)).willReturn(commentDto1);

        mvc.perform(get("/api/v1/books/{id}/comments/{order-number}", "1", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                        content().json(objectMapper.writeValueAsString(commentDto1))
                );

    }

    @WithMockUser(
            username = "admin",
            authorities = {"ROLE_ADMIN"}
    )
    @Test
    void addComment() throws Exception {

        CommentForm commentForm = CommentForm.builder()
                .content("comment content")
                .build();

        CommentDto expectedCommentDto = CommentDto.builder()
                .content("comment content")
                .orderNumber(1)
                .bookId("1")
                .build();

        given(commentService.addComment(any(CommentDto.class)))
                .willReturn(expectedCommentDto);

        mvc.perform(post("/api/v1/books/{id}/comments", "1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentForm))
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(
                        result -> {
                            var actualCommentString = result.getResponse().getContentAsString();

                            CommentDto actualCommentDto = objectMapper.readValue(actualCommentString, CommentDto.class);

                            Assertions.assertThat(expectedCommentDto).isEqualTo(actualCommentDto);
                        }
                );
    }

    @WithMockUser(
            username = "admin",
            authorities = {"ROLE_ADMIN"}
    )
    @Test
    void addCommentForbidden() throws Exception {

        CommentForm commentForm = CommentForm.builder()
                .content("comment content")
                .build();

        CommentDto expectedCommentDto = CommentDto.builder()
                .content("comment content")
                .orderNumber(1)
                .bookId("1")
                .build();

        given(commentService.addComment(any(CommentDto.class)))
                .willReturn(expectedCommentDto);

        mvc.perform(post("/api/v1/books/{id}/comments", "1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentForm))
                        .with(csrf().useInvalidToken())
                )
                .andExpect(status().isForbidden());
    }

    @WithMockUser(
            username = "admin",
            authorities = {"ROLE_ADMIN"}
    )
    @Test
    void deleteCommentForbidden() throws Exception {
        mvc.perform(
                        delete("/api/v1/books/{id}/comments/{order-number}", "1", 1)
                                .with(csrf().useInvalidToken())
                )
                .andExpect(status().isForbidden());
    }

    @WithMockUser(
            username = "admin",
            authorities = {"ROLE_ADMIN"}
    )
    @Test
    void deleteComment() throws Exception {
        mvc.perform(
                        delete("/api/v1/books/{id}/comments/{order-number}", "1", 1)
                                .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @WithMockUser(
            username = "admin",
            authorities = {"ROLE_ADMIN"}
    )
    @Test
    void updateComment() throws Exception {
        CommentForm commentForm = CommentForm.builder()
                .content("updatedComment")
                .build();

        CommentDto expectedCommentDto = CommentDto.builder()
                .content("updatedComment")
                .orderNumber(1)
                .bookId("1")
                .build();

        given(commentService.updateComment(any(CommentDto.class)))
                .willReturn(expectedCommentDto);


        mvc.perform(put("/api/v1/books/{id}/comments/{order-number}", "1", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentForm))
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(
                        result -> {
                            var actualCommentString = result.getResponse().getContentAsString();

                            CommentDto actualCommentDto = objectMapper.readValue(actualCommentString, CommentDto.class);

                            Assertions.assertThat(expectedCommentDto).isEqualTo(actualCommentDto);
                        }
                );
    }

    @WithMockUser(
            username = "admin",
            authorities = {"ROLE_ADMIN"}
    )
    @Test
    void updateCommentForbidden() throws Exception {
        CommentForm commentForm = CommentForm.builder()
                .content("updatedComment")
                .build();

        CommentDto expectedCommentDto = CommentDto.builder()
                .content("updatedComment")
                .orderNumber(1)
                .bookId("1")
                .build();

        given(commentService.updateComment(any(CommentDto.class)))
                .willReturn(expectedCommentDto);


        mvc.perform(put("/api/v1/books/{id}/comments/{order-number}", "1", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentForm))
                        .with(csrf().useInvalidToken())
                )
                .andExpect(status().isForbidden());
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
                .commentsDto(new ArrayList<>())
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
                .commentsDto(new ArrayList<>())
                .build();
    }
}