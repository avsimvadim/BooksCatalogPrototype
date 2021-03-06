package com.softserve.booksCatalogPrototype.repository;

import com.softserve.booksCatalogPrototype.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {

}
