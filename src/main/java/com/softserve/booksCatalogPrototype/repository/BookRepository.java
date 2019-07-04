package com.softserve.booksCatalogPrototype.repository;


import com.softserve.booksCatalogPrototype.model.Author;
import com.softserve.booksCatalogPrototype.model.Book;
import com.softserve.booksCatalogPrototype.model.Review;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends MongoRepository<Book, String> {

    List<Book> findBooksByAuthors(Author author);

    List<Book> findAllByRateIsNot(int number, Pageable pageable);

    List<Book> findAllByRateIs(double rate, Pageable pageable);

    Book findBookByReviewsIs(Review review);

}
