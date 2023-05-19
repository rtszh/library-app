package ru.rtszh.libraryservice.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.rtszh.libraryservice.service.PageService;
import ru.rtszh.libraryservice.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = PageController.class,
        properties = {"spring.cloud.config.enabled=false"}
)
class PageControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @MockBean
    private PageService pageService;

    @WithMockUser(
            username = "admin",
            authorities = {"ROLE_ADMIN"}
    )
    @Test
    void getAllBooks() throws Exception {
        mvc.perform(get("/admin/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @WithMockUser(
            username = "admin",
            authorities = {"ROLE_ADMIN"}
    )
    @Test
    void editBook() throws Exception {
        mvc.perform(get("/books/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("bookId", "1"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @WithMockUser(
            username = "admin",
            authorities = {"ROLE_ADMIN"}
    )
    @Test
    void bookText() throws Exception {

        given(userService.getPageNumber(any(), any(), anyInt())).willReturn(2);
        given(pageService.getPagesCount(any())).willReturn(3);

        mvc.perform(get("/books/{id}/text", "1"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("bookId", "1"))
                .andExpect(model().attribute("currentPage", 2))
                .andExpect(model().attribute("totalPages", 3))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

}