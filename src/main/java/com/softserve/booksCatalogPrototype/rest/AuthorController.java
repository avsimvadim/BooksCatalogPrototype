package com.softserve.booksCatalogPrototype.rest;

import com.softserve.booksCatalogPrototype.dto.AuthorDTO;
import com.softserve.booksCatalogPrototype.exception.EntityException;
import com.softserve.booksCatalogPrototype.exception.PaginationException;
import com.softserve.booksCatalogPrototype.model.Author;
import com.softserve.booksCatalogPrototype.service.impl.AuthorService;
import com.softserve.booksCatalogPrototype.util.DTOConverter;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<AuthorDTO> add(@RequestBody AuthorDTO authorDTO) {
        if (!(authorDTO instanceof AuthorDTO)){
            throw new EntityException("is not author entity");
        }
        Author author = DTOConverter.convertAuthorDTOToAuthorRequest(authorDTO);
        Author result = authorService.save(author);
        AuthorDTO dtoResult = DTOConverter.convertAuthorToAuthorDTOResponse(result);
        return ResponseEntity.ok(dtoResult);
    }

    @GetMapping("/all")
    public ResponseEntity<List<AuthorDTO>> all() {
        List<Author> result = authorService.getAll();
        List<AuthorDTO> authorDTOS = DTOConverter.convertAuthorListToAuthorDTOListResponse(result);
        return ResponseEntity.ok(authorDTOS);
    }

    @GetMapping("/all_pagination")
    public ResponseEntity<List<AuthorDTO>> allPages(@RequestParam int pageNumber, @RequestParam int pageSize) {
        if (pageNumber < 0 || pageSize <= 0){
            throw new PaginationException("wrong page number or size");
        }
        Stream<Author> stream = authorService.getAll(new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.ASC, "firstName")));
        List<Author> authors = stream.collect(Collectors.toList());
        List<AuthorDTO> authorDTOS = DTOConverter.convertAuthorListToAuthorDTOListResponse(authors);
        return ResponseEntity.ok(authorDTOS);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<AuthorDTO> get(@PathVariable String id){
        Author author = authorService.get(id);
        AuthorDTO authorDTO = DTOConverter.convertAuthorToAuthorDTOResponse(author);
        return ResponseEntity.ok(authorDTO);
    }

    @PutMapping("/update")
    public ResponseEntity<AuthorDTO> update(@RequestBody Author author){
        Author result = authorService.update(author);
        AuthorDTO authorDTO = DTOConverter.convertAuthorToAuthorDTOResponse(author);
        return ResponseEntity.ok(authorDTO);
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
