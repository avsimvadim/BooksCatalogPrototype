package com.softserve.booksCatalogPrototype.dto;

import com.softserve.booksCatalogPrototype.model.Publisher;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class BookDTO {

    private String name;

    private Date yearPublished;

    private Publisher publisher;

    private List<String> authorsId = new ArrayList<>();

}
