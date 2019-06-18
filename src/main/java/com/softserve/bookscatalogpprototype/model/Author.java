package com.softserve.bookscatalogpprototype.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class Author {

    private String firstName;

    private String secondName;

    private Date createDate;
}
