package com.softserve.booksCatalogPrototype.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

import com.softserve.booksCatalogPrototype.dto.AuthorDTO;
import com.softserve.booksCatalogPrototype.exception.custom.PaginationException;
import com.softserve.booksCatalogPrototype.model.Author;
import com.softserve.booksCatalogPrototype.service.AuthorServiceImpl;
import com.softserve.booksCatalogPrototype.util.DTOConverter;

@RestController
@RequestMapping("/api/author")
public class AuthorController {

    private final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private AuthorServiceImpl authorService;

    @Autowired
    public AuthorController(AuthorServiceImpl authorService) {
        this.authorService = authorService;
    }

	@Secured("ROLE_ADMIN")
    @PostMapping("/add")
    public ResponseEntity<Author> add(@RequestBody AuthorDTO authorDTO) {
        logger.info("In add method.");
        Author author = DTOConverter.convertAuthorDTOToAuthor(authorDTO);
        Author result = authorService.save(author);
        return ResponseEntity.ok(result);
    }

	@Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/all")
    public ResponseEntity<List<Author>> all() {
        logger.info("In all method.");
        List<Author> result = authorService.getAll();
        return ResponseEntity.ok(result);
    }

	@Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/all-pagination")
    public ResponseEntity<List<Author>> allPages(@RequestParam int pageNumber, @RequestParam int pageSize) {
        logger.info("In allPages method.");
        if (pageNumber < 0 || pageSize <= 0){
            throw new PaginationException("wrong page number or size");
        }
        Page<Author> page = authorService.getAll(new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.ASC, "firstName")));
        List<Author> result = page.getContent();
        return ResponseEntity.ok(result);
    }

	@Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/get/{id}")
    public ResponseEntity<Author> get(@PathVariable String id){
        logger.info("In get method.");
        Author result = authorService.get(id);
        return ResponseEntity.ok(result);
    }

	@Secured("ROLE_ADMIN")
    @PutMapping("/update")
    public ResponseEntity<Author> update(@RequestBody Author author){
        logger.info("In update method.");
        Author result = authorService.update(author);
        return ResponseEntity.ok(result);
    }

	@Secured("ROLE_ADMIN")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable String id){
        logger.info("In delete method.");
        Author author = authorService.get(id);
        authorService.delete(author);
        return ResponseEntity.ok().build();
    }

	@Secured("ROLE_ADMIN")
    @DeleteMapping("/bulk-delete")
    public ResponseEntity deleteAuthors(@RequestParam("id") String... ids){
        logger.info("In deleteAuthors method.");
        authorService.deleteAuthors(ids);
        return ResponseEntity.ok().build();
    }

}
