package com.softserve.bookscatalogpprototype.service.impl;

import com.softserve.bookscatalogpprototype.exception.RateOutOfBoundException;
import com.softserve.bookscatalogpprototype.model.Author;
import com.softserve.bookscatalogpprototype.model.Book;
import com.softserve.bookscatalogpprototype.repository.AuthorRepository;
import com.softserve.bookscatalogpprototype.repository.BookRepository;
import com.softserve.bookscatalogpprototype.service.GeneralDao;
import com.softserve.bookscatalogpprototype.util.BookRateCheck;
import org.apache.commons.math3.util.Precision;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BookService implements GeneralDao<Book> {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private MongoOperations mongoOperations;
    // add new book, if author first and second name is same to the one in the database, no new author will be created and
    // id from the database's author will be taken and set to incoming author
    @Override
    public Book save(Book book) {
        // TODO: 27.06.2019 exception
        Book checkedBook = null;
        try {
            checkedBook = BookRateCheck.rateCheck(book);
        } catch (RateOutOfBoundException e) {
            e.printStackTrace();
        }
        checkedBook.getAuthors().stream().forEach(author -> {
            Author result = authorRepository.findByFirstNameIsAndSecondName(author.getFirstName(), author.getSecondName());
            if (result != null) {
                ObjectId objectId = result.getId();
                author.setId(objectId);
            }
        });
        return bookRepository.save(checkedBook);
    }

    @Override
    public List<Book> getAll() {
        return bookRepository.findAll();
    }

    @Override
    public Book get(String isbn) {
        ObjectId objectId = new ObjectId(isbn);
        return bookRepository.findById(objectId).get();
    }

    @Override
    public void delete(Book book) {
        bookRepository.delete(book);
    }

    @Override
    public Book update(Book newBook) {
        // TODO: 27.06.2019 pizdec z variables and exception(
        Book checkedBook = null;
        try {
            checkedBook = BookRateCheck.rateCheck(newBook);
        } catch (RateOutOfBoundException e) {
            e.printStackTrace();
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(checkedBook.getIsbn()));
        query.fields().include("_id");

        Update update = new Update();
        update.set("name", checkedBook.getName());
        update.set("yearPublished", checkedBook.getYearPublished());
        update.set("publisher", checkedBook.getPublisher());
        update.set("creationDate", checkedBook.getCreationDate());
        update.set("rate", checkedBook.getRate());

        mongoOperations.updateFirst(query, update, Book.class);

        return bookRepository.findById(newBook.getIsbn()).get();
    }


    public Page<Book> getAll(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    public List<Book> getBooksByAuthor(String authorId){
        return bookRepository.findBooksByAuthors(authorRepository.findById(new ObjectId(authorId)).get());
    }

    public List<Book> withRate(Pageable pageable){
        return bookRepository.findAllByRateIsNotNull(pageable);
    }

    public List<Book> withRate(int rate, Pageable pageable){
        return bookRepository.findAllByRateIs(rate, pageable);
    }

    public Book giveRate(String id, int newRate){
        Book book = this.get(id);
        double rate = book.getRate();
        int totalVoteCount = book.getTotalVoteCount();
        rate = Precision.round((rate * totalVoteCount + newRate) / (totalVoteCount + 1), 1);
        book.setRate(rate);
        book.setTotalVoteCount(totalVoteCount + 1);
        bookRepository.save(book);
        return book;
    }
}
