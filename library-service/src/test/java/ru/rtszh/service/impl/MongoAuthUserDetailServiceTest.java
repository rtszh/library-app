package ru.rtszh.service.impl;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.rtszh.domain.User;
import ru.rtszh.repository.UserRepository;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = MongoAuthUserDetailService.class)
class MongoAuthUserDetailServiceTest {

    @Autowired
    private MongoAuthUserDetailService mongoAuthUserDetailService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void loadUserByUsername1() {
        Optional<User> foundedUserOptional = Optional.of(
                new User(new ObjectId(), "user", "password", Set.of("ROLE_USER"))
        );

        when(userRepository.findUserByUsername(anyString()))
                .thenReturn(foundedUserOptional);

        var userDetails = mongoAuthUserDetailService.loadUserByUsername("user");

        assertThat(userDetails).isNotNull();
    }

    @Test
    void loadUserByUsername2() {
        Optional<User> foundedUserOptional = Optional.empty();

        when(userRepository.findUserByUsername(anyString()))
                .thenReturn(foundedUserOptional);

        assertThatThrownBy(() -> {
            mongoAuthUserDetailService.loadUserByUsername("user");
        }).isInstanceOf(UsernameNotFoundException.class);
    }
}