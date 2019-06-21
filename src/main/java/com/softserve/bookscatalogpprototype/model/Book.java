package com.softserve.bookscatalogpprototype.model;

import com.softserve.bookscatalogpprototype.annotations.CascadeSave;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document
@NoArgsConstructor(force = true)
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "isbn")
@ToString
public class Book {

    @Id
    private ObjectId isbn;

    private final String name;

    private final Date yearPublished;

    private final Publisher publisher;

    private final Date creationDate;

    @DBRef
    @CascadeSave
    private final List<Author> authors;

    @DBRef
    @CascadeSave
    private final List<Review> reviews;

}
