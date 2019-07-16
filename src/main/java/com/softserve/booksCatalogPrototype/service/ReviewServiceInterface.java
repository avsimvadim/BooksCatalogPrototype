package com.softserve.booksCatalogPrototype.service;

import com.softserve.booksCatalogPrototype.dto.ReviewDTO;
import com.softserve.booksCatalogPrototype.model.Review;

import java.util.List;

public interface ReviewServiceInterface {
    String save(String bookId, ReviewDTO reviewDTO);

    List<Review> getAllReviews(String id);

    Review get(String id);

    void deleteReview(String reviewId);

    Review update(Review newReview);
}
