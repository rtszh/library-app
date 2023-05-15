package ru.rtszh.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.rtszh.domain.User;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {
    List<User> findByUsernameAndBookId(String username, String bookId);
}
