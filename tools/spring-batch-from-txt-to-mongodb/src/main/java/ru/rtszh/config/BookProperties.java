package ru.rtszh.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "book")
public class BookProperties {
    private String title;

    private List<String> authors;

    private List<String> genres;
}
