package com.softserve.booksCatalogPrototype.service.impl;

import com.softserve.booksCatalogPrototype.model.Author;
import com.softserve.booksCatalogPrototype.repository.AuthorRepository;
import com.softserve.booksCatalogPrototype.service.GeneralDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService implements GeneralDao<Author> {

    @Autowired
    private AuthorRepository authorRepository;

    @Override
    public Author save(Author object) {
        return null;
    }

    @Override
    public List<Author> getAll() {
        return null;
    }

    @Override
    public Author get(String isbn) {
        return null;
    }

    @Override
    public void delete(Author object) {
    }

    @Override
    public Author update(Author object) {
        return null;
    }


    public Author findByFirstNameIsAndSecondName(String firstName, String secondName){
        return authorRepository.findByFirstNameIsAndSecondName(firstName, secondName);
    }
}
