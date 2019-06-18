package com.softserve.bookscatalogpprototype.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;


@Document
@EqualsAndHashCode(of = "isbn")
@Getter
@Setter
@ToString
public class Book {

    @Id
    private Long isbn;

    private String name;

    private Date yearPublished;

    private Date createDate;

    private List<Author> authors = new ArrayList<>();

    private List<Review> reviews = new LinkedList<>();

    public Book() {
    }

    public Book(Long isbn, String name, Date yearPublished, Date createDate, List<Author> authors, List<Review> reviews) {
        this.isbn = isbn;
        this.name = name;
        this.yearPublished = yearPublished;
        this.createDate = createDate;
        this.authors = authors;
        this.reviews = reviews;
    }
}
