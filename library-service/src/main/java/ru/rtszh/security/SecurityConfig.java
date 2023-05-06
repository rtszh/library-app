package ru.rtszh.security;

import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;


@EnableWebSecurity
public class SecurityConfig {

    private static final String ADMIN_AUTHORITY = "ROLE_ADMIN";
    private static final String USER_AUTHORITY = "ROLE_USER";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests((authorize) -> authorize
                        .antMatchers(HttpMethod.POST, "/api/v1/books").hasAuthority(ADMIN_AUTHORITY)
                        .antMatchers(HttpMethod.PUT, "/api/v1/books/*").hasAuthority(ADMIN_AUTHORITY)
                        .antMatchers(HttpMethod.DELETE, "/api/v1/books/*").hasAuthority(ADMIN_AUTHORITY)
                        .antMatchers("/").hasAnyAuthority(ADMIN_AUTHORITY, USER_AUTHORITY)
                        .antMatchers("/books/**").hasAnyAuthority(ADMIN_AUTHORITY, USER_AUTHORITY)
                        .antMatchers(HttpMethod.GET, "/api/v1/**").hasAnyAuthority(ADMIN_AUTHORITY, USER_AUTHORITY)
                        .antMatchers(HttpMethod.POST, "/api/v1/books/{id}/comments").hasAnyAuthority(ADMIN_AUTHORITY, USER_AUTHORITY)
                        .antMatchers(HttpMethod.PUT, "/api/v1/books/{id}/comments/*").hasAnyAuthority(ADMIN_AUTHORITY, USER_AUTHORITY)
                        .antMatchers(HttpMethod.DELETE, "/api/v1/books/{id}/comments/*").hasAnyAuthority(ADMIN_AUTHORITY, USER_AUTHORITY)
                        .antMatchers("/actuator/**").hasAuthority(ADMIN_AUTHORITY)
                )
                .oauth2ResourceServer()
                .jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter()));

        return http.build();
    }

    private Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(new RealmRoleConverter());
        return jwtConverter;
    }

}
