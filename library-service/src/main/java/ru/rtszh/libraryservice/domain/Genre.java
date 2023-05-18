package ru.rtszh.libraryservice.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Document
public class Genre {
    @Id
    private String id;

    private String name;
}
