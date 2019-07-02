package com.softserve.booksCatalogPrototype.model;

import com.softserve.booksCatalogPrototype.annotations.CascadeSave;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
    private List<Review> responses = new LinkedList<>();

    private Date creationDate;

    public Review(String commenterName, String comment, Date creationDate) {
        this.commenterName = commenterName;
        this.comment = comment;
        this.creationDate = creationDate;
    }
}
