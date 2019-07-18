package com.softserve.booksCatalogPrototype.unit;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.MongoOperations;

import com.softserve.booksCatalogPrototype.dto.ReviewDTO;
import com.softserve.booksCatalogPrototype.model.Book;
import com.softserve.booksCatalogPrototype.model.Review;
import com.softserve.booksCatalogPrototype.repository.ReviewRepository;
import com.softserve.booksCatalogPrototype.service.impl.BookService;
import com.softserve.booksCatalogPrototype.service.impl.ReviewService;
import com.softserve.booksCatalogPrototype.unit.util.GetObjects;


@RunWith(MockitoJUnitRunner.class)
public class ReviewServiceTests {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookService bookService;

    @Mock
    private MongoOperations mongoOperations;

    @InjectMocks
    ReviewService reviewService;

    @After
    public void tearDown() throws Exception {
        reviewRepository = null;
        bookService = null;
        mongoOperations = null;
    }

    @Test
    public void saveTest() {
        ReviewDTO reviewDTO = new ReviewDTO("name", "comment");
        Review review = GetObjects.getReview("name", "comment");
        Book book = GetObjects.getBook("1");
        when(reviewRepository.save(review)).thenReturn(new Review("1","name", "comment", null, new Date()));
        when(bookService.get(book.getIsbn())).thenReturn(book);
        Assert.assertEquals(book.getIsbn(), reviewService.save(book.getIsbn(), reviewDTO).getId());
    }

    @Test
    public void saveResponse() {
        Review response = new Review("2", "responser", "response", null, new Date());
        Review review = new Review("1", null, null, new ArrayList<>(), new Date());
        when(reviewRepository.findById("1")).thenReturn(Optional.of(review));
        when(reviewRepository.save(new Review("responser", "response"))).thenReturn(response);
        Assert.assertEquals("2" ,reviewService.saveResponse("1", new ReviewDTO("responser", "response")).getId());
    }

    @Test
    public void getAllReviews() {
        when(bookService.get("id")).thenReturn(GetObjects.getBook(GetObjects.getFilledReview()));
        Assert.assertEquals(1, reviewService.getAllReviews("id").size());
    }

    @Test
    public void getAllResponses() {
        List<Review> responses = Arrays.asList(GetObjects.getFilledReview(), GetObjects.getFilledReview());
        when(reviewRepository.findById("id")).thenReturn(Optional.of(new Review("1", null, null, responses, null)));
        Assert.assertEquals(2, reviewService.getAllResponses("id").size());
    }

    @Test
    public void get() {
        when(reviewRepository.findById("id")).thenReturn(Optional.of(GetObjects.getFilledReview()));
        reviewService.get("id");
        verify(reviewRepository, times(1)).findById(any());
    }

    @Test
    public void deleteReview() {
        when(reviewRepository.findById("id")).thenReturn(Optional.of(GetObjects.getFilledReview()));
        List<Review> reviews = new ArrayList<>();
        reviews.add(GetObjects.getFilledReview());
        when(bookService.findBookWithReview(GetObjects.getFilledReview()))
                .thenReturn(new Book("1", null, null, null, null, 0.0, 0, null, reviews));
        reviewService.deleteReview("id");
        verify(bookService, times(1)).save(any());
    }

    @Test
    public void deleteResponse() {
        when(reviewRepository.findById("id")).thenReturn(Optional.of(GetObjects.getFilledReview()));
        reviewService.deleteResponse("id");
        verify(reviewRepository, times(1)).delete(any());
    }

    @Test
    public void update() {
        when(reviewRepository.findById("id")).thenReturn(Optional.of(GetObjects.getFilledReview()));
        reviewService.update(GetObjects.getFilledReview());
        verify(reviewRepository, times(2)).findById(any());
    }
}

