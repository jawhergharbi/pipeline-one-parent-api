package com.sawoo.pipeline.api.service.auth;

import com.googlecode.jmapper.JMapper;
import com.sawoo.pipeline.api.dto.user.UserAuthDetails;
import com.sawoo.pipeline.api.model.UserMongoDB;
import com.sawoo.pipeline.api.repository.mongo.UserRepositoryMongo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthJwtUserDetailsServiceImpl implements UserDetailsService {

    private final UserRepositoryMongo userRepositoryMongo;
    private final JMapper<UserAuthDetails, UserMongoDB> mapperDomainToDTO = new JMapper<>(UserAuthDetails.class, UserMongoDB.class);

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Load user credentials: [{}]", email);

        return userRepositoryMongo.findByEmail(email)
                .map((user) -> {
                    log.debug("User component successfully found: [{}]", user);

                    return mapperDomainToDTO.getDestination(user);
                }).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}
