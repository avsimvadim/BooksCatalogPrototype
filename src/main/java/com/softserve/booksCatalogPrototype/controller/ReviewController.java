package com.softserve.booksCatalogPrototype.controller;

import com.softserve.booksCatalogPrototype.dto.ReviewDTO;
import com.softserve.booksCatalogPrototype.model.Book;
import com.softserve.booksCatalogPrototype.model.Review;
import com.softserve.booksCatalogPrototype.service.impl.BookService;
import com.softserve.booksCatalogPrototype.service.impl.ReviewService;
import com.softserve.booksCatalogPrototype.util.DTOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/add_review/{id}")
    public ResponseEntity<String> addReview(@PathVariable("id") String bookId, @RequestBody ReviewDTO reviewDTO){
        String result = reviewService.save(bookId, reviewDTO);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/add_response/{id}")
    public ResponseEntity<String> addResponse(@PathVariable("id") String parentReviewId, @RequestBody ReviewDTO responseDTO){
        String responseId = reviewService.saveResponse(parentReviewId, responseDTO);
        return ResponseEntity.ok(responseId);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Review> get(@PathVariable("id") String reviewId){
        Review review = reviewService.get(reviewId);
        return ResponseEntity.ok(review);
    }

    // all reviews of book by book id
    @GetMapping("/all_reviews/{id}")
    public ResponseEntity<List<Review>> allReviews(@PathVariable String id){
        List<Review> reviews = reviewService.getAllReviews(id);
        return ResponseEntity.ok(reviews);
    }

    // review id
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
