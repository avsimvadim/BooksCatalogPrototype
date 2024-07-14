package com.booksCatalogPrototype.service;

import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.booksCatalogPrototype.exception.custom.AuthorException;
import com.booksCatalogPrototype.model.Author;
import com.booksCatalogPrototype.model.Book;
import com.booksCatalogPrototype.model.Review;
import com.booksCatalogPrototype.repository.AuthorRepository;
import com.booksCatalogPrototype.repository.BookRepository;
import org.apache.commons.io.IOUtils;
import org.apache.commons.math3.util.Precision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.booksCatalogPrototype.exception.custom.BookException;
import com.booksCatalogPrototype.exception.custom.ContentException;
import com.booksCatalogPrototype.exception.custom.CoverException;

@Service
public class BookServiceImpl implements BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);
    private static final String DELETING_BOOK_IS_FAILED = "Deleting book failed.";
    private static final String GETTING_BOOK_IS_FAILED = "Getting book failed.";
    private static final String UPDATING_BOOK_IS_FAILED = "Updating book failed.";
    private static final String GIVING_RATE_IS_FAILED = "Giving rate to book failed.";
    private static final String GETTING_BOOK_COVER_FAILED = "Getting cover failed.";
    private static final String GETTING_BOOK_CONTENT_FAILED = "Getting content failed.";
    private static final String CONTENT_TYPE_PNG = "image/png";
    private static final String CONTENT_TYPE_PDF = "application/pdf";

    private BookRepository bookRepository;

    private AuthorRepository authorRepository;

    private MongoOperations mongoOperations;

    private GridFsTemplate gridFsTemplate;

    private AuthorServiceImpl authorService;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, AuthorRepository authorRepository,
                           MongoOperations mongoOperations, GridFsTemplate gridFsTemplate,
                           AuthorServiceImpl authorService) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.mongoOperations = mongoOperations;
        this.gridFsTemplate = gridFsTemplate;
        this.authorService = authorService;
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
        if (Objects.isNull(id)) {
            logger.error(GETTING_BOOK_IS_FAILED);
            throw new BookException("Book id is null");
        }
        Book book = bookRepository.findById(id).orElseThrow(() -> new BookException("There is no book with id " + id));
        return book;
    }

    @Override
    public void delete(Book book) {
        if (Objects.isNull(book)) {
            logger.error(DELETING_BOOK_IS_FAILED);
            throw new BookException("Book is null");
        }
        try {
            deleteBookContent(book.getIsbn());
            deleteBookCover(book.getIsbn());
            bookRepository.delete(book);
            logger.info("book " + book.toString() + " is deleted");
        } catch (Exception e) {
            throw new BookException("Could not delete the book");
        }
    }

    @Override
    public Book update(Book newBook) {
        if (Objects.isNull(newBook)) {
            logger.error(UPDATING_BOOK_IS_FAILED);
            throw new BookException("New book is null");
        }
        Supplier<BookException> supplier = () -> new BookException("There is no book with such id");
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

    public List<Book> getBooksByAuthor(String authorId) {
        if (Objects.isNull(authorId)) {
            logger.error(GETTING_BOOK_IS_FAILED);
            throw new BookException("Book id is null");
        }

        Author author = authorRepository.findById(authorId).orElseThrow(() -> new AuthorException("No author with such id"));
        List<Book> booksByAuthors = bookRepository.findBooksByAuthors(author);
        return booksByAuthors;
    }

    public Book deleteAuthorFromBook(String bookId, String authorId) {
        if (Objects.isNull(authorId) || Objects.isNull(bookId)) {
            logger.error(DELETING_BOOK_IS_FAILED);
            throw new BookException("Author or book id is null");
        }

        Book book = get(bookId);
        Author author = authorService.get(authorId);
        if (!book.getAuthors().contains(author)) {
            throw new AuthorException("The book with id " + bookId + " already does not contain the author with id " + authorId);
        }
        book.getAuthors().remove(author);
        Book saved = bookRepository.save(book);
        return saved;
    }

    public List<Book> withRate(Pageable pageable) {
        return bookRepository.findAllByRateIsNot(0, pageable);
    }

    public List<Book> withRate(double rate, Pageable pageable) {
        return bookRepository.findWithRate(rate, pageable);
    }

    public Book giveRate(String id, int newRate) {
        if (Objects.isNull(id)) {
            logger.error(GIVING_RATE_IS_FAILED);
            throw new BookException("Author or book id is null");
        }

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

    public void deleteBooks(String... ids) {
        List<String> list = Lists.newArrayList(ids);
        Iterables.removeIf(list, Predicates.isNull());
        List<String> listWithoutDuplicates = list.stream().distinct().collect(Collectors.toList());
        listWithoutDuplicates.forEach(id -> this.get(id));
        listWithoutDuplicates.forEach(id -> this.delete(this.get(id)));
    }

    public String uploadBookCover(MultipartFile file, String id) {
        if (Objects.isNull(file)) {
            logger.error("File is null.");
            throw new CoverException("File is null");
        }
        if (!file.getContentType().equals(CONTENT_TYPE_PNG)) {
            logger.error("Wrong cover type.");
            throw new CoverException("Wrong cover type");
        }
        try {
            gridFsTemplate.delete(Query.query(Criteria.where("_id").is(id)));
        } catch (Exception e) {
            throw new CoverException("Failed to clear book's cover, wrong id");
        }
        DBObject metaData = new BasicDBObject();
        metaData.put("bookId", id);
        metaData.put("contentType", CONTENT_TYPE_PNG);
        try (InputStream is = file.getInputStream()) {
            return gridFsTemplate.store(is, id + ".png", CONTENT_TYPE_PNG, metaData).toString();
        } catch (Exception e) {
            throw new CoverException("Failed to delete book's cover, wrong id");
        }
    }

    public byte[] getBookCover(String id) {
        GridFSFile file = checkMediaTypeByBookId(CONTENT_TYPE_PNG, id);
        try (InputStream inputStream = gridFsTemplate.getResource(file).getInputStream()) {
            return IOUtils.toByteArray(inputStream);
        } catch (Exception e) {
            throw new CoverException("Failed to get book's cover");
        }
    }

    public void deleteBookCover(String id) {
        // TODO: 1/19/2021  
        //checkMediaTypeByBookId(CONTENT_TYPE_PNG, id);
        try {
            //gridFsTemplate.delete(Query.query(Criteria.where("_id").is(id)));
            Query query = new Query(GridFsCriteria.whereMetaData("bookId").is(id));
            gridFsTemplate.delete(query);
        } catch (Exception e) {
            throw new CoverException("Failed to delete book's cover, wrong id");
        }
    }

    public String uploadBookContent(MultipartFile file, String id) {
        if (Objects.isNull(file)) {
            logger.error("File is null.");
            throw new ContentException("File is null");
        }
        if (!file.getContentType().equals(CONTENT_TYPE_PDF)) {
            logger.error("Wrong content type.");
            throw new ContentException("Wrong content type");
        }
        //if (gridFsTemplate);
        try {
            gridFsTemplate.delete(Query.query(Criteria.where("_id").is(id)));
        } catch (Exception e) {
            throw new CoverException("Failed to clear book's content, wrong id");
        }
        DBObject metaData = new BasicDBObject();
        metaData.put("bookId", id);
        metaData.put("contentType", CONTENT_TYPE_PDF);
        try (InputStream is = file.getInputStream()) {
            return gridFsTemplate.store(is, id + ".pdf", CONTENT_TYPE_PDF, metaData).toString();
        } catch (Exception e) {
            throw new ContentException("Failed to delete book's content, wrong id");
        }
    }


    public byte[] getBookContent(String id) {
        GridFSFile file = checkMediaTypeByBookId(CONTENT_TYPE_PDF, id);
        try (InputStream inputStream = gridFsTemplate.getResource(file).getInputStream()) {
            return IOUtils.toByteArray(inputStream);
        } catch (Exception e) {
            throw new ContentException("Failed to get book's content");
        }
    }

    public void deleteBookContent(String id) {
        // TODO: 1/19/2021  
        //checkMediaTypeByBookId(CONTENT_TYPE_PDF, id);
        try {
            Query query = new Query(GridFsCriteria.whereMetaData("bookId").is(id));
            query.addCriteria(GridFsCriteria.whereMetaData("contentType").is(CONTENT_TYPE_PDF));
            gridFsTemplate.delete(query);
        } catch (Exception e) {
            throw new ContentException("Failed to delete book's content, wrong id");
        }
    }

    private GridFSFile checkMediaTypeByBookId(String mediaType, String id) {
        Query query = new Query(GridFsCriteria.whereMetaData("bookId").is(id));
        query.addCriteria(GridFsCriteria.whereMetaData("contentType").is(mediaType.toString()));
        GridFSFile file =
                gridFsTemplate.findOne(query);
        if (mediaType.equals(CONTENT_TYPE_PDF)) {
            if (Objects.isNull(file)) {
                logger.error(GETTING_BOOK_CONTENT_FAILED);
                throw new ContentException("Did not find a content with such id");
            }
            if (!file.getMetadata().get("contentType").toString().equals(CONTENT_TYPE_PDF)) {
                logger.error("Wrong id.");
                throw new ContentException("Wrong id");
            }
        } else if (mediaType.equals(CONTENT_TYPE_PNG)) {
            if (Objects.isNull(file)) {
                logger.error(GETTING_BOOK_COVER_FAILED);
                throw new CoverException("Did not find a cover with such id");
            }
            if (!file.getMetadata().get("contentType").toString().equals(CONTENT_TYPE_PNG)) {
                logger.error("Wrong id.");
                throw new CoverException("Wrong id");
            }
        } else {
            logger.error("No content type for this book.");
            throw new BookException("No content type for this book");
        }
        return file;
    }

    public Book findBookWithReview(Review review) {
        if (Objects.isNull(review)) {
            logger.error(GETTING_BOOK_IS_FAILED);
            throw new BookException("Review is null");
        }

        try {
            Book result = bookRepository.findBookByReviewsIs(review);
            return result;
        } catch (Exception e) {
            throw new BookException("There is no book with such review");
        }
    }

}
