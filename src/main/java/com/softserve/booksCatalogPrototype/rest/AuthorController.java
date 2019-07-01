package com.softserve.booksCatalogPrototype.rest;

import com.softserve.booksCatalogPrototype.exception.EntityException;
import com.softserve.booksCatalogPrototype.exception.PaginationException;
import com.softserve.booksCatalogPrototype.model.Author;
import com.softserve.booksCatalogPrototype.service.impl.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/author")
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @PostMapping("/add")
    public ResponseEntity<Author> add(@RequestBody Author author) {
        if (!(author instanceof Author)){
            throw new EntityException("is not author entity");
        }
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
        Stream<Author> stream = authorService.getAll(new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.ASC, "firstName")));
        List<Author> authors = stream.collect(Collectors.toList());
        return ResponseEntity.ok(authors);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Author> get(@PathVariable String id){
        Author author = authorService.get(id);
        return ResponseEntity.ok(author);
    }

    @PutMapping("/update")
    public ResponseEntity<Author> update(@RequestBody Author author){
        Author result = authorService.update(author);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable String id){
        authorService.delete(authorService.get(id));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/bulk_delete")
    public ResponseEntity deleteAuthors(@RequestParam("id") String[] ids){
        authorService.deleteAuthors(ids);
        return ResponseEntity.ok().build();
    }

}
