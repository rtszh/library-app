package ru.rtszh.libraryservice.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import ru.rtszh.libraryservice.domain.Page;

import java.util.List;

public interface PageRepository extends MongoRepository<Page, String> {
    List<Page> findByBookId(String bookId, Pageable pageable);
    int countAllByBookId(String bookId);
    void deleteAllByBookId(String bookId);
}
