package ru.rtszh.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PageController {
    @GetMapping("/books")
    public String getAllBooks() {
        return "bookList";
    }

    @GetMapping("/books/{id}")
    public String editBook(@PathVariable String id, Model model) {

        model.addAttribute("bookId", id);

        return "bookEdit";
    }

    @GetMapping("/books/{id}/comments/{order-number}")
    public String editComment(@PathVariable("id") String id,
                              @PathVariable("order-number") int orderNumber,
                              Model model) {

        model.addAttribute("bookId", id);
        model.addAttribute("commentOrderNumber", orderNumber);

        return "commentEdit";
    }
}
