package com.bemojr.book_network.service;

import com.bemojr.book_network.entity.Token;
import com.bemojr.book_network.entity.User;
import com.bemojr.book_network.enumeration.TokenType;
import com.bemojr.book_network.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ClaimsBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoder;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${application.security.jwt.expiration}")
    private long JWT_EXPIRATION;

    @Value("${application.security.jwt.secret-key}")
    private String SECRET_KEY;

    private final TokenRepository tokenRepository;

    public String extractUsername(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }

    public Date extractExpiration(String jwt) {
        return extractClaim(jwt, Claims::getExpiration);
    }

    public <T> T extractClaim(String jwt, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(jwt);
        return claimResolver.apply(claims);
    }

    public Claims extractAllClaims(String jwt) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<String, Object>(), userDetails);
    }

    public String generateToken(Map<String, Object> claims, UserDetails userDetails) {
        String token = buildToken(claims, userDetails, JWT_EXPIRATION);
        saveToken(token, userDetails);
        return token;
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long jwtExpiration) {
        List<String> authorities = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        extraClaims.put("authorities", authorities);

        return Jwts.builder()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .signWith(getSignInKey())
                .compact();
    }

    @Transactional
    private void saveToken(String jwtToken, UserDetails userDetails) {
        if (!(userDetails instanceof User user)) {
            return;
        }

        Token token = Token.builder()
                .token(jwtToken)
                .createdAt(LocalDateTime.now())
                .tokenType(TokenType.BEARER)
                .isExpired(false)
                .isValid(true)
                .user(user)
                .build();

        tokenRepository.save(token);
    }

    public boolean isTokenValid(String jwt, UserDetails userDetails) {
        String username = extractClaim(jwt, Claims::getSubject);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(jwt);
    }

    private boolean isTokenExpired(String jwt) {
        Date expirationDate = extractExpiration(jwt);
        return (new Date(System.currentTimeMillis())).after(expirationDate);
    }

    public SecretKey getSignInKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
