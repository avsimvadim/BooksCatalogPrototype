package com.softserve.bookscatalogpprototype.model;

import java.util.*;

public class Book {

    private String name;

    private Date yearPublished;

    private UUID isbn;

    private Publisher publisher;

    private Date createDate;

    private List<Author> authors = new ArrayList<>();

    private List<Review> reviews = new LinkedList<>();


}
