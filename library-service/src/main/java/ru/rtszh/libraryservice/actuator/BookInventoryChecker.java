package ru.rtszh.libraryservice.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;
import ru.rtszh.libraryservice.domain.Book;
import ru.rtszh.libraryservice.repository.BookRepository;

import java.util.List;

@Component
public class BookInventoryChecker implements HealthIndicator {

    private static final int BOOKS_MIN_AMOUNT = 2;

    private final BookRepository repository;

    public BookInventoryChecker(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Health health() {
        List<Book> books = repository.findAll();

        if (books.size() < BOOKS_MIN_AMOUNT) {
            return Health.down()
                    .status(Status.DOWN)
                    .withDetail(
                            String.format(
                                    "Books amount in database is not enough. Minimum required: %d, actual amount: %d",
                                    BOOKS_MIN_AMOUNT,
                                    books.size()
                            ),
                            books.size()
                    )
                    .build();
        }

        return Health.up()
                .status(Status.UP)
                .withDetail("Current books amount in database is enough. Minimum books required: ", BOOKS_MIN_AMOUNT)
                .build();

    }
}
