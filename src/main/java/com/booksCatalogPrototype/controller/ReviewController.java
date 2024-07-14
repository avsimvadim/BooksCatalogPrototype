package com.booksCatalogPrototype.controller;

import java.util.List;

import com.booksCatalogPrototype.dto.ReviewDTO;
import com.booksCatalogPrototype.model.Review;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.booksCatalogPrototype.service.ReviewServiceImpl;

@RestController
@RequestMapping("/api/review")
public class ReviewController {

    private final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private ReviewServiceImpl reviewService;

    @Autowired
    public ReviewController(ReviewServiceImpl reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/add-review/{bookId}")
    public ResponseEntity<Review> addReview(@PathVariable String bookId, @RequestBody ReviewDTO reviewDTO){
        logger.info("In addReview method.");
        Review result = reviewService.save(bookId, reviewDTO);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/add-response/{parentReviewId}")
    public ResponseEntity<Review> addResponse(@PathVariable String parentReviewId, @RequestBody ReviewDTO responseDTO){
        logger.info("In addResponse method.");
        Review response = reviewService.saveResponse(parentReviewId, responseDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Review> get(@PathVariable("id") String reviewId){
        logger.info("In addResponse method.");
        Review review = reviewService.get(reviewId);
        return ResponseEntity.ok(review);
    }

	/**
	 * @param bookId book id
	 * @return all reviews of book
	 */
    @GetMapping("/all-reviews/{bookId}")
    public ResponseEntity<List<Review>> allReviews(@PathVariable String bookId){
        logger.info("In allReviews method.");
        List<Review> reviews = reviewService.getAllReviews(bookId);
        return ResponseEntity.ok(reviews);
    }

	/**
	 * @param reviewId review id
	 * @return response of review
	 */
    @GetMapping("/all-responses/{reviewId}")
    public ResponseEntity<List<Review>> allResponses(@PathVariable String reviewId){
        logger.info("In allResponses method.");
        List<Review> responses = reviewService.getAllResponses(reviewId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/update")
    public ResponseEntity<Review> update(@RequestBody Review review){
        logger.info("In update method.");
        Review result = reviewService.update(review);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/delete-review/{reviewId}")
    public ResponseEntity deleteReview(@PathVariable String reviewId){
        logger.info("In deleteReview method.");
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete-response/{responseId}")
    public ResponseEntity deleteResponse(@PathVariable String responseId){
        logger.info("In deleteResponse method.");
        reviewService.deleteResponse(responseId);
        return ResponseEntity.ok().build();
    }
}
