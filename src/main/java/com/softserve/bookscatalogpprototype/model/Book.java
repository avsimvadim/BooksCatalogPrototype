package com.softserve.bookscatalogpprototype.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;


@Document
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Book {

    @Id
    private ObjectId id;

    private String name;

    private Date yearPublished;

    private Date createDate;

    @DBRef
    private List<Author> authors;

    @DBRef
    private List<Review> reviews;

}
