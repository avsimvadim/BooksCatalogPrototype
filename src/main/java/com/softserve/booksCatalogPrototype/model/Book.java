package com.softserve.booksCatalogPrototype.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.softserve.booksCatalogPrototype.annotations.CascadeSave;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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

    private double rate;

    private int totalVoteCount;

    @DBRef
    @CascadeSave
    private List<Author> authors = new ArrayList<>();

    @DBRef
    @CascadeSave
    private List<Review> reviews = new LinkedList<>();

    public Book(String name, Date yearPublished, Publisher publisher, List<Author> authors) {
        this.name = name;
        this.yearPublished = yearPublished;
        this.publisher = publisher;
        this.authors = authors;
    }

}
