package com.softserve.bookscatalogpprototype.rest;

import com.softserve.bookscatalogpprototype.model.Book;
import com.softserve.bookscatalogpprototype.service.impl.BookDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api")
public class BookController {

    @Autowired
    private BookDao bookDao;

//    public BookController(){
//        Book book1 = new Book(1L, "tolstoi", new Date(), new Date(), null, null);
//        Book book2 = new Book(2L, "math", new Date(), new Date(), null, null);
//
//        bookDao.save(book1);
//        bookDao.save(book2);
//    }

    @PostMapping("/book")
    public boolean createBook(@RequestBody Book book) {
        return bookDao.save(book);
    }

    @GetMapping("/books")
    public List<Book> getBooks() {
        return bookDao.getAll();
    }

    @GetMapping("/book")
    public ResponseEntity<Book> getBookById(@RequestParam("bookId") Long bookId)
            throws ResourceNotFoundException {
        Book book = bookDao.get(bookId);
        return ResponseEntity.ok().body(book);
    }
}
