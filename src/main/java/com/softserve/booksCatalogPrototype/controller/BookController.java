package com.softserve.booksCatalogPrototype.controller;

import com.softserve.booksCatalogPrototype.dto.BookDTO;
import com.softserve.booksCatalogPrototype.exception.custom.BookException;
import com.softserve.booksCatalogPrototype.exception.custom.ContentException;
import com.softserve.booksCatalogPrototype.exception.custom.PaginationException;
import com.softserve.booksCatalogPrototype.exception.custom.RateOutOfBoundException;
import com.softserve.booksCatalogPrototype.model.Book;
import com.softserve.booksCatalogPrototype.service.impl.BookService;
import com.softserve.booksCatalogPrototype.util.DTOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/book")
public class BookController {

    private BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping("/add")
    public ResponseEntity<Book> add(@RequestBody BookDTO bookDTO) {
        Book book = DTOConverter.convertBook(bookDTO);
        Book result = bookService.save(book);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Book>> all() {
        List<Book> result = bookService.getAll();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all_pagination")
    public ResponseEntity<List<Book>> allPages(@RequestParam int pageNumber, @RequestParam int pageSize) {
        if (pageNumber < 0 || pageSize <= 0){
            throw new PaginationException("Wrong page number or size" );
        }
        Page<Book> pageResult = bookService.getAll(new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.ASC, "name")));
        if (!pageResult.hasContent()){
            throw new BookException("There are no books");
        }
        List<Book> result = pageResult.getContent();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Book> get(@PathVariable String id){
        Book result = bookService.get(id);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/update")
    public ResponseEntity<Book> update(@RequestBody Book book){
        Book updated = bookService.update(book);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable String id){
        Book book = bookService.get(id);
        bookService.delete(book);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/bulk_delete")
    public ResponseEntity deleteBooks(@RequestParam("id") String... ids){
        bookService.deleteBooks(ids);
        return ResponseEntity.ok().build();
    }

    //get books by author
    @GetMapping("/books/{authorId}")
    public ResponseEntity<List<Book>> author(@PathVariable String authorId){
        List<Book> result = bookService.getBooksByAuthor(authorId);
        return ResponseEntity.ok(result);
    }

    //get books with rate
    @GetMapping("/rate_exists")
    public ResponseEntity<List<Book>> rateExists(@RequestParam int pageNumber, @RequestParam int pageSize){
        if (pageNumber < 0 || pageSize < 1){
            throw new PaginationException("Wrong page number or size");
        }
        List<Book> books = bookService.withRate(new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "rate")));
        return ResponseEntity.ok(books);
    }

    // get books with rate
    @GetMapping("/rate")
    public ResponseEntity<List<Book>> rate(@RequestParam double rate, @RequestParam int pageNumber, @RequestParam int pageSize){
        if (pageNumber < 0 || pageSize < 1){
            throw new PaginationException("Wrong page number or size");
        }
        if (rate < 1 || rate > 5){
            throw new RateOutOfBoundException("Rate cannot be " + rate);
        }
        List<Book> books = bookService.withRate(rate, new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "rate")));
        return ResponseEntity.ok(books);
    }

    //give rate to book with id
    @GetMapping("/give_rate/{id}")
    public ResponseEntity<Book> giveRate(@PathVariable String id, @RequestParam int rate){
        if (rate < 1 || rate > 5){
            throw new RateOutOfBoundException("Rate cannot be " + rate);
        }
        Book result = bookService.giveRate(id, rate);
        return ResponseEntity.ok(result);
    }

    //  put cover and book id, get cover id
    @PostMapping(value = "/upload_cover/{id}")
    public ResponseEntity<String> uploadCover(@RequestParam MultipartFile file, @PathVariable String id){
        String bookCover = bookService.uploadBookCover(file, id);
        return ResponseEntity.ok(bookCover);
    }

    // TODO: 01.07.2019 to repair
    @GetMapping(value = "/get_cover/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<Resource> getCover(@PathVariable String id) throws IOException {
        Resource bookCover = bookService.getBookCover(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + bookCover.getFilename() + "\"")
                .contentLength(bookCover.contentLength())
                .contentType(MediaType.IMAGE_PNG)
                .body(bookCover);
    }

    //  delete by cover id
    @DeleteMapping("/delete_cover/{id}")
    public ResponseEntity deleteCover(@PathVariable String id) {
        bookService.deleteBookCover(id);
        return ResponseEntity.ok().build();
    }

    //    put content and book id, get content id
    @PostMapping("/upload_content/{id}")
    public ResponseEntity<String> uploadContent(@RequestParam MultipartFile file, @PathVariable String id) throws ContentException {
        String bookContent = bookService.uploadBookContent(file, id);
        return ResponseEntity.ok(bookContent);
    }

    // get content by book id
    @GetMapping(value = "/get_content/{id}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Resource> getContent(@PathVariable String id) throws IOException{
        Resource bookContent = bookService.getBookContent(id);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + bookContent.getFilename() + "\"")
                .contentLength(bookContent.contentLength())
                .contentType(MediaType.TEXT_PLAIN)
                .cacheControl(CacheControl.noCache())
                .body(bookContent);
    }

    //    delete content by content id
    @DeleteMapping("/delete_content/{id}")
    public ResponseEntity deleteContent(@PathVariable String id) {
        bookService.deleteBookContent(id);
        return ResponseEntity.ok().build();
    }

}
