package ru.rtszh.libraryservice.exceptions;

public class BookNotFoundException extends LibraryServiceException {
    public BookNotFoundException(String message) {
        super(message);
    }
}
