package ru.rtszh.libraryservice.service.impl;

import org.springframework.stereotype.Service;
import ru.rtszh.libraryservice.domain.Book;
import ru.rtszh.libraryservice.domain.User;
import ru.rtszh.libraryservice.exceptions.BookNotFoundException;
import ru.rtszh.libraryservice.repository.BookRepository;
import ru.rtszh.libraryservice.repository.UserRepository;
import ru.rtszh.libraryservice.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public UserServiceImpl(BookRepository bookRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Override
    public int getPageNumber(Optional<String> usernameOptional, String bookId, int page) {

        String username;

        if (usernameOptional.isEmpty()) {
            return 1;
        } else {
            username = usernameOptional.get();
        }

        List<User> users = userRepository.findByUsernameAndBookId(username, bookId);

        if (users.size() > 1) {
            deleteDublicates(users);

            var currentUserData = users.get(0);

            updateUserData(currentUserData, page);

            return currentUserData.getStoppedAtPage();

        } else if (users.size() == 1) {
            var currentUserData = users.get(0);

            updateUserData(currentUserData, page);

            return currentUserData.getStoppedAtPage();

        } else if (page > 1) {
            createUserData(bookId, username, page);

            return page;

        } else {
            return 1;
        }

    }

    private void deleteDublicates(List<User> users) {
        for (int i = 1; i < users.size(); i++) {
            userRepository.delete(users.get(i));
        }
    }

    private void updateUserData(User user, int page) {
        if (page < 0) {
            return;
        }

        user.setStoppedAtPage(page);
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    private void createUserData(String bookId, String username, int page) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(
                                String.format("Book with id='%s' not found", bookId)
                        )
                );

        User user = User.builder()
                .username(username)
                .stoppedAtPage(page)
                .createdAt(LocalDateTime.now())
                .bookId(book)
                .build();

        userRepository.save(user);
    }
}
