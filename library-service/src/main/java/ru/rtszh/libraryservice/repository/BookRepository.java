package ru.rtszh.libraryservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.rtszh.libraryservice.domain.Book;

import java.util.List;


public interface BookRepository extends MongoRepository<Book, String> {

    List<Book> findAll();

    Book findByTitle(String title);

}
