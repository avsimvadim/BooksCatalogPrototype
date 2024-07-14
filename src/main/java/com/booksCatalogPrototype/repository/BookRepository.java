package com.booksCatalogPrototype.repository;

import java.util.List;

import com.booksCatalogPrototype.model.Author;
import com.booksCatalogPrototype.model.Book;
import com.booksCatalogPrototype.model.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends MongoRepository<Book, String> {

    List<Book> findBooksByAuthors(Author author);

    List<Book> findAllByRateIsNot(int number, Pageable pageable);

    @Query("{ 'rate' : ?0 }")
    List<Book> findWithRate(double rate, Pageable pageable);

    Book findBookByReviewsIs(Review review);

}
