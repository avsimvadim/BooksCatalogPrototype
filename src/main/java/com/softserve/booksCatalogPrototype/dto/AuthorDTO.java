package com.softserve.booksCatalogPrototype.dto;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AuthorDTO {

    private String id;

    private String firstName;

    private String secondName;

    private Date creationDate;

}
