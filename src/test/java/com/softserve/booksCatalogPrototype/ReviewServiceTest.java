
package com.softserve.booksCatalogPrototype;

import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import com.softserve.booksCatalogPrototype.service.BookServiceImpl;
import com.softserve.booksCatalogPrototype.service.ReviewServiceImpl;
import com.softserve.booksCatalogPrototype.util.GetObjects;


@RunWith(MockitoJUnitRunner.class)
public class ReviewServiceTest {


    @Mock
    private BookServiceImpl bookService;

    @InjectMocks
    ReviewServiceImpl reviewService;

    @After
    public void tearDown() throws Exception {
        bookService = null;
    }

    @Test
    public void getAllReviews() {
        when(bookService.get("id")).thenReturn(GetObjects.getBook(GetObjects.getFilledReview()));
        Assert.assertEquals(1, reviewService.getAllReviews("id").size());
    }
}

