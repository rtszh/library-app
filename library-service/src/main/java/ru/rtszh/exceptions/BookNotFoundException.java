package ru.rtszh.exceptions;

public class BookNotFoundException extends LibraryServiceException {
    public BookNotFoundException(String message) {
        super(message);
    }
}
