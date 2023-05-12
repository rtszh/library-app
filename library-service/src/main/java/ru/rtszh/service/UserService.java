package ru.rtszh.service;

import java.util.Optional;

public interface UserService {
    int getPageNumber(Optional<String> usernameOptional, String bookId, int page);
}
