package ru.rtszh.controllers;

import org.springframework.web.bind.annotation.*;
import ru.rtszh.dto.BookDto;
import ru.rtszh.dto.BookUpdateDto;
import ru.rtszh.dto.CommentDto;
import ru.rtszh.forms.CommentForm;
import ru.rtszh.service.BookService;
import ru.rtszh.service.CommentService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class ApiController {

    private final BookService bookService;
    private final CommentService commentService;


    public ApiController(BookService bookService, CommentService commentService) {
        this.bookService = bookService;
        this.commentService = commentService;
    }

    @GetMapping("/api/v1/books")
    public List<BookDto> getBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/api/v1/books/{id}")
    public BookDto getBookById(@PathVariable String id) {
        return bookService.getBookById(id);
    }

    @PostMapping("/api/v1/books")
    public BookDto addBook(@RequestBody BookDto bookDto) {
        return bookService.insertBook(bookDto);
    }

    @DeleteMapping("/api/v1/books/{id}")
    public void deleteBook(@PathVariable String id) {
        bookService.deleteBookById(id);
    }

    @PutMapping("/api/v1/books")
    public BookDto updateBook(@RequestBody BookUpdateDto bookUpdateDto) {
        return bookService.updateBook(bookUpdateDto);
    }

    @GetMapping("/api/v1/books/{id}/comments")
    public List<CommentDto> getCommentsByBookId(@PathVariable String id) {
        return commentService.getBookComments(id);
    }

    @GetMapping("/api/v1/books/{id}/comments/{order-number}")
    public CommentDto getCommentsByIdAndOrderNumber(@PathVariable String id,
                                                    @PathVariable("order-number") int orderNumber) {
        return commentService.getBookComment(id, orderNumber);
    }

    @PostMapping("/api/v1/books/{id}/comments")
    public CommentDto addComment(@PathVariable String id, @RequestBody CommentForm commentForm) {
        return commentService.addComment(
                CommentDto.builder()
                        .content(commentForm.getContent())
                        .time(LocalDateTime.now())
                        .bookId(id)
                        .orderNumber(0)
                        .build()
        );
    }

    @DeleteMapping("/api/v1/books/{id}/comments/{order-number}")
    public void deleteComment(@PathVariable("id") String bookId,
                              @PathVariable("order-number") int orderNumber) {
        commentService.deleteComment(bookId, orderNumber);
    }

    @PutMapping("/api/v1/books/{id}/comments/{order-number}")
    public CommentDto updateComment(@PathVariable String id, @PathVariable("order-number") int orderNumber,
                                    @RequestBody CommentForm commentForm) {
        return commentService.updateComment(
                CommentDto.builder()
                        .content(commentForm.getContent())
                        .time(LocalDateTime.now())
                        .bookId(id)
                        .orderNumber(orderNumber)
                        .build()
        );
    }
}
