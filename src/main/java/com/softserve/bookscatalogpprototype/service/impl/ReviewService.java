package com.softserve.bookscatalogpprototype.service.impl;

import com.softserve.bookscatalogpprototype.model.Review;
import com.softserve.bookscatalogpprototype.service.GeneralDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReviewService implements GeneralDao<Review> {

    @Override
    public Review save(Review object) {
        return object;
    }

    @Override
    public List<Review> getAll() {
        return null;
    }

    @Override
    public Review get(String isbn) {
        return null;
    }

    @Override
    public void delete(Review object) {

    }
}
