package com.bemojr.book_network.filter;

import com.bemojr.book_network.entity.Token;
import com.bemojr.book_network.repository.TokenRepository;
import com.bemojr.book_network.repository.UserRepository;
import com.bemojr.book_network.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);

        final SecurityContext securityContext = SecurityContextHolder.getContext();

        if (
                userEmail != null &&
                        securityContext.getAuthentication() == null
        ) {
            UserDetails userDetails = userRepository.findByEmail(userEmail).orElseThrow(() -> new EntityNotFoundException("User not found!"));
            Token token = tokenRepository.findByToken(jwt).orElseThrow(() -> new JwtException("Invalid token!"));

            if (jwtService.isTokenValid(jwt, userDetails) && token.isValid() && !token.isExpired()) {
                updateSecurityContext(request, userDetails, securityContext);
            } else {
                throw new JwtException("Invalid token!");
            }
        }

        filterChain.doFilter(request, response);
    }

    private void updateSecurityContext(HttpServletRequest request,UserDetails userDetails, SecurityContext securityContext) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        authenticationToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        securityContext.setAuthentication(authenticationToken);
    }
}
