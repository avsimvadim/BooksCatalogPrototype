package com.softserve.booksCatalogPrototype.service.impl;

import com.softserve.booksCatalogPrototype.dto.ReviewDTO;
import com.softserve.booksCatalogPrototype.exception.ReviewIsNotFoundException;
import com.softserve.booksCatalogPrototype.model.Book;
import com.softserve.booksCatalogPrototype.model.Review;
import com.softserve.booksCatalogPrototype.repository.ReviewRepository;
import com.softserve.booksCatalogPrototype.service.GeneralDao;
import com.softserve.booksCatalogPrototype.util.DTOConverter;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BookService bookService;

    public String save(String bookId, ReviewDTO reviewDTO) {
        Review review = DTOConverter.convertReviewDTOToReview(reviewDTO);
        Review savedReview = reviewRepository.save(review);

        Book book = bookService.get(bookId);
        Review result = reviewRepository.findById(savedReview.getId()).get();
        book.getReviews().add(result);
        bookService.save(book);
        return savedReview.getId().toString();
    }

    public List<Review> getAll() {
        return null;
    }

    public Review get(String id) {
        Optional<Review> result = reviewRepository.findById(new ObjectId(id));
        if (!result.isPresent()){
            throw new ReviewIsNotFoundException("no review with such id");
        }
        return result.get();
    }

    public void delete(Review object) {

    }

    public Review update(Review object) {
        return null;
    }

    public Review delete(List<Review> reviews){
        return null;
    }
}
