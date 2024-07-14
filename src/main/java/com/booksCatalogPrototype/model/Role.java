package com.booksCatalogPrototype.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
public class Role {

    @Id
    private String id;

    private RoleName name;


    public Role(RoleName name) {
        this.name = name;
    }
}
