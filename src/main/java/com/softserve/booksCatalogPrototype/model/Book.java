package com.softserve.booksCatalogPrototype.model;

import com.softserve.booksCatalogPrototype.annotations.CascadeSave;
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

    //rate can be between 1 and 5, rate = 0 means book is not evaluated yet
    private double rate;

    private int totalVoteCount;

    @DBRef
    @CascadeSave
    private List<Author> authors = new ArrayList<>();

    @DBRef
    @CascadeSave
    private List<Review> reviews = new ArrayList<>();

}
