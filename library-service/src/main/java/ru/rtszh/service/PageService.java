package ru.rtszh.service;

import ru.rtszh.dto.PageDto;

import java.util.List;

public interface PageService {
    List<PageDto> findPagesByBookId(String id, int page, int size);

    int getPagesCount(String id);
}
