package com.bemojr.book_network.configuration;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class KeyCloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt token) {
        Collection<? extends GrantedAuthority> authorities = Stream.concat(
                new JwtAuthenticationConverter().convert(token).getAuthorities().stream(),
                extractResourceRoles(token).stream()
        ).collect(Collectors.toSet());

        JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(token, authorities);

        return jwtAuthenticationToken;
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt token) {
        var resourceAccess = (HashMap<String, Object>) token.getClaim("resource_access");
        if (resourceAccess == null) {
            return Set.of();
        }

        var account = (HashMap<String, Object>) resourceAccess.get("account");
        if (account == null) {
            return Set.of();
        }

        var roles = (List<String>) account.get("roles");

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.replace("-","_")))
                .collect(Collectors.toSet());
    }
}
