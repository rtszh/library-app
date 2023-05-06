package ru.rtszh.mongock.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.rtszh.domain.User;
import ru.rtszh.repository.UserRepository;

import java.util.Set;

@ChangeLog
public class DatabaseChangelog {

    @ChangeSet(order = "001", id = "dropCollection", author = "developer", runAlways = true)
    public void createTestCollection(MongoDatabase db) {
        db.getCollection("user").drop();
    }

    @ChangeSet(order = "002", id = "insertUsers", author = "developer", runAlways = true)
    public void insertUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {

        String encodedPassword = passwordEncoder.encode(
                "password"
        );

        var user = new User(
                null, "user", encodedPassword, Set.of("USER")
        );

        var admin = new User(
                null, "admin", encodedPassword, Set.of("ADMIN")
        );

        userRepository.save(user);
        userRepository.save(admin);
    }

}
