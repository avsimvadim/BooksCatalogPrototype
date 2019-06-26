package com.softserve.bookscatalogpprototype.service.impl;

import com.softserve.bookscatalogpprototype.model.Author;
import com.softserve.bookscatalogpprototype.model.Book;
import com.softserve.bookscatalogpprototype.repository.AuthorRepository;
import com.softserve.bookscatalogpprototype.repository.BookRepository;
import com.softserve.bookscatalogpprototype.service.GeneralDao;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BookService implements GeneralDao<Book> {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    // add new book, if author first and second name is same to the one in the database, no new author will be created and
    // id from the database's author will be taken and set to incoming author
    @Override
    public Book save(Book book) {
        book.getAuthors().stream().forEach(author -> {
            Author result = authorRepository.findByFirstNameIsAndSecondName(author.getFirstName(), author.getSecondName());
            if (result != null) {
                ObjectId objectId = result.getId();
                author.setId(objectId);
            }
        });
        return bookRepository.save(book);
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


}
