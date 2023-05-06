package ru.rtszh.service.impl;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.rtszh.dto.BookDto;
import ru.rtszh.dto.BookUpdateDto;
import ru.rtszh.dto.CommentDto;
import ru.rtszh.service.BookService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {CommentServiceImpl.class, ModelMapper.class})
class CommentServiceImplTest {

    @Autowired
    private CommentServiceImpl commentService;
    @MockBean
    private BookService bookService;

    @Test
    void addComment() {

        String bookId = "1";
        String bookTitle = "title1";
        List<CommentDto> comments = new ArrayList<>();
        comments.add(CommentDto.builder().content("comment1").orderNumber(1).bookId("1").build());
        comments.add(CommentDto.builder().content("comment2").orderNumber(2).bookId("1").build());

        var bookDto = BookDto.builder()
                .id(bookId)
                .title(bookTitle)
                .commentsDto(comments)
                .build();

        var commentDto1 = CommentDto.builder().content("comment3").orderNumber(3).bookId("1").build();
        var commentDto2 = CommentDto.builder().content("comment4").orderNumber(4).bookId("1").build();

        var expectedComment1 = CommentDto.builder().content("comment3").orderNumber(3).bookId("1").build();
        var expectedComment2 = CommentDto.builder().content("comment4").orderNumber(4).bookId("1").build();

        when(bookService.getBookById(bookId))
                .thenReturn(bookDto);

        // add first comment
        commentService.addComment(commentDto1);

        var captor = ArgumentCaptor.forClass(BookUpdateDto.class);

        verify(bookService).updateBook(captor.capture());

        var bookUpdateDto = captor.getValue();

        var actualComments = bookUpdateDto.getCommentsDto();

        assertThat(actualComments).contains(expectedComment1);
        assertThat(actualComments.size()).isEqualTo(3);

        // add second comment
        commentService.addComment(commentDto2);

        captor = ArgumentCaptor.forClass(BookUpdateDto.class);

        verify(bookService, times(2)).updateBook(captor.capture());

        actualComments = bookUpdateDto.getCommentsDto();

        assertThat(actualComments).contains(expectedComment2);
        assertThat(actualComments.size()).isEqualTo(4);
    }

    @Test
    void updateComment() {
        String bookTitle = "title1";
        List<CommentDto> comments = new ArrayList<>();
        comments.add(CommentDto.builder().content("comment1").orderNumber(1).bookId("1").build());
        comments.add(CommentDto.builder().content("comment2").orderNumber(2).bookId("1").build());
        comments.add(CommentDto.builder().content("comment3").orderNumber(3).bookId("1").build());

        BookDto bookDto = BookDto.builder()
                .title(bookTitle)
                .commentsDto(comments)
                .build();

        var expectedComment = CommentDto.builder().content("updated comment").orderNumber(2).bookId("1").build();

        var commentDto = CommentDto.builder()
                .content("updated comment")
                .time(LocalDateTime.now())
                .orderNumber(2)
                .bookId("1")
                .build();

        when(bookService.getBookById("1"))
                .thenReturn(bookDto);

        commentService.updateComment(commentDto);

        var captor = ArgumentCaptor.forClass(BookUpdateDto.class);

        verify(bookService).updateBook(captor.capture());

        var bookUpdateDto = captor.getValue();
        var actualComments = bookUpdateDto.getCommentsDto();

        var actualComment = actualComments.stream()
                .filter(commentDto1 -> commentDto1.getOrderNumber() == 2)
                .findFirst()
                .get();

        assertThat(actualComment.getContent()).isEqualTo(expectedComment.getContent());
        assertThat(actualComment.getOrderNumber()).isEqualTo(expectedComment.getOrderNumber());
    }

    @Test
    void deleteComment() {
        String bookId = "1";
        String bookTitle = "title1";
        List<CommentDto> comments = new ArrayList<>();
        comments.add(CommentDto.builder().content("comment1").orderNumber(1).bookId("1").build());
        comments.add(CommentDto.builder().content("comment2").orderNumber(2).bookId("1").build());
        comments.add(CommentDto.builder().content("comment3").orderNumber(3).bookId("1").build());
        comments.add(CommentDto.builder().content("comment4").orderNumber(4).bookId("1").build());

        BookDto book = BookDto.builder()
                .title(bookTitle)
                .commentsDto(comments)
                .build();

        when(bookService.getBookById(bookId))
                .thenReturn(book);

        commentService.deleteComment(bookId, 2);

        var captor = ArgumentCaptor.forClass(BookUpdateDto.class);

        verify(bookService).updateBook(captor.capture());

        var bookUpdateDto = captor.getValue();
        var commentsAsList = bookUpdateDto.getCommentsDto();

        assertThat(commentsAsList.get(0).getContent()).isEqualTo("comment1");
        assertThat(commentsAsList.get(0).getOrderNumber()).isEqualTo(1);

        assertThat(commentsAsList.get(1).getContent()).isEqualTo("comment3");
        assertThat(commentsAsList.get(1).getOrderNumber()).isEqualTo(2);

        assertThat(commentsAsList.get(2).getContent()).isEqualTo("comment4");
        assertThat(commentsAsList.get(2).getOrderNumber()).isEqualTo(3);
    }
}