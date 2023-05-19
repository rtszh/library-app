package ru.rtszh.libraryservice.service.impl;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.rtszh.libraryservice.domain.Book;
import ru.rtszh.libraryservice.domain.Page;
import ru.rtszh.libraryservice.dto.PageDto;
import ru.rtszh.libraryservice.repository.PageRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@SpringBootTest(
        classes = {PageServiceImpl.class, ModelMapper.class},
        properties = {"spring.cloud.config.enabled=false"}
)
class PageServiceImplTest {

    @Autowired
    private PageServiceImpl pageService;
    @Autowired
    private ModelMapper mapper;
    @MockBean
    private PageRepository pageRepository;

    @Test
    void findPagesByBookId() {

        List<Page> pages = List.of(
                Page.builder()
                        .id("1")
                        .text("text1")
                        .chapter(1)
                        .pageNumber(1)
                        .bookId(Book.builder().id("10").title("title1").build())
                        .build(),
                Page.builder()
                        .id("2")
                        .text("text2")
                        .chapter(1)
                        .pageNumber(2)
                        .bookId(Book.builder().id("10").title("title1").build())
                        .build()
        );

        List<PageDto> expectedPages = List.of(
                PageDto.builder()
                        .text("text1")
                        .chapter(1)
                        .pageNumber(1)
                        .build(),
                PageDto.builder()
                        .text("text2")
                        .chapter(1)
                        .pageNumber(2)
                        .build()
        );

        when(pageRepository.findByBookId(any(), any()))
                .thenReturn(pages);

        List<PageDto> actualPages = pageService.findPagesByBookId("1", 1, 2);

        assertThat(expectedPages).hasSameElementsAs(actualPages);
    }

    @Test
    void getPagesCount() {
        when(pageRepository.countAllByBookId(any()))
                .thenReturn(10);

        int expectedPagesCount = 10;

        int actualPagesCount = pageService.getPagesCount(any());

        assertThat(expectedPagesCount).isEqualTo(actualPagesCount);
    }
}