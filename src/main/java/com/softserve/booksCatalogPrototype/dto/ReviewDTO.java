package com.softserve.booksCatalogPrototype.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReviewDTO {

    private String commenterName;

    private String comment;

    private Date creationDate;

}
