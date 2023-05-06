package ru.rtszh.forms;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BookForm {
    String title;
    String authors;
    String genres;
}
