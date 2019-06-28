package com.softserve.booksCatalogPrototype.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "isbn")
@ToString
public class Author {

    @Id
    private ObjectId id;

    private String firstName;

    private String secondName;

    private Date creationDate;

    public Author(String firstName, String secondName, Date creationDate) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.creationDate = creationDate;
    }
}
