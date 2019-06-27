package com.softserve.bookscatalogpprototype.util;

import com.softserve.bookscatalogpprototype.exception.RateOutOfBoundException;
import com.softserve.bookscatalogpprototype.model.Book;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.Precision;

public class BookRateCheck {
    public static Book rateCheck(Book book) throws RateOutOfBoundException{
        double rate = book.getRate();
        if (rate != 0 && rate >= 1 && rate <= 5) {
            book.setTotalVoteCount(1);
            double newRate = Precision.round(rate,1);
            book.setRate(newRate);
            return book;
        } else if (book.getRate() != 0){
            throw new RateOutOfBoundException("Rate is not from 0 to 5");
        }
        return book;
    }
}
