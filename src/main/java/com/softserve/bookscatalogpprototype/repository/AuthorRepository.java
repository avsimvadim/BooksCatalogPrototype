package com.softserve.bookscatalogpprototype.repository;

import com.softserve.bookscatalogpprototype.model.Author;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends MongoRepository<Author, Long> {
}
