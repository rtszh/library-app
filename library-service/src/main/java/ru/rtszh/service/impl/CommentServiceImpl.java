package ru.rtszh.service.impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.apache.commons.collections4.ListUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.rtszh.dto.BookDto;
import ru.rtszh.dto.BookUpdateDto;
import ru.rtszh.dto.CommentDto;
import ru.rtszh.exceptions.CommentNotFoundException;
import ru.rtszh.service.BookService;
import ru.rtszh.service.CommentService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private static final String CIRCUIT_BREAKER_NAME = "commentService";
    private static final String FALLBACK_METHOD_COMMENTS = "fallbackBookComments";
    private static final String FALLBACK_METHOD_COMMENT = "fallbackBookComment";

    private final BookService bookService;

    private final ModelMapper mapper;

    public CommentServiceImpl(BookService bookService, ModelMapper mapper) {
        this.bookService = bookService;
        this.mapper = mapper;
    }

    @Override
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME)
    public CommentDto addComment(CommentDto commentDto) {

        var currentBook = bookService.getBookById(commentDto.getBookId());

        var commentsOfCurrentBook = currentBook.getCommentsDto();

        var commentDtoWithOrderNumber = mapper.map(commentDto, CommentDto.class);

        int commentOrderNumber = commentsOfCurrentBook.size() + 1;

        commentDtoWithOrderNumber.setOrderNumber(commentOrderNumber);

        commentsOfCurrentBook.add(commentDtoWithOrderNumber);

        var bookUpdateDto = mapper.map(currentBook, BookUpdateDto.class);
        bookUpdateDto.setCommentsDto(commentsOfCurrentBook);

        bookService.updateBook(bookUpdateDto);

        return CommentDto.builder()
                .content(commentDto.getContent())
                .time(commentDto.getTime())
                .orderNumber(commentOrderNumber)
                .bookId(commentDto.getBookId())
                .build();
    }

    @Override
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME)
    public CommentDto updateComment(CommentDto updatedCommentDto) {

        var currentBook = bookService.getBookById(updatedCommentDto.getBookId());

        List<CommentDto> commentsOfCurrentBook = getCommentsOfCurrentBookWithoutUpdated(currentBook, updatedCommentDto.getOrderNumber());

        commentsOfCurrentBook.add(
                mapper.map(updatedCommentDto, CommentDto.class)
        );

        var bookUpdateDto = mapper.map(currentBook, BookUpdateDto.class);
        bookUpdateDto.setCommentsDto(commentsOfCurrentBook);

        bookService.updateBook(bookUpdateDto);

        return updatedCommentDto;
    }

    @Override
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = FALLBACK_METHOD_COMMENTS)
    public List<CommentDto> getBookComments(String bookId) {
        var currentBook = bookService.getBookById(bookId);

        var commentsForCurrentBook = currentBook.getCommentsDto();

        return commentsForCurrentBook.stream()
                .sorted(Comparator.comparing(CommentDto::getTime).reversed())
                .collect(Collectors.toList());
    }

    @Override
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = FALLBACK_METHOD_COMMENT)
    public CommentDto getBookComment(String bookId, int orderNumber) {
        var currentBook = bookService.getBookById(bookId);

        var commentsForCurrentBook = currentBook.getCommentsDto();

        return commentsForCurrentBook.stream()
                .filter(commentDto -> commentDto.getOrderNumber() == orderNumber)
                .findFirst()
                .orElseThrow(() -> new CommentNotFoundException(
                                String.format("Cannot find comment with order number = %d in book with id - %s",
                                        orderNumber, bookId
                                )
                        )
                );
    }

    @Override
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME)
    public void deleteComment(String bookId, int commentOrderNumberToDelete) {
        var currentBook = bookService.getBookById(bookId);

        List<CommentDto> commentsForCurrentBookWithoutDeleted = currentBook.getCommentsDto().stream()
                .filter(comment -> comment.getOrderNumber() != commentOrderNumberToDelete)
                .collect(Collectors.toList());

        var commentsAfterRenumber = renumberComments(
                commentsForCurrentBookWithoutDeleted,
                commentOrderNumberToDelete
        );

        var bookUpdateDto = mapper.map(currentBook, BookUpdateDto.class);
        bookUpdateDto.setCommentsDto(commentsAfterRenumber);

        bookService.updateBook(bookUpdateDto);
    }

    private List<CommentDto> renumberComments(List<CommentDto> commentsForCurrentBook, int orderNumberOfDeletedComment) {

        var commentsUnderDeletedComment = commentsForCurrentBook.stream()
                .filter(comment -> comment.getOrderNumber() < orderNumberOfDeletedComment)
                .collect(Collectors.toList());

        var commentsOverDeletedComment = commentsForCurrentBook.stream()
                .filter(comment -> comment.getOrderNumber() > orderNumberOfDeletedComment)
                .map(commentDto -> {
                    var commentOverDeleted = mapper.map(commentDto, CommentDto.class);
                    commentOverDeleted.setOrderNumber(
                            commentOverDeleted.getOrderNumber() - 1
                    );

                    return commentOverDeleted;
                })
                .collect(Collectors.toList());

        return ListUtils.union(commentsUnderDeletedComment, commentsOverDeletedComment);
    }

    private List<CommentDto> getCommentsOfCurrentBookWithoutUpdated(BookDto bookDto, int orderNumber) {
        return bookDto.getCommentsDto().stream()
                .filter(commentDto -> commentDto.getOrderNumber() != orderNumber)
                .collect(Collectors.toList());
    }

    private List<CommentDto> fallbackBookComments(Exception ex) {
        return List.of(
                fallbackBookComment(ex)
        );
    }

    private CommentDto fallbackBookComment(Exception ex) {
        return CommentDto.builder()
                .bookId("N/A")
                .time(LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(0),
                        ZoneId.systemDefault()
                ))
                .content("N/A")
                .orderNumber(-1)
                .build();
    }

}
