package com.softserve.bookscatalogpprototype.rest;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.softserve.bookscatalogpprototype.dto.BookDTO;
import com.softserve.bookscatalogpprototype.exception.UploadContentException;
import com.softserve.bookscatalogpprototype.exception.UploadCoverException;
import com.softserve.bookscatalogpprototype.model.Author;
import com.softserve.bookscatalogpprototype.model.Book;
import com.softserve.bookscatalogpprototype.repository.AuthorRepository;
import com.softserve.bookscatalogpprototype.service.impl.AuthorService;
import com.softserve.bookscatalogpprototype.service.impl.BookService;
import com.softserve.bookscatalogpprototype.util.DTOConverter;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private GridFsOperations gridFsOperations;

    @PostMapping("/add")
    public ResponseEntity<BookDTO> add(@RequestBody Book book) {
        BookDTO result = DTOConverter.convertBook(bookService.save(book));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    public ResponseEntity<List<BookDTO>> all() {
        List<BookDTO> result = bookService.getAll().stream()
                .map(book -> DTOConverter.convertBook(book))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/allPage")
    public ResponseEntity<List<BookDTO>> allPAge(@RequestParam int pageNumber, @RequestParam int pageSize) {
        Page<Book> pageResult = bookService.getAll(new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.ASC, "name")));
        List<BookDTO> result = pageResult.stream()
                .map(book -> DTOConverter.convertBook(book))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<BookDTO> get(@PathVariable String id){
        BookDTO result = DTOConverter.convertBook(bookService.get(id));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable String id){
        bookService.delete(bookService.get(id));
        return ResponseEntity.ok().build();
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

    @PostMapping("/update")
    public ResponseEntity<Book> update(@RequestBody Book book){
        Book update = bookService.update(book);
        return ResponseEntity.ok(update);
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
