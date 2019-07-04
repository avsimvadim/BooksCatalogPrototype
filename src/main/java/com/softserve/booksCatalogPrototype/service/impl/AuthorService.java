package com.softserve.booksCatalogPrototype.service.impl;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.softserve.booksCatalogPrototype.exception.custom.AuthorException;
import com.softserve.booksCatalogPrototype.exception.custom.BookException;
import com.softserve.booksCatalogPrototype.model.Author;
import com.softserve.booksCatalogPrototype.model.Book;
import com.softserve.booksCatalogPrototype.repository.AuthorRepository;
import com.softserve.booksCatalogPrototype.repository.BookRepository;
import com.softserve.booksCatalogPrototype.service.GeneralDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class AuthorService implements GeneralDao<Author> {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MongoOperations mongoOperations;

    @Override
    public Author save(Author author) {
        Author result = authorRepository.save(author);
        return result;
    }

    @Override
    public List<Author> getAll() {
        List<Author> result = authorRepository.findAll();
        return result;
    }

    public Page<Author> getAll(Pageable pageable) {
        Page<Author> pages = authorRepository.findAll(pageable);
        return pages;
    }

    @Override
    public Author get(String id) {
        Author author = authorRepository.findById(id).orElseThrow(() -> new AuthorException("Did not find the author with this id"));
        return author;
    }

    @Override
    public void delete(Author author) {
        List<Book> booksByAuthors = bookRepository.findBooksByAuthors(author);
        if (booksByAuthors.isEmpty()){
            try {
                authorRepository.delete(author);
            }catch (Exception e){
                throw new AuthorException("Did not find the author with this id");
            }
        } else {
            throw new BookException("There is at least 1 book connected to this author");
        }
    }

    @Override
    public Author update(Author newAuthor) {
        Supplier<AuthorException> supplier = () -> new AuthorException( "Did not find the author with this id");
        authorRepository.findById(newAuthor.getId()).orElseThrow(supplier);

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(newAuthor.getId()));
        query.fields().include("_id");

        Update update = new Update();
        update.set("firstName", newAuthor.getFirstName());
        update.set("secondName", newAuthor.getSecondName());

        mongoOperations.updateFirst(query, update, Author.class);
        Author result = authorRepository.findById(newAuthor.getId()).orElseThrow(supplier);
        return result;
    }

    public void deleteAuthors(String... ids){
        List<String> list = Arrays.asList(ids);
        Iterables.removeIf(list, Predicates.isNull());
        List<String> listWithoutDuplicates = list.stream().distinct().collect(Collectors.toList());
        listWithoutDuplicates.stream().forEach(id -> this.get(id));
        listWithoutDuplicates.stream().forEach(id -> this.delete(this.get(id)));
    }

}
