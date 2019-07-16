package com.softserve.booksCatalogPrototype.service.impl;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.softserve.booksCatalogPrototype.exception.custom.AuthorException;
import com.softserve.booksCatalogPrototype.exception.custom.BookException;
import com.softserve.booksCatalogPrototype.exception.custom.ContentException;
import com.softserve.booksCatalogPrototype.exception.custom.CoverException;
import com.softserve.booksCatalogPrototype.model.Author;
import com.softserve.booksCatalogPrototype.model.Book;
import com.softserve.booksCatalogPrototype.model.Review;
import com.softserve.booksCatalogPrototype.repository.AuthorRepository;
import com.softserve.booksCatalogPrototype.repository.BookRepository;
import com.softserve.booksCatalogPrototype.service.BookServiceInterface;
import org.apache.commons.math3.util.Precision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class BookService implements BookServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    private BookRepository bookRepository;

    private AuthorRepository authorRepository;

    private MongoOperations mongoOperations;

    private GridFsOperations gridFsOperations;

    @Autowired
    public BookService(BookRepository bookRepository, AuthorRepository authorRepository, MongoOperations mongoOperations, GridFsOperations gridFsOperations) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.mongoOperations = mongoOperations;
        this.gridFsOperations = gridFsOperations;
    }

    @Override
    public Book save(Book book) {
        Book result = bookRepository.save(book);
        logger.info("Book " + book.toString() + " is saved");
        return result;
    }

    @Override
    public List<Book> getAll() {
        return bookRepository.findAll();
    }

    @Override
    public Book get(String id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new BookException("There is no book with id " + id));
        return book;
    }

    @Override
    public void delete(Book book) {
       try {
           deleteBookContent(book.getIsbn());
           deleteBookCover(book.getIsbn());
           bookRepository.delete(book);
           logger.info("book " + book.toString() + " is deleted");
        } catch (Exception e){
            throw new BookException("Could not delete the book");
        }
    }

    @Override
    public Book update(Book newBook) {
        Supplier<BookException> supplier = () -> new BookException( "There is no book with such id");
        bookRepository.findById(newBook.getIsbn()).orElseThrow(supplier);

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(newBook.getIsbn()));
        query.fields().include("_id");

        Update update = new Update();
        update.set("name", newBook.getName());
        update.set("yearPublished", newBook.getYearPublished());
        update.set("publisher", newBook.getPublisher());
        update.set("rate", newBook.getRate());

        mongoOperations.updateFirst(query, update, Book.class);
        Book result = bookRepository.findById(newBook.getIsbn()).orElseThrow(supplier);
        logger.info("Book is updated");
        return result;
    }

    public Page<Book> getAll(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    public List<Book> getBooksByAuthor(String authorId){
        Author author = authorRepository.findById(authorId).orElseThrow(() -> new AuthorException("No author with such id"));
        List<Book> booksByAuthors = bookRepository.findBooksByAuthors(author);
        return booksByAuthors;
    }

    public List<Book> withRate(Pageable pageable){
        return bookRepository.findAllByRateIsNot(0, pageable);
    }

    public List<Book> withRate(double rate, Pageable pageable){
        return bookRepository.findWithRate(rate, pageable);
    }

    public Book giveRate(String id, int newRate){
        Book book = this.get(id);
        double rate = book.getRate();
        int totalVoteCount = book.getTotalVoteCount();
        rate = Precision.round((rate * totalVoteCount + newRate) / (totalVoteCount + 1), 1);
        book.setRate(rate);
        book.setTotalVoteCount(totalVoteCount + 1);
        bookRepository.save(book);
        logger.info("Book with id " + id + " is rated");
        return book;
    }

    public void deleteBooks(String... ids){
        List<String> list = Lists.newArrayList(ids);
        Iterables.removeIf(list, Predicates.isNull());
        List<String> listWithoutDuplicates = list.stream().distinct().collect(Collectors.toList());
        listWithoutDuplicates.forEach(id -> this.get(id));
        listWithoutDuplicates.forEach(id -> this.delete(this.get(id)));
    }

    public String uploadBookCover(MultipartFile file, String id){
        DBObject metaData = new BasicDBObject();
        metaData.put("bookId", id);
        try(InputStream is = file.getInputStream()) {
            return gridFsOperations.store(is,id + ".png", "image/png",metaData).toString();
        } catch (Exception e) {
            throw new CoverException("Failed to upload cover");
        }
    }

    public Resource getBookCover(String id){
        GridFSFile file = gridFsOperations.findOne(Query.query(Criteria.where("metadata.bookId").is(id)));
        if (file == null){
            throw new CoverException("Did not find a cover with such id");
        }
        return new GridFsResource(file);
    }

    public void deleteBookCover(String id){
        try {
            gridFsOperations.delete(Query.query(Criteria.where("_id").is(id)));
        } catch (Exception e){
            throw new CoverException("Failed to delete book's cover, wrong id");
        }
    }

    public String uploadBookContent(MultipartFile file, String id){
        DBObject metaData = new BasicDBObject();
        metaData.put("bookId", id);
        try(InputStream is = file.getInputStream()) {
            return gridFsOperations.store(is,id + ".txt", "text/plain", metaData).toString();
        } catch (Exception e) {
            throw new ContentException("Failed to delete book's content, wrong id");
        }
    }

    public Resource getBookContent(String id){
        GridFSFile file = gridFsOperations.findOne(Query.query(Criteria.where("metadata.bookId").is(id)));
        if (file == null){
            throw new ContentException("Did not find a content with such id");
        }
        return new GridFsResource(file);
    }

    public void deleteBookContent(String id){
        try {
            gridFsOperations.delete(Query.query(Criteria.where("_id").is(id)));
        } catch (Exception e){
            throw new ContentException("Failed to delete book's content, wrong id");
        }
    }

    public Book findBookWithReview(Review review){
        try{
            Book result = bookRepository.findBookByReviewsIs(review);
            return result;
        } catch (Exception e){
            throw new BookException("There is no book with such review");
        }
    }

}
