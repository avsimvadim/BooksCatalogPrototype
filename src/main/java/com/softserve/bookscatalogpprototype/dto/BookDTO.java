package com.softserve.bookscatalogpprototype.dto;


import com.softserve.bookscatalogpprototype.annotations.CascadeSave;
import com.softserve.bookscatalogpprototype.model.Author;
import com.softserve.bookscatalogpprototype.model.Publisher;
import com.softserve.bookscatalogpprototype.model.Review;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.ArrayList;
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

    private List<String> authorsId;

    private List<String> reviewsId;
}
