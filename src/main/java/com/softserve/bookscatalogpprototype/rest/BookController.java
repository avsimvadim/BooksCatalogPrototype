package com.softserve.bookscatalogpprototype.rest;

import com.softserve.bookscatalogpprototype.model.Author;
import com.softserve.bookscatalogpprototype.model.Book;
import com.softserve.bookscatalogpprototype.service.impl.BookService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api")
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping("/book")
    public boolean createBook(@RequestBody Book book) {
        return bookService.save(book);
    }

    @GetMapping("/books")
    public List<Book> getBooks() {
        return bookService.getAll();
    }

    @GetMapping("/book")
    public ResponseEntity<Book> getBookById(@RequestParam("bookId") Long bookId){
        Book book = bookService.get(bookId);
        return ResponseEntity.ok().body(book);
    }
}
