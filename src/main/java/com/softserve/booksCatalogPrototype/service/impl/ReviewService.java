package com.softserve.booksCatalogPrototype.service.impl;

import java.util.List;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.softserve.booksCatalogPrototype.dto.ReviewDTO;
import com.softserve.booksCatalogPrototype.exception.custom.ReviewException;
import com.softserve.booksCatalogPrototype.model.Book;
import com.softserve.booksCatalogPrototype.model.Review;
import com.softserve.booksCatalogPrototype.repository.ReviewRepository;
import com.softserve.booksCatalogPrototype.service.ReviewServiceInterface;
import com.softserve.booksCatalogPrototype.util.DTOConverter;

@Service
public class ReviewService implements ReviewServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);

    private ReviewRepository reviewRepository;

    private BookService bookService;

    private MongoOperations mongoOperations;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, BookService bookService, MongoOperations mongoOperations) {
        this.reviewRepository = reviewRepository;
        this.bookService = bookService;
        this.mongoOperations = mongoOperations;
    }

    @Override
    public Review save(String bookId, ReviewDTO reviewDTO) {
        Review review = DTOConverter.convertReviewDTOToReview(reviewDTO);
        Review result = reviewRepository.save(review);

        Book book = bookService.get(bookId);
        book.getReviews().add(result);
        bookService.save(book);
        logger.info("Review " + review.toString() + " is saved");
        return result;
    }

    public Review saveResponse(String parentId, ReviewDTO responseDTO){
        Review review = reviewRepository.findById(parentId).orElseThrow(() -> new ReviewException("Did not find the review with such id"));

        Review response = DTOConverter.convertReviewDTOToReview(responseDTO);
        Review saved = reviewRepository.save(response);

        review.getResponses().add(saved);
        reviewRepository.save(review);
        logger.info("Response " + response.toString() + " is saved");
        return saved;
    }

    @Override
    public List<Review> getAllReviews(String id) {
        Book book = bookService.get(id);
        List<Review> reviews = book.getReviews();
        return reviews;
    }

    public List<Review> getAllResponses(String id) {
        Review review = reviewRepository.findById(id).orElseThrow(() -> new ReviewException("Did not find the review with such id"));
        List<Review> responses = review.getResponses();
        return responses;
    }

    @Override
    public Review get(String id) {
        Review result = reviewRepository.findById(id).orElseThrow(() -> new ReviewException("Did not find the review with such id"));
        return result;
    }

    @Override
    public void deleteReview(String reviewId) {
        Review review = this.get(reviewId);
        Book book = bookService.findBookWithReview(review);
        book.getReviews().remove(review);
        reviewRepository.delete(review);
        bookService.save(book);
        logger.info("Review with id " + reviewId + " is deleted");
    }

    public void deleteResponse(String responseId){
        Review response = reviewRepository.findById(responseId).orElseThrow(() -> new ReviewException("Did not find the response with such id"));
        reviewRepository.delete(response);
        logger.info("Response with id " + responseId + " is deleted");
    }

    @Override
    public Review update(Review newReview) {
        Supplier<ReviewException> supplier = () -> new ReviewException("Did not find the review with such id");
        reviewRepository.findById(newReview.getId()).orElseThrow(supplier);

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(newReview.getId()));
        query.fields().include("_id");

        Update update = new Update();
        update.set("commenterName", newReview.getCommenterName());
        update.set("comment", newReview.getComment());

        mongoOperations.updateFirst(query, update, Review.class);
        Review result = reviewRepository.findById(newReview.getId()).orElseThrow(supplier);
        return result;
    }

}
