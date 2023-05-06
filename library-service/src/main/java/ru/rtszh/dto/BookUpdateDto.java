package ru.rtszh.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookUpdateDto {
    String id;
    String title;
    @JsonProperty("authors")
    List<AuthorDto> authorsDto;
    @JsonProperty("genres")
    List<GenreDto> genresDto;
    @Nullable
    List<CommentDto> commentsDto;
}