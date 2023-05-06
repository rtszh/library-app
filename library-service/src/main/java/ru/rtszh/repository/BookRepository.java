package ru.rtszh.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.rtszh.domain.Book;

import java.util.List;


public interface BookRepository extends MongoRepository<Book, String> {

    List<Book> findAll();

    Book findByTitle(String title);

}
