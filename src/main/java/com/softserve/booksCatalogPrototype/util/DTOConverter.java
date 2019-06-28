package com.softserve.booksCatalogPrototype.util;

import com.softserve.booksCatalogPrototype.model.Author;
import com.softserve.booksCatalogPrototype.dto.BookDTO;
import com.softserve.booksCatalogPrototype.model.Book;
import com.softserve.booksCatalogPrototype.model.Review;

import java.util.List;
import java.util.stream.Collectors;

public class DTOConverter {

    public static BookDTO convertBook(Book book){
        return new BookDTO(book.getIsbn().toString(), book.getName(), book.getYearPublished(), book.getPublisher(), book.getCreationDate(), book.getRate(),convertAuthorsId(book.getAuthors()), convertReviewsId(book.getReviews()));
    }

    public static List<String> convertAuthorsId(List<Author> authors){
        return authors.stream().map(author -> author.getId().toString()).collect(Collectors.toList());
    }

    public static List<String> convertReviewsId(List<Review> reviews){
        return reviews.stream().map(review -> review.getId().toString()).collect(Collectors.toList());
    }

    public static List<BookDTO> convertBookListToBookDTOList(List<Book> books){
        return books.stream().map(book -> convertBook(book)).collect(Collectors.toList());
    }

}
