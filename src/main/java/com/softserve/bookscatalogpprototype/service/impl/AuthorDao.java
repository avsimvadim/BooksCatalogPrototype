package com.softserve.bookscatalogpprototype.service.impl;

import com.softserve.bookscatalogpprototype.model.Author;
import com.softserve.bookscatalogpprototype.service.GeneralDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional

public class AuthorDao implements GeneralDao<Author> {
    @Override
    public boolean save(Author object) {
        return false;
    }

    @Override
    public List<Author> getAll() {
        return null;
    }

    @Override
    public Author get(long isbn) {
        return null;
    }

    @Override
    public void delete(Author object) {

    }
}
