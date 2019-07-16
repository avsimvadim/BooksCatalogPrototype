package com.softserve.booksCatalogPrototype.service;

import com.softserve.booksCatalogPrototype.model.Author;

import java.util.List;

public interface AuthorServiceInterface {
    Author save(Author object);

    List<Author> getAll();

    Author get(String id);

    void delete(Author object);

    Author update(Author object);
}
