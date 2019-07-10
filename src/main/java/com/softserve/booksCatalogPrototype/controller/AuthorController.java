package com.softserve.booksCatalogPrototype.controller;

import com.softserve.booksCatalogPrototype.dto.AuthorDTO;
import com.softserve.booksCatalogPrototype.exception.custom.PaginationException;
import com.softserve.booksCatalogPrototype.model.Author;
import com.softserve.booksCatalogPrototype.service.impl.AuthorService;
import com.softserve.booksCatalogPrototype.util.DTOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/author")
public class AuthorController {

    private AuthorService authorService;

    @Autowired
    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @PostMapping("/add")
    public ResponseEntity<Author> add(@RequestBody AuthorDTO authorDTO) {
        Author author = DTOConverter.convertAuthorDTOToAuthor(authorDTO);
        Author result = authorService.save(author);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Author>> all() {
        List<Author> result = authorService.getAll();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all_pagination")
    public ResponseEntity<List<Author>> allPages(@RequestParam int pageNumber, @RequestParam int pageSize) {
        if (pageNumber < 0 || pageSize <= 0){
            throw new PaginationException("wrong page number or size");
        }
        Page<Author> page = authorService.getAll(new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.ASC, "firstName")));
        List<Author> result = page.getContent();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Author> get(@PathVariable String id){
        Author result = authorService.get(id);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/update")
    public ResponseEntity<Author> update(@RequestBody Author author){
        Author result = authorService.update(author);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable String id){
        Author author = authorService.get(id);
        authorService.delete(author);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/bulk_delete")
    public ResponseEntity deleteAuthors(@RequestParam("id") String... ids){
        authorService.deleteAuthors(ids);
        return ResponseEntity.ok().build();
    }

}
