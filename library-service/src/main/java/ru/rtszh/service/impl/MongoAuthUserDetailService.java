package ru.rtszh.service.impl;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.rtszh.domain.User;
import ru.rtszh.repository.UserRepository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

//@Service
public class MongoAuthUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    public MongoAuthUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findUserByUsername(userName);

        if (userOptional.isPresent()) {
            var user = userOptional.get();

            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    getGrantedAuthorities(user)
            );
        } else {
            throw new UsernameNotFoundException(
                    String.format("Username %s not found", userName)
            );
        }

    }

    private Set<GrantedAuthority> getGrantedAuthorities(User user) {
        return user.getRoles().stream()
                .map(role -> "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

}
