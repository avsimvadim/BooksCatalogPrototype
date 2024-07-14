package com.booksCatalogPrototype.service;

import java.util.List;

import com.booksCatalogPrototype.dto.ReviewDTO;
import com.booksCatalogPrototype.model.Review;

public interface ReviewService {
    Review save(String bookId, ReviewDTO reviewDTO);

    List<Review> getAllReviews(String id);

    Review get(String id);

    void deleteReview(String reviewId);

    Review update(Review newReview);
}
