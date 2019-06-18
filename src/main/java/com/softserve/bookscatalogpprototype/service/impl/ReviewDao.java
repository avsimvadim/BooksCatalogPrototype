package com.softserve.bookscatalogpprototype.service.impl;

import com.softserve.bookscatalogpprototype.model.Review;
import com.softserve.bookscatalogpprototype.service.GeneralDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional

public class ReviewDao implements GeneralDao<Review> {

    @Override
    public boolean save(Review object) {
        return false;
    }

    @Override
    public List<Review> getAll() {
        return null;
    }

    @Override
    public Review get(long isbn) {
        return null;
    }

    @Override
    public void delete(Review object) {

    }
}
