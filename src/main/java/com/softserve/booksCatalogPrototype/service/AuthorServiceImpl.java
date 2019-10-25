package com.softserve.booksCatalogPrototype.service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.softserve.booksCatalogPrototype.exception.custom.AuthorException;
import com.softserve.booksCatalogPrototype.exception.custom.BookException;
import com.softserve.booksCatalogPrototype.model.Author;
import com.softserve.booksCatalogPrototype.model.Book;
import com.softserve.booksCatalogPrototype.repository.AuthorRepository;
import com.softserve.booksCatalogPrototype.repository.BookRepository;

@Service
public class AuthorServiceImpl implements AuthorService {

    private static final Logger logger = LoggerFactory.getLogger(AuthorServiceImpl.class);
    private static final String DELETING_AUTHOR_IS_FAILED = "Deleting of the [{}] failed.";

    private AuthorRepository authorRepository;

    private BookRepository bookRepository;

    private MongoOperations mongoOperations;

    @Autowired
    public AuthorServiceImpl(AuthorRepository authorRepository, BookRepository bookRepository, MongoOperations mongoOperations) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
        this.mongoOperations = mongoOperations;
    }

    @Override
    public Author save(Author author) {
        Author result = authorRepository.save(author);
        logger.info("Author " + author.toString() + " is saved");
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
        Author author = authorRepository.findById(id).orElseThrow(() -> new AuthorException("Did not find the author with id: " + id));
        return author;
    }

    @Override
    public void delete(Author author) {
        if(Objects.isNull(author)) {
            logger.error(DELETING_AUTHOR_IS_FAILED, "unknown author");
            throw new AuthorException("Author is null");
        }
        List<Book> booksByAuthors = bookRepository.findBooksByAuthors(author);
        String authorDescription = author.toString();
        if (booksByAuthors.isEmpty()){
            try {
                authorRepository.delete(author);
                logger.info("Author " + authorDescription + " is deleted");
            }catch (Exception e){
                logger.error(DELETING_AUTHOR_IS_FAILED, authorDescription);
                throw new AuthorException("Did not find " + authorDescription);
            }
        } else {
            logger.error(DELETING_AUTHOR_IS_FAILED, authorDescription);
            throw new BookException("There is at least 1 book connected to " + authorDescription);
        }
    }

    @Override
    public Author update(Author newAuthor) {
        if(Objects.isNull(newAuthor.getId())) {
            logger.error(DELETING_AUTHOR_IS_FAILED, newAuthor);
            throw new AuthorException("Empty id field in the author");
        }
        String newAuthorId = newAuthor.getId();
        Supplier<AuthorException> supplier = () -> new AuthorException( "Did not find the author with id: " + newAuthorId);
        authorRepository.findById(newAuthorId).orElseThrow(supplier);

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(newAuthorId));
        query.fields().include("_id");

        Update update = new Update();
        update.set("firstName", newAuthor.getFirstName());
        update.set("secondName", newAuthor.getSecondName());

        mongoOperations.updateFirst(query, update, Author.class);
        Author result = authorRepository.findById(newAuthorId).orElseThrow(supplier);
        logger.info("Author is updated");
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
