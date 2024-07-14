package com.booksCatalogPrototype.controller.util;

import java.util.Date;
import java.util.List;

import com.booksCatalogPrototype.dto.AuthorDTO;
import com.booksCatalogPrototype.dto.BookDTO;
import com.booksCatalogPrototype.dto.ReviewDTO;
import com.booksCatalogPrototype.model.Author;
import com.booksCatalogPrototype.model.Book;
import com.booksCatalogPrototype.model.Publisher;
import com.booksCatalogPrototype.model.Review;
import org.springframework.http.ResponseEntity;

import com.google.gson.Gson;

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

	public static String authorToJson(ResponseEntity<Author> response){
		Gson json = new Gson();
		return json.toJson(response.getBody(), Author.class);
	}

	public static String getJsonAuthor(String firstName, String secondName) {
		AuthorDTO authorDTO = new AuthorDTO(firstName, secondName);
		Gson json = new Gson();
		return json.toJson(authorDTO, AuthorDTO.class);
	}

	public static String reviewToJson(ResponseEntity<Review> response){
		Gson json = new Gson();
		return json.toJson(response.getBody(), Review.class);
	}

	public static String getJsonReview(String commenterName, String comment) {
		ReviewDTO reviewDTO = new ReviewDTO(commenterName, comment);
		Gson json = new Gson();
		return json.toJson(reviewDTO, ReviewDTO.class);
	}

}
