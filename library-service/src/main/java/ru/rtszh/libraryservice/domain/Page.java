package ru.rtszh.libraryservice.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Document
public class Page {
    @Id
    private String id;

    private String text;

    private int chapter;

    private int pageNumber;

    @DBRef
    private Book bookId;
}
