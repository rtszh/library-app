package ru.rtszh.libraryservice.service;

import java.util.Optional;

public interface UserService {
    int getPageNumber(Optional<String> usernameOptional, String bookId, int page);
}
