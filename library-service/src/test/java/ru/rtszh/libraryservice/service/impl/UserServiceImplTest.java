package ru.rtszh.libraryservice.service.impl;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.rtszh.libraryservice.domain.Book;
import ru.rtszh.libraryservice.domain.User;
import ru.rtszh.libraryservice.repository.BookRepository;
import ru.rtszh.libraryservice.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {UserServiceImpl.class, ModelMapper.class})
class UserServiceImplTest {

    @Autowired
    private UserServiceImpl pageService;
    @MockBean
    private BookRepository bookRepository;
    @MockBean
    private UserRepository userRepository;

    // username == null
    @Test
    void getPageNumber1() {

        int expectedPageNumber = 1;

        int actualPageNumber = pageService.getPageNumber(Optional.empty(), "1", 1);

        assertThat(expectedPageNumber).isEqualTo(actualPageNumber);
    }

    // username != null, users > 1
    @Test
    void getPageNumber2() {

        int expectedPageNumber = 2;

        when(userRepository.findByUsernameAndBookId("testUser1", "1"))
                .thenReturn(
                        List.of(
                                User.builder().id("10").username("testUser1").stoppedAtPage(2).build(),
                                User.builder().id("11").username("testUser1").stoppedAtPage(3).build(),
                                User.builder().id("12").username("testUser1").stoppedAtPage(4).build()
                        )
                );

        int actualPageNumber = pageService.getPageNumber(Optional.of("testUser1"), "1", 2);

        verify(userRepository, times(1)).findByUsernameAndBookId(any(), any());
        verify(userRepository, times(1)).save(any());
        verify(userRepository, times(2)).delete(any());

        assertThat(expectedPageNumber).isEqualTo(actualPageNumber);
    }

    // username != null, users = 1
    @Test
    void getPageNumber3() {

        int expectedPageNumber = 2;

        when(userRepository.findByUsernameAndBookId("testUser1", "1"))
                .thenReturn(
                        List.of(
                                User.builder().id("10").username("testUser1").stoppedAtPage(2).build()
                        )
                );


        int actualPageNumber = pageService.getPageNumber(Optional.of("testUser1"), "1", 2);

        verify(userRepository, times(1)).findByUsernameAndBookId(any(), any());
        verify(userRepository, times(1)).save(any());

        assertThat(expectedPageNumber).isEqualTo(actualPageNumber);
    }

    // username != null, users = 0, page > 1
    @Test
    void getPageNumber4() {

        int expectedPageNumber = 2;

        when(userRepository.findByUsernameAndBookId("testUser1", "1"))
                .thenReturn(Collections.emptyList());

        when(bookRepository.findById("1"))
                .thenReturn(Optional.of(Book.builder().id("1").title("title1").build()));

        int actualPageNumber = pageService.getPageNumber(Optional.of("testUser1"), "1", 2);

        verify(bookRepository, times(1)).findById(any());
        verify(userRepository, times(1)).save(any());

        assertThat(expectedPageNumber).isEqualTo(actualPageNumber);
    }

    // username != null, users = 0, page = 1
    @Test
    void getPageNumber5() {

        int expectedPageNumber = 1;

        when(userRepository.findByUsernameAndBookId("testUser1", "1"))
                .thenReturn(Collections.emptyList());

        int actualPageNumber = pageService.getPageNumber(Optional.of("testUser1"), "1", 1);

        assertThat(expectedPageNumber).isEqualTo(actualPageNumber);
    }

}