package ru.rtszh.exceptions;

public class CommentNotFoundException extends LibraryServiceException {
    public CommentNotFoundException(String message) {
        super(message);
    }
}
