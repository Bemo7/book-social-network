package com.bemojr.book_network.service;

import com.bemojr.book_network.dto.AuthenticationRequest;
import com.bemojr.book_network.dto.AuthenticationResponse;
import com.bemojr.book_network.dto.RegistrationRequest;
import com.bemojr.book_network.entity.Role;
import com.bemojr.book_network.entity.Token;
import com.bemojr.book_network.entity.User;
import com.bemojr.book_network.repository.RoleRepository;
import com.bemojr.book_network.repository.TokenRepository;
import com.bemojr.book_network.repository.UserRepository;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final ActivationCodeService activationCodeService;
    private final UserService userService;

    @Transactional
    public void register(RegistrationRequest request) throws MessagingException, TemplateException, IOException {
        Role role = roleRepository.findByName("USER").orElseThrow(()-> new EntityNotFoundException("Role 'USER' has not been initialized"));
        Optional<User> existingUser = userRepository.findByEmail(request.email());

        if (existingUser.isPresent()) throw new EntityExistsException("User already exists");

        User user = userService.createUser(request, List.of(role));
        activationCodeService.sendValidationEmail(user);
    }



    public AuthenticationResponse authenticate(@Valid AuthenticationRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        Map<String, Object> claims = new HashMap<>();
        User user = (User) authentication.getPrincipal();
        claims.put("fullName", user.fullName());

        revokeAllToken(user);

        String token = jwtService.generateToken(claims, user);

        return AuthenticationResponse.builder().token(token).build();
    }

    @Transactional
    public void activateAccount(String token) throws MessagingException, TemplateException, IOException {
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (LocalDateTime.now().isAfter(savedToken.getExpiredAt())) {
            activationCodeService.revokeToken(savedToken);
            activationCodeService.sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Activation token has expired. A new token has been sent to the same email address");
        }

        User user =  userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setEnabled(true);
        userRepository.save(user);

        savedToken.setValid(true);
        savedToken.setValidatedAt(LocalDateTime.now());

        tokenRepository.save(savedToken);
    }

    @Transactional
    private void revokeAllToken(User user) {
        List<Token> tokens = tokenRepository.findValidTokensByUser(user);

        if (tokens.isEmpty()) return;

        tokens.forEach(
                token -> {
                    if (!token.isExpired() || token.getExpiredAt() == null) {
                        token.setExpired(true);
                        token.setExpiredAt(LocalDateTime.now());
                    }

                    if (token.isValid() || token.getValidatedAt() == null) {
                        token.setValidatedAt(LocalDateTime.now());
                        token.setValid(false);
                    }
                }
        );

        tokenRepository.saveAll(tokens);
    }
}
