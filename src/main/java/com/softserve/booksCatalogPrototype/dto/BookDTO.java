package com.softserve.booksCatalogPrototype.dto;

import com.softserve.booksCatalogPrototype.model.Publisher;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
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

    private List<String> reviewsId = new LinkedList<>();
}
