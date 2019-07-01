package com.softserve.booksCatalogPrototype.rest;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.softserve.booksCatalogPrototype.dto.BookDTO;
import com.softserve.booksCatalogPrototype.exception.*;
import com.softserve.booksCatalogPrototype.model.Book;
import com.softserve.booksCatalogPrototype.service.impl.BookService;
import com.softserve.booksCatalogPrototype.util.DTOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
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
        if (!(book instanceof Book)){
            throw new EntityException("is not book entity");
        }
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

    @GetMapping("/all_pagination")
    public ResponseEntity<List<BookDTO>> allPages(@RequestParam int pageNumber, @RequestParam int pageSize) {
        if (pageNumber < 0 || pageSize <= 0){
            throw new PaginationException("wrong page number or size");
        }
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

    @PutMapping("/update")
    public ResponseEntity<BookDTO> update(@RequestBody Book book){
        BookDTO bookDTO = DTOConverter.convertBook(bookService.update(book));
        return ResponseEntity.ok(bookDTO);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable String id){
        bookService.delete(bookService.get(id));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/bulk_delete")
    public ResponseEntity deleteBooks(@RequestParam("id") String[] ids){
        bookService.deleteBooks(ids);
        return ResponseEntity.ok().build();
    }

    //get books by author
    @GetMapping("/books/{authorId}")
    public ResponseEntity<List<BookDTO>> author(@PathVariable String authorId){
        List<Book> booksByAuthor = bookService.getBooksByAuthor(authorId);
        List<BookDTO> bookDTOS = DTOConverter.convertBookListToBookDTOList(booksByAuthor);
        return ResponseEntity.ok(bookDTOS);
    }

    //get books with rate
    @GetMapping("/rate_exists")
    public ResponseEntity<List<BookDTO>> rateExists(@RequestParam int pageNumber, @RequestParam int pageSize){
        if (pageNumber < 0 || pageSize <= 0){
            throw new PaginationException("wrong page number or size");
        }
        List<BookDTO> bookDTOS = DTOConverter.convertBookListToBookDTOList(bookService.withRate(new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "rate"))));
        return ResponseEntity.ok(bookDTOS);
    }

    // get books with rate
    @GetMapping("/rate")
    public ResponseEntity<List<BookDTO>> rate(@RequestParam int rate, @RequestParam int pageNumber, @RequestParam int pageSize){
        if (pageNumber < 0 || pageSize <= 0){
            throw new PaginationException("wrong page number or size");
        }
        if (rate < 1 || rate > 5){
            throw new RateOutOfBoundException("Rate is more than 5 or less than 1");
        }
        List<BookDTO> bookDTOS = DTOConverter.convertBookListToBookDTOList(bookService.withRate(rate, new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "rate"))));
        return ResponseEntity.ok(bookDTOS);
    }

    //give rate to book with id, rate from 1 to 5
    @GetMapping("/give_rate/{id}")
    public ResponseEntity<BookDTO> giveRate(@PathVariable String id, @RequestParam int rate){
        BookDTO bookDTO = DTOConverter.convertBook(bookService.giveRate(id, rate));
        return ResponseEntity.ok(bookDTO);
    }

//  put cover and book id, get cover id
    @PostMapping("/upload_cover/{id}")
    public ResponseEntity<String> uploadCover(@RequestParam MultipartFile file, @PathVariable String id){
        DBObject metaData = new BasicDBObject();
        metaData.put("bookId", id);
        try(InputStream is = file.getInputStream()) {
            String coverId = gridFsOperations.store(is,id + ".png", "image/png", metaData).toString();
            return ResponseEntity.ok(coverId);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            throw new UploadCoverException("failed to upload cover");
        }
    }

    @GetMapping(value = "/get_cover/{id}", produces = "multipart/form-data")
    public ResponseEntity<Resource> getCover(@PathVariable String id){
        GridFSFile file = gridFsOperations.findOne(Query.query(Criteria.where("metadata.bookId").is(id)));
        Resource resource = new GridFsResource(file);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_LOCATION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(resource);
    }

//  delete by cover id
    @GetMapping("/delete_cover/{id}")
    public ResponseEntity deleteCover(@PathVariable String id) {
        gridFsOperations.delete(Query.query(Criteria.where("_id").is(id)));
        return ResponseEntity.ok().build();
    }

    //    put content and book id, get content id
    @PostMapping("/upload_content/{id}")
    public ResponseEntity<String> uploadContent(@RequestParam MultipartFile file, @PathVariable String id) throws UploadContentException {
        DBObject metaData = new BasicDBObject();
        metaData.put("bookId", id);
        try(InputStream is = file.getInputStream()) {
            String contentId = gridFsOperations.store(is,id + ".txt", "text/plain", metaData).toString();
            return ResponseEntity.ok(contentId);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            throw new UploadContentException("failed to upload a content");
        }
    }

    // get content by book id
    @GetMapping(value = "/get_content/{id}", produces = "multipart/form-data")
    public ResponseEntity<Resource> getContent(@PathVariable String id){
        GridFSFile file = gridFsOperations.findOne(Query.query(Criteria.where("metadata.bookId").is(id)));
        Resource resource = new GridFsResource(file);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_LOCATION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(resource);
    }

//    delete content by content id
    @GetMapping("/delete_content/{id}")
    public ResponseEntity deleteContent(@PathVariable String id) {
        gridFsOperations.delete(Query.query(Criteria.where("_id").is(id)));
        return ResponseEntity.ok().build();
    }


}
