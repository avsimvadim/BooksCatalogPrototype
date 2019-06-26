package com.softserve.bookscatalogpprototype.repository;


import com.softserve.bookscatalogpprototype.model.Author;
import com.softserve.bookscatalogpprototype.model.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends MongoRepository<Book, Long> {

    List<Book> getBooksByAuthors(Author author);

}
