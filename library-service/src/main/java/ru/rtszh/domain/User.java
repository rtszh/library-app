package ru.rtszh.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Document
public class User {
    @Id
    private String id;

    private String username;

    private int stoppedAtPage;

    //    @Indexed(name = "createdAtIndex", expireAfter = "7d")
//    @Indexed(name = "createdAtIndex", expireAfter = "50s")
    @Indexed(name = "createdAtIndex", expireAfter = "5m")
    private LocalDateTime createdAt;

    @DBRef
    private Book bookId;
}
