package com.bemojr.book_network.configuration;

import com.bemojr.book_network.filter.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {
    private static final String[] WHITELIST = {
            "/auth/**",
            "/v*/api-docs/**",
            "/swagger-*/**",
            "/configuration-ui",
            "/configuration-security",
            "/webjars/**"
    };

    private final AuthenticationProvider authenticationProvider;
    private final JwtFilter jwtAuthFilter;
    private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return  httpSecurity
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        requestMatcherRegistry -> requestMatcherRegistry.requestMatchers(WHITELIST)
                                .permitAll()
                                .requestMatchers("/demo/**", "/user/**")
                                .hasRole("USER")
                                .anyRequest()
                                .authenticated()
                )
                .sessionManagement(
                        sessionManagementConfigurer -> sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                ).logout(
                        logoutConfigurer -> logoutConfigurer.addLogoutHandler(logoutHandler)
//                                .logoutUrl("/auth/logout")
                                .clearAuthentication(true)
                                .logoutSuccessHandler(
                                        (request, response, authentication) -> SecurityContextHolder.clearContext()
                                )
                )
                .build();
    }
}
