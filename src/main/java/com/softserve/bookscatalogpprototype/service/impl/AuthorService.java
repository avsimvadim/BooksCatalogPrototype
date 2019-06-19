package com.softserve.bookscatalogpprototype.service.impl;

import com.softserve.bookscatalogpprototype.model.Author;
import com.softserve.bookscatalogpprototype.repository.AuthorRepository;
import com.softserve.bookscatalogpprototype.service.GeneralDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthorService implements GeneralDao<Author> {

    @Autowired
    private AuthorRepository authorRepository;

    @Override
    public boolean save(Author object) {
        authorRepository.save(object);
        return true;
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
