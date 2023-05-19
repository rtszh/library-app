package ru.rtszh.gateway.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.rtszh.gateway.config.AppProperties;

@RequiredArgsConstructor

@Controller
@EnableConfigurationProperties(AppProperties.class)
public class GatewayController {

    private final AppProperties appProperties;

    @GetMapping("/")
    public String getWelcomePage(Model model) {

        model.addAttribute("libraryServiceName", appProperties.getRedirectService());

        return "index";
    }
}
