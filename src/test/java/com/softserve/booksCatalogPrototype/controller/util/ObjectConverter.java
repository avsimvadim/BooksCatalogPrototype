package com.softserve.booksCatalogPrototype.controller.util;

import com.google.gson.Gson;
import com.softserve.booksCatalogPrototype.dto.BookDTO;
import com.softserve.booksCatalogPrototype.model.Book;
import com.softserve.booksCatalogPrototype.model.Publisher;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;

public class ObjectConverter {

    public static String bookToJson(ResponseEntity<Book> response){
        Gson json = new Gson();
        return json.toJson(response.getBody(), Book.class);
    }

    public static String bookToJson(Book book){
        Gson json = new Gson();
        return json.toJson(book, Book.class);
    }

    public static String getJsonBook(String name, Date yearPublished, Publisher publisher, List<String> authorsId) {
        BookDTO bookDTO = new BookDTO(name, yearPublished, publisher, authorsId);
        Gson json = new Gson();
        return json.toJson(bookDTO, BookDTO.class);
    }

}
