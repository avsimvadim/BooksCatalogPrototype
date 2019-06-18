package com.softserve.bookscatalogpprototype.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class Review {

    private String commenterName;

    private String comment;

    private Review response;

    private int rating;

    private Date createDate;

}
