package ru.rtszh.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.rtszh.dto.BookDto;
import ru.rtszh.dto.BookUpdateDto;
import ru.rtszh.dto.PageDto;
import ru.rtszh.service.BookService;
import ru.rtszh.service.PageService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ApiController {

    private final BookService bookService;
    private final PageService pageService;

    public ApiController(BookService bookService, PageService pageService) {
        this.bookService = bookService;
        this.pageService = pageService;
    }

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

    @GetMapping("/books/{id}/text")
    public List<PageDto> getBookText(
            @PathVariable String id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "1") int size
    ) {

        return pageService.findPagesByBookId(id, page, size);
    }
}
