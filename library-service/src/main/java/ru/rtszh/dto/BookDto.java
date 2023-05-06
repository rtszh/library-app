package ru.rtszh.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookDto {
    String id;
    String title;
    @JsonProperty("authors")
    List<AuthorDto> authorsDto;
    @JsonProperty("genres")
    List<GenreDto> genresDto;
    @NonNull
    List<CommentDto> commentsDto;

}
