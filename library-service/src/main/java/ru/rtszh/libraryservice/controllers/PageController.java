package ru.rtszh.libraryservice.controllers;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.rtszh.libraryservice.config.SpringProperties;
import ru.rtszh.libraryservice.service.PageService;
import ru.rtszh.libraryservice.service.UserService;
import ru.rtszh.libraryservice.utils.JwtUtils;

import java.util.Collection;
import java.util.Optional;

@Controller
@EnableConfigurationProperties(SpringProperties.class)
public class PageController {

    private final PageService pageService;
    private final UserService userService;
    private final SpringProperties springProperties;

    public PageController(PageService pageService, UserService userService, SpringProperties springProperties) {
        this.pageService = pageService;
        this.userService = userService;
        this.springProperties = springProperties;
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
        model.addAttribute("applicationName", springProperties.getName());

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
        model.addAttribute("applicationName", springProperties.getName());

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
        model.addAttribute("applicationName", springProperties.getName());

        return "bookText";
    }

    private boolean isAdminAuthority(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_ADMIN"));
    }
}
