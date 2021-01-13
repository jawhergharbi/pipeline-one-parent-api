package com.sawoo.pipeline.api.service.user;

import com.googlecode.jmapper.JMapper;
import com.sawoo.pipeline.api.dto.user.UserAuthDetails;
import com.sawoo.pipeline.api.model.user.User;
import com.sawoo.pipeline.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserAuthJwtUserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final JMapper<UserAuthDetails, User> mapperDomainToDTO = new JMapper<>(UserAuthDetails.class, User.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Load user credentials: [{}]", username);

        return userRepository.findByEmail(username)
                .map((user) -> {
                    log.debug("User component successfully found: [{}]", user);

                    return mapperDomainToDTO.getDestination(user);
                }).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
}
