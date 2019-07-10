package com.softserve.booksCatalogPrototype.model;

import com.softserve.booksCatalogPrototype.annotations.CascadeDelete;
import com.softserve.booksCatalogPrototype.annotations.CascadeSave;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Document
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "isbn")
@ToString
public class Book {

    @Id
    private String isbn;

    private String name;

    private Date yearPublished;

    private Publisher publisher;

    @CreatedDate
    private Date creationDate;

    //rate can be between 1 and 5, rate = 0 means book is not evaluated yet
    private double rate;

    private int totalVoteCount;

    @DBRef
    @CascadeSave
    private List<Author> authors = new ArrayList<>();

    @DBRef
    @CascadeSave
    @CascadeDelete
    private List<Review> reviews = new LinkedList<>();

    public Book(String name, Date yearPublished, Publisher publisher, List<Author> authors) {
        this.name = name;
        this.yearPublished = yearPublished;
        this.publisher = publisher;
        this.authors = authors;
    }

}
