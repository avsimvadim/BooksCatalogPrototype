package com.softserve.booksCatalogPrototype.model;

import com.softserve.booksCatalogPrototype.annotations.CascadeSave;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
public class Review {

    @Id
    private ObjectId id;

    private String commenterName;

    private String comment;

    @DBRef
    @CascadeSave
    private Review response;

    private int rating;

    private Date creationDate;

}
