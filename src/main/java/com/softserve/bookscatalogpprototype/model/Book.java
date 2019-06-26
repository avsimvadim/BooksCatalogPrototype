package com.softserve.bookscatalogpprototype.model;

import com.softserve.bookscatalogpprototype.annotations.CascadeSave;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "isbn")
@ToString
public class Book {

    @Id
    private ObjectId isbn;

    private String name;

    private Date yearPublished;

    private Publisher publisher;

    private Date creationDate;

    @DBRef
    @CascadeSave
    private List<Author> authors = new ArrayList<>();

    @DBRef
    @CascadeSave
    private List<Review> reviews = new ArrayList<>();

    public Book(String name, Date yearPublished, Publisher publisher, Date creationDate) {
        this.name = name;
        this.yearPublished = yearPublished;
        this.publisher = publisher;
        this.creationDate = creationDate;
    }
}
