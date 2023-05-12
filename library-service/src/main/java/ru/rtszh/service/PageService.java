package ru.rtszh.service;

import ru.rtszh.dto.PageDto;

import java.util.List;

public interface PageService {
    List<PageDto> findPagesByBookId(String bookId, int page, int size);

    int getPagesCount(String bookId);

    void deleteAllPagesByBookId(String bookId);
}
