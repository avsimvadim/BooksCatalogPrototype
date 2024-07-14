package com.booksCatalogPrototype.service;

import com.booksCatalogPrototype.model.Author;

import java.util.List;

public interface AuthorService {
    Author save(Author object);

    List<Author> getAll();

    Author get(String id);

    void delete(Author object);

    Author update(Author object);
}
