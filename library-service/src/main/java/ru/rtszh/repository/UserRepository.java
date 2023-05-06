package ru.rtszh.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import ru.rtszh.domain.User;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    @Query("{username:'?0'}")
    Optional<User> findUserByUsername(String username);
}
