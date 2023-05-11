package ru.rtszh.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.rtszh.domain.Page;


public interface PageRepository extends MongoRepository<Page, String> {

}
