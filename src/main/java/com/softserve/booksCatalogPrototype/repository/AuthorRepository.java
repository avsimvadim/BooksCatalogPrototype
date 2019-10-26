package com.softserve.booksCatalogPrototype.repository;

import com.softserve.booksCatalogPrototype.model.Author;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends MongoRepository<Author, String> {

    Author findByFirstNameIsAndSecondName(String firstName, String secondName);

}
