package ru.rtszh.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.rtszh.service.PageService;

import java.util.Collection;

@Controller
public class PageController {

    private final PageService pageService;

    public PageController(PageService pageService) {
        this.pageService = pageService;
    }

    @GetMapping("/welcome")
    public String welcomePage(Authentication authentication) {

        if (isAdminAuthority(authentication.getAuthorities())) {
            return "bookList";
        } else {
            return "hello";
        }
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin/books")
    public String getAllBooks() {
        return "bookList";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin/books/{id}")
    public String editBook(@PathVariable String id, Model model) {

        model.addAttribute("bookId", id);

        return "bookEdit";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin/books/{id}/comments/{order-number}")
    public String editComment(@PathVariable("id") String id,
                              @PathVariable("order-number") int orderNumber,
                              Model model) {

        model.addAttribute("bookId", id);
        model.addAttribute("commentOrderNumber", orderNumber);

        return "commentEdit";
    }

    @GetMapping("/books/{id}/text")
    public String getBookText(
            @PathVariable String id,
            @RequestParam(defaultValue = "-1") int page,
            Model model
    ) {

        model.addAttribute("bookId", id);

        if (page == -1) {
            page = 1; // для page == -1 нужно взять page из базы данных для username
            // если username открыл книгу впервые, то нужно присвоить page == 1
        }

        int totalPages = pageService.getPagesCount(id);

        // current page - страница, на которой остановился текущий пользователь
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "bookText";
    }

    private boolean isAdminAuthority(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_ADMIN"));
    }
//
//    @GetMapping("/user")
//    public String getUserPage(Authentication authentication, Model model) {
//
//
//
//
//    }
}
