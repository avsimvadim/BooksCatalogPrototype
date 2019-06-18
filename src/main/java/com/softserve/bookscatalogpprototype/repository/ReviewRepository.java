package com.softserve.bookscatalogpprototype.repository;

import com.softserve.bookscatalogpprototype.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends MongoRepository<Review, Long> {
}
