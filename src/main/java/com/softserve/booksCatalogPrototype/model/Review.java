package com.softserve.booksCatalogPrototype.model;

import com.softserve.booksCatalogPrototype.annotations.CascadeDelete;
import com.softserve.booksCatalogPrototype.annotations.CascadeSave;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
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
    private String id;

    private String commenterName;

    private String comment;

    @DBRef
    @CascadeSave
    @CascadeDelete
    private List<Review> responses = new LinkedList<>();

    @CreatedDate
    private Date creationDate;

    public Review(String commenterName, String comment) {
        this.commenterName = commenterName;
        this.comment = comment;
    }
}
