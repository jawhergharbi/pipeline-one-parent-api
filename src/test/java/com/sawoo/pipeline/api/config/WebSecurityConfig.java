package com.sawoo.pipeline.api.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
@EnableGlobalMethodSecurity(securedEnabled=true, prePostEnabled = true)
@AllArgsConstructor
@Profile(value = {"unit-tests", "integration-tests", "unit-tests-embedded"})
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        auth.inMemoryAuthentication()
                .passwordEncoder(encoder)
                    .withUser("miguel")
                    .password(encoder.encode("my-secret"))
                    .roles("USER")
                .and()
                    .withUser("miguelito")
                    .password(encoder.encode("my-secret"))
                    .roles("USER", "ADMIN");
    }

    @Bean
    public PasswordEncoder getEncoder() {
        log.debug("Configuring new password encoder. Type: BCryptPasswordEncoder");
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        log.debug("Configuring new authentication manager. Default bean is being used");
        return super.authenticationManagerBean();
    }
}
