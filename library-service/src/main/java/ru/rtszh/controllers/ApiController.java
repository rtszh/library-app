package ru.rtszh.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.rtszh.dto.BookDto;
import ru.rtszh.dto.BookUpdateDto;
import ru.rtszh.dto.CommentDto;
import ru.rtszh.dto.PageDto;
import ru.rtszh.forms.CommentForm;
import ru.rtszh.service.BookService;
import ru.rtszh.service.PageService;
import ru.rtszh.service.CommentService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ApiController {

    private final BookService bookService;
    private final CommentService commentService;
    private final PageService pageService;

    public ApiController(BookService bookService, CommentService commentService,
                         PageService pageService) {
        this.bookService = bookService;
        this.commentService = commentService;
        this.pageService = pageService;
    }

    // books

    @GetMapping("/books")
    public List<BookDto> getBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/books/{id}")
    public BookDto getBookById(@PathVariable String id) {
        return bookService.getBookById(id);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/books")
    public BookDto addBook(@RequestBody BookDto bookDto) {
        return bookService.insertBook(bookDto);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/books/{id}")
    public void deleteBook(@PathVariable String id) {
        bookService.deleteBookById(id);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/books")
    public BookDto updateBook(@RequestBody BookUpdateDto bookUpdateDto) {
        return bookService.updateBook(bookUpdateDto);
    }

    // comments

    @GetMapping("/books/{id}/comments")
    public List<CommentDto> getCommentsByBookId(@PathVariable String id) {
        return commentService.getBookComments(id);
    }

    @GetMapping("/books/{id}/comments/{order-number}")
    public CommentDto getCommentsByIdAndOrderNumber(@PathVariable String id,
                                                    @PathVariable("order-number") int orderNumber) {
        return commentService.getBookComment(id, orderNumber);
    }

    @PostMapping("/books/{id}/comments")
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

    @DeleteMapping("/books/{id}/comments/{order-number}")
    public void deleteComment(@PathVariable("id") String bookId,
                              @PathVariable("order-number") int orderNumber) {
        commentService.deleteComment(bookId, orderNumber);
    }

    @PutMapping("/books/{id}/comments/{order-number}")
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

    @GetMapping("/books/{id}/text")
    public List<PageDto> getBookText(
            @PathVariable String id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "1") int size
    ) {

        return pageService.findPagesByBookId(id, page, size);
    }
}
