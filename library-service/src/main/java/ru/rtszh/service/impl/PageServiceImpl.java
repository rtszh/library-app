package ru.rtszh.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.rtszh.domain.Book;
import ru.rtszh.domain.Page;
import ru.rtszh.dto.*;
import ru.rtszh.exceptions.BookNotFoundException;
import ru.rtszh.repository.BookRepository;
import ru.rtszh.repository.PageRepository;
import ru.rtszh.service.PageService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PageServiceImpl implements PageService {

    private final PageRepository pageRepository;
    private final ModelMapper mapper;


    @Override
    public List<PageDto> findPagesByBookId(String id, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        List<Page> pages = pageRepository.findByBookId(id, pageable);

        return toDto(pages);
    }

    @Override
    public int getPagesCount(String id) {
        return pageRepository.countAllByBookId(id);
    }

    private List<PageDto> toDto(Collection<Page> pages) {
        return pages.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private PageDto toDto(Page page) {

        return mapper.map(page, PageDto.class);
    }

}
