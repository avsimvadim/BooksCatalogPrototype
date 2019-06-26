package com.softserve.bookscatalogpprototype.rest;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.softserve.bookscatalogpprototype.exception.UploadContentException;
import com.softserve.bookscatalogpprototype.exception.UploadCoverException;
import com.softserve.bookscatalogpprototype.model.Author;
import com.softserve.bookscatalogpprototype.model.Book;
import com.softserve.bookscatalogpprototype.repository.AuthorRepository;
import com.softserve.bookscatalogpprototype.service.impl.AuthorService;
import com.softserve.bookscatalogpprototype.service.impl.BookService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private GridFsOperations gridFsOperations;

    // add new book, if author first and second name is same to the one in the database, no new author will be created and
    // id from the database's author will be taken and set to incoming author
    @PostMapping("/book")
    public boolean createBook(@RequestBody Book book) {
        book.getAuthors().stream().forEach(author -> {
            Author result = authorService.findByFirstNameIsAndSecondName(author.getFirstName(), author.getSecondName());
            if (result != null) {
                ObjectId objectId = result.getId();
                author.setId(objectId);
            }
        });
        bookService.save(book);
        return true;
    }

    // TODO: 26.06.2019  
//    @GetMapping("/books")
//    public ResponseEntity<List<Book>> getBooks() {
//        return bookService.getAll();
//    }

    @GetMapping("/book")
    public ResponseEntity<Book> getBookById(@RequestParam Long id){
        Book book = bookService.get(id);
        return ResponseEntity.ok().body(book);
    }

//    put cover and book id, get cover id
    @PostMapping("/uploadCover/{id}")
    public ResponseEntity<String> uploadCover(@RequestParam MultipartFile file, @PathVariable String id) throws UploadCoverException{
        DBObject metaData = new BasicDBObject();
        metaData.put("bookId", id);
        try(InputStream is = file.getInputStream()) {
            String coverId = gridFsOperations.store(is,id + ".png", "image/png", metaData).toString();
            return ResponseEntity.ok(coverId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new UploadCoverException();
    }

//  delete by cover id
    @GetMapping("/deleteCover/{id}")
    public ResponseEntity deleteCover(@PathVariable String id) {
        gridFsOperations.delete(Query.query(Criteria.where("_id").is(id)));
        return ResponseEntity.ok().build();
    }

    //    put content and book id, get content id
    // TODO: 26.06.2019 content byte[] 
    @PostMapping("/uploadContent/{id}")
    public ResponseEntity<String> uploadContent(@RequestParam MultipartFile file, @PathVariable String id) throws UploadContentException{
        DBObject metaData = new BasicDBObject();
        metaData.put("bookId", id);
        try(InputStream is = file.getInputStream()) {
            String contentId = gridFsOperations.store(is,id + ".txt", "text/plain", metaData).toString();
            return ResponseEntity.ok(contentId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new UploadContentException();
    }

//    delete content by content id
    @GetMapping("/deleteContent/{id}")
    public ResponseEntity deleteContent(@PathVariable String id) {
        gridFsOperations.delete(Query.query(Criteria.where("_id").is(id)));
        return ResponseEntity.ok().build();
    }

    // TODO: 26.06.2019  
//    @GetMapping("/getBooks")
//    public ResponseEntity deleteContent(@PathParam(value = "author") Author author) {
//        List<Book> list = bookService.getBooksByAuthor(author);
//        System.out.println(list.toString());
//        return ResponseEntity.ok().build();
//    }
}
