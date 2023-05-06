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
//@ComponentScan(basePackages = "ru.rtszh.service")
public class SecurityConfig {

    //    private static final String ADMIN_ROLE = "ADMIN";
//    private static final String USER_ROLE = "USER";
    private static final String ADMIN_ROLE = "ROLE_ADMIN";
    private static final String USER_ROLE = "ROLE_USER";
//
//    @Bean
//    public BCryptPasswordEncoder bCryptPasswordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf().disable()
//                .authorizeHttpRequests((authorize) -> authorize
//                        .antMatchers(HttpMethod.POST, "/api/v1/books").hasRole(ADMIN_ROLE)
//                        .antMatchers(HttpMethod.PUT, "/api/v1/books/*").hasRole(ADMIN_ROLE)
//                        .antMatchers(HttpMethod.DELETE, "/api/v1/books/*").hasRole(ADMIN_ROLE)
//                        .antMatchers("/").hasAnyRole(ADMIN_ROLE, USER_ROLE)
//                        .antMatchers("/books/**").hasAnyRole(ADMIN_ROLE, USER_ROLE)
//                        .antMatchers(HttpMethod.GET, "/api/v1/**").hasAnyRole(ADMIN_ROLE, USER_ROLE)
//                        .antMatchers(HttpMethod.POST, "/api/v1/books/{id}/comments").hasAnyRole(ADMIN_ROLE, USER_ROLE)
//                        .antMatchers(HttpMethod.PUT, "/api/v1/books/{id}/comments/*").hasAnyRole(ADMIN_ROLE, USER_ROLE)
//                        .antMatchers(HttpMethod.DELETE, "/api/v1/books/{id}/comments/*").hasAnyRole(ADMIN_ROLE, USER_ROLE)
//                        .antMatchers("/actuator/**").hasRole(ADMIN_ROLE)
//                        .anyRequest().denyAll()
//                )
//                .formLogin();
//        return http.build();
//    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeRequests()
//                .anyRequest().authenticated()
//                .and()
//                .oauth2ResourceServer()
//                .jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter()));
//
//        return http.build();
//    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeRequests((authorize) -> authorize
//                        .antMatchers(HttpMethod.POST, "/api/v1/books").hasRole(ADMIN_ROLE)
//                        .antMatchers(HttpMethod.PUT, "/api/v1/books/*").hasRole(ADMIN_ROLE)
//                        .antMatchers(HttpMethod.DELETE, "/api/v1/books/*").hasRole(ADMIN_ROLE)
//                        .antMatchers("/").hasAnyRole(ADMIN_ROLE, USER_ROLE)
//                        .antMatchers("/books/**").hasAnyRole(ADMIN_ROLE, USER_ROLE)
//                        .antMatchers(HttpMethod.GET, "/api/v1/**").hasAnyRole(ADMIN_ROLE, USER_ROLE)
//                        .antMatchers(HttpMethod.POST, "/api/v1/books/{id}/comments").hasAnyRole(ADMIN_ROLE, USER_ROLE)
//                        .antMatchers(HttpMethod.PUT, "/api/v1/books/{id}/comments/*").hasAnyRole(ADMIN_ROLE, USER_ROLE)
//                        .antMatchers(HttpMethod.DELETE, "/api/v1/books/{id}/comments/*").hasAnyRole(ADMIN_ROLE, USER_ROLE)
//                        .antMatchers("/actuator/**").hasRole(ADMIN_ROLE)
//                )
//                .oauth2ResourceServer()
//                .jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter()));
//
//        return http.build();
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests((authorize) -> authorize
                        .antMatchers(HttpMethod.POST, "/api/v1/books").hasAuthority(ADMIN_ROLE)
                        .antMatchers(HttpMethod.PUT, "/api/v1/books/*").hasAuthority(ADMIN_ROLE)
                        .antMatchers(HttpMethod.DELETE, "/api/v1/books/*").hasAuthority(ADMIN_ROLE)
                        .antMatchers("/").hasAnyAuthority(ADMIN_ROLE, USER_ROLE)
                        .antMatchers("/books/**").hasAnyAuthority(ADMIN_ROLE, USER_ROLE)
                        .antMatchers(HttpMethod.GET, "/api/v1/**").hasAnyAuthority(ADMIN_ROLE, USER_ROLE)
                        .antMatchers(HttpMethod.POST, "/api/v1/books/{id}/comments").hasAnyAuthority(ADMIN_ROLE, USER_ROLE)
                        .antMatchers(HttpMethod.PUT, "/api/v1/books/{id}/comments/*").hasAnyAuthority(ADMIN_ROLE, USER_ROLE)
                        .antMatchers(HttpMethod.DELETE, "/api/v1/books/{id}/comments/*").hasAnyAuthority(ADMIN_ROLE, USER_ROLE)
                        .antMatchers("/actuator/**").hasAuthority(ADMIN_ROLE)
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
