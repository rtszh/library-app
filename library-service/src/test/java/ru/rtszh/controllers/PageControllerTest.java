package ru.rtszh.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PageController.class)
class PageControllerTest {
    @Autowired
    private MockMvc mvc;

    @WithMockUser(
            username = "admin",
            authorities = {"ROLE_ADMIN"}
    )
    @Test
    void getAllBooks() throws Exception {
        mvc.perform(get("/"))
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
    void editComment() throws Exception {
        mvc.perform(get("/books/{id}/comments/{order-number}", "1", 2))
                .andExpect(status().isOk())
                .andExpect(model().attribute("bookId", "1"))
                .andExpect(model().attribute("commentOrderNumber", 2))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

}