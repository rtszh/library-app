package ru.rtszh.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.rtszh.service.PageService;
import ru.rtszh.service.UserService;
import ru.rtszh.utils.JwtUtils;

import java.util.Collection;
import java.util.Optional;

@Controller
public class PageController {

    private final PageService pageService;
    private final UserService userService;

    public PageController(PageService pageService, UserService userService) {
        this.pageService = pageService;
        this.userService = userService;
    }

    @GetMapping("/welcome")
    public String welcomePage(Authentication authentication) {

        if (isAdminAuthority(authentication.getAuthorities())) {
            return "adminBookList";
        } else {
            return "bookList";
        }
    }

    // admin
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin/books")
    public String adminGetAllBooks() {
        return "adminBookList";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin/books/{id}")
    public String adminEditBook(@PathVariable String id, Model model) {

        model.addAttribute("bookId", id);

        return "adminBookEdit";
    }

    // other users
    @GetMapping("/books")
    public String getAllBooks() {
        return "bookList";
    }

    @GetMapping("/books/{id}")
    public String editBook(@PathVariable String id, Model model) {

        model.addAttribute("bookId", id);

        return "bookInfo";
    }

    @GetMapping("/books/{id}/text")
    public String getBookText(
            @PathVariable String id,
            @RequestParam(defaultValue = "-1") int page,
            Authentication authentication,
            Model model
    ) {

        model.addAttribute("bookId", id);

        Optional<String> userName = JwtUtils.getUsernameFromToken(authentication);

        int validPage = userService.getPageNumber(userName, id, page);

        int totalPages = pageService.getPagesCount(id);

        model.addAttribute("currentPage", validPage);
        model.addAttribute("totalPages", totalPages);

        return "bookText";
    }

    private boolean isAdminAuthority(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_ADMIN"));
    }
}
