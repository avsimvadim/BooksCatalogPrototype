package com.softserve.bookscatalogpprototype.service.impl;

import com.softserve.bookscatalogpprototype.model.Book;
import com.softserve.bookscatalogpprototype.repository.BookRepository;
import com.softserve.bookscatalogpprototype.service.GeneralDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookDao implements GeneralDao<Book> {

    @Autowired
    private BookRepository bookRepository;

    @Override
    public boolean save(Book book) {
        bookRepository.save(book);
        return true;
    }

    @Override
    public List<Book> getAll() {
        return null;
    }

    @Override
    public Book get(long isbn) {
        Optional<Book> result = bookRepository.findById(isbn);
        return result.get();
    }

    @Override
    public void delete(Book book) {
        bookRepository.delete(book);
    }
}
