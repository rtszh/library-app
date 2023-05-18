package ru.rtszh.libraryservice.service;

import ru.rtszh.libraryservice.dto.BookDto;
import ru.rtszh.libraryservice.dto.BookUpdateDto;

import java.util.List;


public interface BookService {
    List<BookDto> getAllBooks();

    BookDto getBookById(String id);

    void deleteBookById(String id);

    BookDto insertBook(BookDto bookDto);

    BookDto updateBook(BookUpdateDto bookUpdateDto);
}
