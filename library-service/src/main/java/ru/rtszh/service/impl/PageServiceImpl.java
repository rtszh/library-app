package ru.rtszh.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.rtszh.domain.Page;
import ru.rtszh.dto.PageDto;
import ru.rtszh.repository.PageRepository;
import ru.rtszh.service.PageService;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PageServiceImpl implements PageService {

    private final PageRepository pageRepository;
    private final ModelMapper mapper;

    @Override
    public List<PageDto> findPagesByBookId(String bookId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        List<Page> pages = pageRepository.findByBookId(bookId, pageable);

        return toDto(pages);
    }

    @Override
    public int getPagesCount(String bookId) {
        return pageRepository.countAllByBookId(bookId);
    }

    @Override
    public void deleteAllPagesByBookId(String bookId) {
        pageRepository.deleteAllByBookId(bookId);
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
