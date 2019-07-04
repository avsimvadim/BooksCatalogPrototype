package com.softserve.booksCatalogPrototype.util;

import com.softserve.booksCatalogPrototype.dto.AuthorDTO;
import com.softserve.booksCatalogPrototype.dto.ReviewDTO;
import com.softserve.booksCatalogPrototype.exception.custom.AuthorException;
import com.softserve.booksCatalogPrototype.model.Author;
import com.softserve.booksCatalogPrototype.dto.BookDTO;
import com.softserve.booksCatalogPrototype.model.Book;
import com.softserve.booksCatalogPrototype.model.Publisher;
import com.softserve.booksCatalogPrototype.model.Review;
import com.softserve.booksCatalogPrototype.service.impl.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DTOConverter {

    private static AuthorService authorService;

    @Autowired
    private DTOConverter(AuthorService authorService) {
        this.authorService = authorService;
    }

    public static Book convertBook(BookDTO bookDTO) {
        return new Book(bookDTO.getName(), bookDTO.getYearPublished(), bookDTO.getPublisher(), convertAuthorsIdToAuthors(bookDTO.getAuthorsId()));
    }

    public static List<Author> convertAuthorsIdToAuthors(List<String> authorsId) {
        if (authorsId == null) {
            return null;
        }
        return authorsId.stream()
                .map(id -> authorService.get(id))
                .collect(Collectors.toList());
    }

    public static Author convertAuthorDTOToAuthor(AuthorDTO authorDTO) {
        return new Author(authorDTO.getFirstName(), authorDTO.getSecondName());
    }

    public static Review convertReviewDTOToReview(ReviewDTO reviewDTO) {
        return new Review(reviewDTO.getCommenterName(), reviewDTO.getComment());
    }

}
