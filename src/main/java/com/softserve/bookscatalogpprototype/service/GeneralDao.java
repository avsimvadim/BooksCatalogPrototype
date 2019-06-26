package com.softserve.bookscatalogpprototype.service;

import java.util.List;

public interface GeneralDao<T> {

    T save(T object);

    List<T> getAll();

    T get(String isbn);

    void delete(T object);

    T update(T object);
}
