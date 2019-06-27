package com.softserve.bookscatalogpprototype.dto;

import com.softserve.bookscatalogpprototype.model.Publisher;
import lombok.*;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class BookDTO {

    private String isbn;

    private String name;

    private Date yearPublished;

    private Publisher publisher;

    private Date creationDate;

    private double rate;

    private List<String> authorsId;

    private List<String> reviewsId;
}
