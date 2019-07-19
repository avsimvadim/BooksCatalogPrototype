package com.softserve.booksCatalogPrototype.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softserve.booksCatalogPrototype.dto.ReviewDTO;
import com.softserve.booksCatalogPrototype.model.Review;
import com.softserve.booksCatalogPrototype.service.impl.ReviewService;

@RestController
@RequestMapping("/api/review")
public class ReviewController {

    private ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/add-review/{bookId}")
    public ResponseEntity<Review> addReview(@PathVariable String bookId, @RequestBody ReviewDTO reviewDTO){
        Review result = reviewService.save(bookId, reviewDTO);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/add-response/{parentReviewId}")
    public ResponseEntity<Review> addResponse(@PathVariable String parentReviewId, @RequestBody ReviewDTO responseDTO){
        Review response = reviewService.saveResponse(parentReviewId, responseDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Review> get(@PathVariable("id") String reviewId){
        Review review = reviewService.get(reviewId);
        return ResponseEntity.ok(review);
    }

	/**
	 * @param bookId book id
	 * @return all reviews of book
	 */
    @GetMapping("/all-reviews/{bookId}")
    public ResponseEntity<List<Review>> allReviews(@PathVariable String bookId){
        List<Review> reviews = reviewService.getAllReviews(bookId);
        return ResponseEntity.ok(reviews);
    }

	/**
	 * @param reviewId review id
	 * @return response of review
	 */
    @GetMapping("/all-responses/{reviewId}")
    public ResponseEntity<List<Review>> allResponses(@PathVariable String reviewId){
        List<Review> responses = reviewService.getAllResponses(reviewId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/update")
    public ResponseEntity<Review> update(@RequestBody Review review){
        Review result = reviewService.update(review);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/delete-review/{reviewId}")
    public ResponseEntity deleteReview(@PathVariable String reviewId){
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete-response/{responseId}")
    public ResponseEntity deleteResponse(@PathVariable String responseId){
        reviewService.deleteResponse(responseId);
        return ResponseEntity.ok().build();
    }
}
