package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.repository.AuthRepository;
import com.sawoo.pipeline.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthJwtUserDetailsServiceImpl implements UserDetailsService {

    private final AuthRepository authRepository;
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        log.debug("Load user credentials: [{}]", identifier);

        return authRepository.findByIdentifier(identifier)
                .map((auth) -> userRepository.findById(auth.getId()).map((user) -> {

                    List<GrantedAuthority> authorities = user.getRoles()
                            .stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    log.debug(
                            "Authentication component successfully found: Authentication identifier: [{}]. User id: [{}]. Authorities: [{}]",
                            identifier,
                            user.getId(),
                            authorities.toString());

                    return new User(auth.getIdentifier(), auth.getPassword(), authorities);
                }).orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + auth.getId())))
                .orElseThrow(() -> new UsernameNotFoundException("Authorization not found with identifier: " + identifier));
    }
}
