package com.sawoo.pipeline.api.config;

import com.sawoo.pipeline.api.config.jwt.JwtAuthenticationEntryPoint;
import com.sawoo.pipeline.api.config.jwt.JwtRequestFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Slf4j
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@Profile(value = {"dev-local", "dev", "test", "prod"})
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final UserDetailsService jwtUserDetailsService;
    private final JwtRequestFilter jwtRequestFilter;

    @Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;

    @Value("${app.cors.allowed-methods}")
    private String[] allowedMethods;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationManager) throws Exception {
        // configure AuthenticationManager so that it knows from where to load
        // user for matching credentials. Use BCryptPasswordEncoder
        log.debug("Configuring authentication manager. AuthenticationManagerBuilder");
        authenticationManager.userDetailsService(jwtUserDetailsService).passwordEncoder(getEncoder());
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

    @Bean
    protected CorsConfigurationSource corsConfigurationSource() {
        log.debug("Configuring cors filter. Allowed origins: [{}]. Allowed methods: [{}]", allowedOrigins, allowedMethods);

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
        configuration.setAllowedMethods(Arrays.asList(allowedMethods));
        configuration.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // we don't CSFR
                .cors()
                .and()
                .authorizeRequests()
                .antMatchers(
                        "/api/auth/login",
                        "/api/auth/register",
                        "/api/auth/register2",
                        "/api/common/**",
                        "/actuator/**")
                .permitAll()
                .anyRequest()
                .authenticated()  // all other request must be authenticated
                .and()
                .exceptionHandling() // make sure we use stateless session (session won't be user to store user's session)
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
