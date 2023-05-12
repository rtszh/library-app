package ru.rtszh.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Document
public class Book {
    @Id
    private String id;

    private String title;

    private List<Author> authors;

    private List<Genre> genres;
}
