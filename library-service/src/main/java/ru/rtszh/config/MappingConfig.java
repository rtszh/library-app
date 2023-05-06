package ru.rtszh.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.rtszh.domain.Comment;
import ru.rtszh.dto.CommentDto;

@Configuration
public class MappingConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        PropertyMap<CommentDto, Comment> toCommentMap = new PropertyMap<>() {
            @Override
            protected void configure() {
                map().setId(null);
            }
        };

        mapper.addMappings(toCommentMap);
        return mapper;
    }

}
