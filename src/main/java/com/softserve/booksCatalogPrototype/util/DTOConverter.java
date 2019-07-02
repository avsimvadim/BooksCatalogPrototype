package com.softserve.booksCatalogPrototype.util;

import com.softserve.booksCatalogPrototype.dto.AuthorDTO;
import com.softserve.booksCatalogPrototype.dto.ReviewDTO;
import com.softserve.booksCatalogPrototype.exception.EntityException;
import com.softserve.booksCatalogPrototype.model.Author;
import com.softserve.booksCatalogPrototype.dto.BookDTO;
import com.softserve.booksCatalogPrototype.model.Book;
import com.softserve.booksCatalogPrototype.model.Review;
import com.softserve.booksCatalogPrototype.service.impl.AuthorService;
import com.softserve.booksCatalogPrototype.service.impl.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DTOConverter {

    private static AuthorService authorService;

    @Autowired
    private DTOConverter(AuthorService authorService){
        this.authorService = authorService;
    }

    public static BookDTO convertBook(Book book){
        return new BookDTO(book.getIsbn().toString(), book.getName(), book.getYearPublished(),
                book.getPublisher(), book.getCreationDate(), book.getRate(),convertAuthorsId(book.getAuthors()), convertReviewsId(book.getReviews()));
    }

    public static Book convertBook(BookDTO bookDTO){
        return new Book(bookDTO.getName(), bookDTO.getYearPublished(), bookDTO.getPublisher(),
                bookDTO.getCreationDate(), bookDTO.getRate(),
                convertAuthorsIdToAuthors(bookDTO.getAuthorsId()), null);
    }

    public static List<String> convertAuthorsId(List<Author> authors){
        if (authors == null){
            return null;
        }
        return authors.stream()
                .map(author -> author.getId().toString())
                .collect(Collectors.toList());
    }

    public static List<Author> convertAuthorsIdToAuthors(List<String> authorsId){
        if (authorsId == null){
            return null;
        }
        return authorsId.stream()
                .map(id -> {
                    try {
                        Author author = authorService.get(id);
                        if (author == null){
                            throw new EntityException("inappropriate author's id, operation was not finished");
                        }
                        return author;
                    }catch (Exception e){
                        throw new EntityException("inappropriate author's id");
                    }
                })
                .collect(Collectors.toList());
    }

    public static List<String> convertReviewsId(List<Review> reviews){
        if (reviews == null){
            return null;
        }
        return reviews.stream()
                .map(review -> review.getId().toString())
                .collect(Collectors.toList());
    }

    public static List<BookDTO> convertBookListToBookDTOList(List<Book> books){
        return books.stream()
                .map(book -> convertBook(book))
                .collect(Collectors.toList());
    }

    public static Author convertAuthorDTOToAuthorRequest(AuthorDTO authorDTO){
        return new Author(authorDTO.getFirstName(), authorDTO.getSecondName(), authorDTO.getCreationDate());
    }

    public static AuthorDTO convertAuthorToAuthorDTOResponse(Author author){
        return new AuthorDTO(author.getId().toString(), author.getFirstName(), author.getSecondName(), author.getCreationDate());
    }

    public static List<AuthorDTO> convertAuthorListToAuthorDTOListResponse(List<Author> authors){
        return authors.stream().map(author -> convertAuthorToAuthorDTOResponse(author)).collect(Collectors.toList());
    }

    public static Review convertReviewDTOToReview(ReviewDTO reviewDTO){
        return new Review(reviewDTO.getCommenterName(), reviewDTO.getComment(), reviewDTO.getCreationDate());
    }

    public static ReviewDTO convertReviewToReviewDTO(Review review){
        return new ReviewDTO(review.getCommenterName(), review.getComment(), review.getCreationDate());
    }
}
