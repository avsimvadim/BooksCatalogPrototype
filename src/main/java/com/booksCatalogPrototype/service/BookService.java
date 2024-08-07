package com.booksCatalogPrototype.service;

import com.booksCatalogPrototype.model.Book;

import java.util.List;

public interface BookService {

    Book save(Book object);

    List<Book> getAll();

    Book get(String isbn);

    void delete(Book object);

    Book update(Book object);
}
