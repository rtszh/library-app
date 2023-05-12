package ru.rtszh.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import ru.rtszh.domain.Page;

import java.util.List;

public interface PageRepository extends MongoRepository<Page, String> {
    List<Page> findByBookId(String bookId, Pageable pageable);
    int countAllByBookId(String bookId);
    void deleteAllByBookId(String bookId);
}
