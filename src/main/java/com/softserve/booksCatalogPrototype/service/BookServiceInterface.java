package com.softserve.booksCatalogPrototype.service;

import com.softserve.booksCatalogPrototype.model.Book;

import java.util.List;

public interface BookServiceInterface {

    Book save(Book object);

    List<Book> getAll();

    Book get(String isbn);

    void delete(Book object);

    Book update(Book object);
}
