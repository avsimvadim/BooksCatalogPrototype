package com.softserve.bookscatalogpprototype.service.impl;

import com.softserve.bookscatalogpprototype.model.Author;
import com.softserve.bookscatalogpprototype.model.Book;
import com.softserve.bookscatalogpprototype.repository.BookRepository;
import com.softserve.bookscatalogpprototype.service.GeneralDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BookService implements GeneralDao<Book> {

    @Autowired
    private BookRepository bookRepository;

    @Override
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public List<Book> getAll() {
        return bookRepository.findAll();
    }

    @Override
    public Book get(long isbn) {
        return bookRepository.findById(isbn).get();
    }

    @Override
    public void delete(Book book) {
        bookRepository.delete(book);
    }

    public List<Book> getBooksByAuthor(Author author){
        return bookRepository.getBooksByAuthors(author);
    }
}
