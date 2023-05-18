package ru.rtszh.libraryservice.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

public class JwtUtils {
    public static Optional<String> getUsernameFromToken(Authentication authentication) {
        try {
            var jwt = (Jwt) authentication.getPrincipal();

            return Optional.of((String) jwt.getClaims().get("preferred_username"));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
