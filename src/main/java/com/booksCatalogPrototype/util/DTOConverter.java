package com.booksCatalogPrototype.util;

import com.booksCatalogPrototype.dto.*;
import com.booksCatalogPrototype.model.Review;
import com.booksCatalogPrototype.model.User;
import com.booksCatalogPrototype.model.Author;
import com.booksCatalogPrototype.model.Book;
import com.booksCatalogPrototype.service.AuthorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DTOConverter {

    private static AuthorServiceImpl authorService;

    @Autowired
    private DTOConverter(AuthorServiceImpl authorService) {
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

    public static User convertAdminDTOToUser(AdminDTO adminDTO){
        return new User(adminDTO.getName(), adminDTO.getUsername(), adminDTO.getEmail(), adminDTO.getPassword());
    }

    public static User convertSignUpRequestToUser(SignUpRequest signUpRequest){
        return new User(signUpRequest.getName(), signUpRequest.getUsername(), signUpRequest.getEmail(), signUpRequest.getPassword());
    }

}
