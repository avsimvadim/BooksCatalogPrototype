package com.softserve.bookscatalogpprototype.rest;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.softserve.bookscatalogpprototype.model.Book;
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
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private GridFsOperations gridFsOperations;

    @PostMapping("/book")
    public boolean createBook(@RequestBody Book book) {
        bookService.save(book);
        return true;
    }

    @GetMapping("/books")
    public List<Book> getBooks() {
        return bookService.getAll();
    }

    @GetMapping("/book")
    public ResponseEntity<Book> getBookById(@RequestParam Long id){
        Book book = bookService.get(id);
        return ResponseEntity.ok().body(book);
    }

    @PostMapping("/uploadImage/{id}")
    public String uploadBookCover(@RequestParam MultipartFile file, @PathVariable ObjectId id){
        ObjectId objectId = new ObjectId();
        try {
            InputStream inputStream = file.getInputStream();
            DBObject metaData = new BasicDBObject();
            metaData.put("book", "hjdei");
            objectId = gridFsOperations.store(inputStream,"test.png","image/png", metaData);
        } catch (Exception e) {
            e.printStackTrace();
        }

        GridFSFile gridfile = gridFsOperations.findOne(new Query(Criteria.where("_id").is(objectId)));
        System.out.println(gridfile.toString());
        return "ok";
    }

}
