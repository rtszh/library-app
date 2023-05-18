package ru.rtszh.libraryservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.rtszh.libraryservice.domain.User;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {
    List<User> findByUsernameAndBookId(String username, String bookId);
}
