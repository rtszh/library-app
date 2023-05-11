package ru.rtszh.exceptions;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(String text) {
        super(text);
    }
}
