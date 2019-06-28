package com.softserve.booksCatalogPrototype.rest;

import com.softserve.booksCatalogPrototype.model.Author;
import com.softserve.booksCatalogPrototype.service.impl.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @PostMapping("/author")
    public boolean createBook(@RequestBody Author author) {
        return true;
    }
}
