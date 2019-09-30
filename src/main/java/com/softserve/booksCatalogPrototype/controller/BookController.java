package com.softserve.booksCatalogPrototype.controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.softserve.booksCatalogPrototype.dto.BookDTO;
import com.softserve.booksCatalogPrototype.exception.custom.BookException;
import com.softserve.booksCatalogPrototype.exception.custom.PaginationException;
import com.softserve.booksCatalogPrototype.exception.custom.RateOutOfBoundException;
import com.softserve.booksCatalogPrototype.model.Book;
import com.softserve.booksCatalogPrototype.service.BookServiceImpl;
import com.softserve.booksCatalogPrototype.util.DTOConverter;

@RestController
@RequestMapping("/api/book")
public class BookController {

    private final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private BookServiceImpl bookService;

    @Autowired
    public BookController(BookServiceImpl bookService) {
        this.bookService = bookService;
    }

	@Secured("ROLE_ADMIN")
    @PostMapping("/add")
    public ResponseEntity<Book> add(@RequestBody BookDTO bookDTO) {
        logger.info("In add method.");
        Book book = DTOConverter.convertBook(bookDTO);
        Book result = bookService.save(book);
        return ResponseEntity.ok(result);
    }

	@Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/all")
    public ResponseEntity<List<Book>> all() {
        logger.info("In all method.");
        List<Book> result = bookService.getAll();
        return ResponseEntity.ok(result);
    }

	@Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/all-pagination")
    public ResponseEntity<List<Book>> allPages(@RequestParam int pageNumber, @RequestParam int pageSize) {
        logger.info("In allPages method.");
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

	@Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/get/{id}")
    public ResponseEntity<Book> get(@PathVariable String id){
        logger.info("In get method.");
        Book result = bookService.get(id);
        return ResponseEntity.ok(result);
    }

	@Secured("ROLE_ADMIN")
    @PutMapping("/update")
    public ResponseEntity<Book> update(@RequestBody Book book){
        Book updated = bookService.update(book);
        return ResponseEntity.ok(updated);
    }

	@Secured("ROLE_ADMIN")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable String id){
        logger.info("In delete method.");
        Book book = bookService.get(id);
        bookService.delete(book);
        return ResponseEntity.ok().build();
    }

	@Secured("ROLE_ADMIN")
    @DeleteMapping("/bulk-delete")
    public ResponseEntity deleteBooks(@RequestParam("id") String... ids){
        logger.info("In deleteBooks method.");
        bookService.deleteBooks(ids);
        return ResponseEntity.ok().build();
    }

	@Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/books/{authorId}")
    public ResponseEntity<List<Book>> author(@PathVariable String authorId){
        logger.info("In author method.");
        List<Book> result = bookService.getBooksByAuthor(authorId);
        return ResponseEntity.ok(result);
    }

	@Secured("ROLE_ADMIN")
	@DeleteMapping("/delete-author-from-book/{bookId}/{authorId}")
	public ResponseEntity<Book> deleteAuthorFromBook(@PathVariable String bookId, @PathVariable String authorId){
        logger.info("In deleteAuthorFromBook method.");
		Book result = bookService.deleteAuthorFromBook(bookId, authorId);
		return ResponseEntity.ok(result);
	}

	/**
	 * @param pageNumber
	 * @param pageSize
	 * @return books which have any rate
	 */
	@Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/rate-exists")
    public ResponseEntity<List<Book>> rateExists(@RequestParam int pageNumber, @RequestParam int pageSize){
        logger.info("In rateExists method.");
        if (pageNumber < 0 || pageSize < 1){
            throw new PaginationException("Wrong page number or size");
        }
        List<Book> books = bookService.withRate(new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "rate")));
        return ResponseEntity.ok(books);
    }

	/**
	 * @param rate
	 * @param pageNumber
	 * @param pageSize
	 * @return books with rate
	 */
	@Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/rate")
    public ResponseEntity<List<Book>> rate(@RequestParam double rate, @RequestParam int pageNumber, @RequestParam int pageSize){
        logger.info("In rate method.");
        if (pageNumber < 0 || pageSize < 1){
            throw new PaginationException("Wrong page number or size");
        }
        if (rate < 1 || rate > 5){
            throw new RateOutOfBoundException("Rate cannot be " + rate);
        }
        List<Book> books = bookService.withRate(rate, new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "rate")));
        return ResponseEntity.ok(books);
    }

	/**
	 * @param id book id to which rate will be given
	 * @param rate
	 * @return
	 */
	@Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PutMapping("/give-rate/{id}")
    public ResponseEntity<Book> giveRate(@PathVariable String id, @RequestParam int rate){
        logger.info("In giveRate method.");
        if (rate < 1 || rate > 5){
            throw new RateOutOfBoundException("Rate cannot be " + rate);
        }
        Book result = bookService.giveRate(id, rate);
        return ResponseEntity.ok(result);
    }

	/**
	 * @param file cover of the book
	 * @param id book id to which cover will be given
	 * @return cover id
	 */
	@Secured("ROLE_ADMIN")
    @PostMapping(value = "/upload-cover/{id}")
    public ResponseEntity<String> uploadCover(@RequestParam MultipartFile file, @PathVariable String id){
        logger.info("In uploadCover method.");
        String bookCover = bookService.uploadBookCover(file, id);
        return ResponseEntity.ok(bookCover);
    }

	/**
	 * @param id book id
	 * @return
	 * @throws IOException
	 */
	@Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping(value = "/get-cover/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<Resource> getCover(@PathVariable String id) throws IOException {
        logger.info("In getCover method.");
        Resource bookCover = bookService.getBookCover(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + bookCover.getFilename() + "\"")
                .contentLength(bookCover.contentLength())
                .contentType(MediaType.IMAGE_PNG)
                .body(bookCover);
    }

	/**
	 * @param id cover id
	 * @return
	 */
	@Secured("ROLE_ADMIN")
    @DeleteMapping("/delete-cover/{id}")
    public ResponseEntity deleteCover(@PathVariable String id) {
        bookService.deleteBookCover(id);
        return ResponseEntity.ok().build();
    }

	/**
	 * @param file content of the book
	 * @param id book id to which content will be given
	 * @return content id
	 */
	@Secured("ROLE_ADMIN")
    @PostMapping("/upload-content/{id}")
    public ResponseEntity<String> uploadContent(@RequestParam MultipartFile file, @PathVariable String id) {
        String bookContent = bookService.uploadBookContent(file, id);
        return ResponseEntity.ok(bookContent);
    }

	/**
	 * @param id book id
	 * @return
	 * @throws IOException
	 */
	@Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping(value = "/get-content/{id}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Resource> getContent(@PathVariable String id) throws IOException{
        Resource bookContent = bookService.getBookContent(id);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + bookContent.getFilename() + "\"")
                .contentLength(bookContent.contentLength())
                .contentType(MediaType.TEXT_PLAIN)
                .cacheControl(CacheControl.noCache())
                .body(bookContent);
    }

	/**
	 * @param id content id
	 * @return
	 */
	@Secured("ROLE_ADMIN")
    @DeleteMapping("/delete-content/{id}")
    public ResponseEntity deleteContent(@PathVariable String id) {
        bookService.deleteBookContent(id);
        return ResponseEntity.ok().build();
    }

}
