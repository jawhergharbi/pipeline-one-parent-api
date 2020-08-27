package com.sawoo.pipeline.api.config.jwt;

import com.sawoo.pipeline.api.common.contants.JwtConstants;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@AllArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        JwtTokenWrapper tokenWrapper = getToken(request);

        if (tokenWrapper != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(tokenWrapper.getUsername());
            if (jwtTokenUtil.validateToken(tokenWrapper.getToken(), userDetails)) {
                UsernamePasswordAuthenticationToken token =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // After setting the Authentication in the context, we specify
                // that the current user is authenticated. So it passes the
                // Spring Security Configurations successfully.
                SecurityContextHolder.getContext().setAuthentication(token);
            }
        }
        filterChain.doFilter(request, response);
    }

    private JwtTokenWrapper getToken(HttpServletRequest request) {
        final String requestTokenHeader = request.getHeader(JwtConstants.AUTHORIZATION_HEADER_KEY);
        String username = null, jwtToken = null;
        if (requestTokenHeader != null && requestTokenHeader.startsWith(JwtConstants.JWT_TOKEN_PREFIX)) {
            log.debug("Auth token has been found {} for request: {} ", request, request.getRequestURI());
            jwtToken = requestTokenHeader.substring(JwtConstants.JWT_TOKEN_PREFIX.length());
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException error) {
                log.warn("Exception trying to get username from jwt token. {}. Request: {}", error.getMessage(), request.getRequestURI());
            } catch (ExpiredJwtException error) {
                log.warn("Jwt token expired. Request: {}. Error {}", error.getMessage(), request.getRequestURI());
            }
        } else {
            log.warn("Proper Auth token has not been found for request: {}", request.getRequestURI());
        }

        JwtTokenWrapper tokenWrapper = null;
        if (username != null) {
            tokenWrapper = new JwtTokenWrapper(username, jwtToken);
        }
        return tokenWrapper;
    }
}
