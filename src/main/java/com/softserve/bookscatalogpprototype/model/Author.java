package com.softserve.bookscatalogpprototype.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@Getter
@Setter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = "isbn")
@ToString
public class Author {

    @Id
    private ObjectId id;

    private final String firstName;

    private final String secondName;

    private final Date creationDate;

}
