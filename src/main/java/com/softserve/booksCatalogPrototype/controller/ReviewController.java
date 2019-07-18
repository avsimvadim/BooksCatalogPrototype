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

    @PostMapping("/add_review/{id}")
    public ResponseEntity<Review> addReview(@PathVariable("id") String bookId, @RequestBody ReviewDTO reviewDTO){
        Review result = reviewService.save(bookId, reviewDTO);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/add_response/{id}")
    public ResponseEntity<Review> addResponse(@PathVariable("id") String parentReviewId, @RequestBody ReviewDTO responseDTO){
        Review response = reviewService.saveResponse(parentReviewId, responseDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Review> get(@PathVariable("id") String reviewId){
        Review review = reviewService.get(reviewId);
        return ResponseEntity.ok(review);
    }

	/**
	 * @param id book id
	 * @return all reviews of book
	 */
    @GetMapping("/all_reviews/{id}")
    public ResponseEntity<List<Review>> allReviews(@PathVariable String id){
        List<Review> reviews = reviewService.getAllReviews(id);
        return ResponseEntity.ok(reviews);
    }

	/**
	 * @param id review id
	 * @return response of review
	 */
    @GetMapping("/all_responses/{id}")
    public ResponseEntity<List<Review>> allResponses(@PathVariable String id){
        List<Review> responses = reviewService.getAllResponses(id);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/update")
    public ResponseEntity<Review> update(@RequestBody Review review){
        Review result = reviewService.update(review);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/delete_review/{id}")
    public ResponseEntity deleteReview(@PathVariable("id") String reviewId){
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete_response/{id}")
    public ResponseEntity deleteResponse(@PathVariable("id") String responseId){
        reviewService.deleteResponse(responseId);
        return ResponseEntity.ok().build();
    }
}
