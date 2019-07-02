package com.softserve.booksCatalogPrototype.service.impl;

import com.softserve.booksCatalogPrototype.exception.AuthorIsNotFoundException;
import com.softserve.booksCatalogPrototype.exception.EntityException;
import com.softserve.booksCatalogPrototype.model.Author;
import com.softserve.booksCatalogPrototype.model.Book;
import com.softserve.booksCatalogPrototype.repository.AuthorRepository;
import com.softserve.booksCatalogPrototype.repository.BookRepository;
import com.softserve.booksCatalogPrototype.service.GeneralDao;
import org.bson.types.ObjectId;
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
import java.util.Optional;
import java.util.stream.Stream;

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
        return authorRepository.save(author);
    }

    @Override
    public List<Author> getAll() {
        List<Author> result = authorRepository.findAll();
        return result;
    }

    public Stream<Author> getAll(Pageable pageable) {
        Stream<Author> pages = authorRepository.findAll(pageable).get();
        return pages;
    }

    @Override
    public Author get(String id) {
        Optional<Author> author = authorRepository.findById(new ObjectId(id));
        if (author.get() == null){
            throw new EntityException("author not found");
        }
        return author.get();
    }

    @Override
    public void delete(Author author) {
        List<Book> booksByAuthors = bookRepository.findBooksByAuthors(author);
        if (booksByAuthors.isEmpty()){
            try {
                authorRepository.delete(author);
            }catch (Exception e){
                e.printStackTrace();
                throw new EntityException("no author with this id or other causes");
            }
        } else {
            throw new EntityException("there is at least 1 book connected to this author");
        }
    }

    @Override
    public Author update(Author newAuthor) {
        Author author = authorRepository.findById(newAuthor.getId()).get();
        if (author == null){
            throw new AuthorIsNotFoundException("not found author with this id");
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(author.getId()));
        query.fields().include("_id");

        Update update = new Update();
        update.set("firstName", author.getFirstName());
        update.set("secondName", author.getSecondName());
        update.set("creationDate", author.getCreationDate());

        mongoOperations.updateFirst(query, update, Author.class);

        return authorRepository.findById(author.getId()).get();
    }

    public void deleteAuthors(String[] ids){
        List<String> list = Arrays.asList(ids);
        try {
            list.stream().forEach(id -> this.delete(this.get(id)));
        }catch (Exception e){
            e.printStackTrace();
            throw new EntityException("no author with this id or other causes");
        }
    }

}
