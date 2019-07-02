package com.softserve.booksCatalogPrototype.rest;

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

    @PostMapping("/addReview")
    public ResponseEntity<String> addReview(@RequestParam String bookId, @RequestBody ReviewDTO reviewDTO){
        String result = reviewService.save(bookId, reviewDTO);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/addResponse/{id}")
    public ResponseEntity<String> addResponse(@PathVariable("id") String parentReviewId, @RequestBody Review response){
        return null;
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Review> get(@PathVariable("id") String reviewId){
        Review review = reviewService.get(reviewId);
        return ResponseEntity.ok(review);
    }

    @PutMapping("/update")
    public ResponseEntity<Review> update(@RequestBody Review review){
        return null;
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable("id") String reviewId){
        return null;
    }
}
