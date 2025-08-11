package com.bemojr.book_network.service;

import com.bemojr.book_network.entity.Token;
import com.bemojr.book_network.entity.User;
import com.bemojr.book_network.enumeration.EmailTemplateName;
import com.bemojr.book_network.enumeration.TokenType;
import com.bemojr.book_network.repository.TokenRepository;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ActivationCodeService {
    @Value("${application.security.mailing.frontend.activation-url}")
    private String activationUrl;

    private final EmailService emailService;
    private final TokenRepository tokenRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendValidationEmail(User user) throws MessagingException, TemplateException, IOException {
        String token = generateAndSaveActivationToken(user);

        emailService.sendEmail(
                user.getEmail(),
                user.fullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                "Account Activation",
                token
        );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revokeToken(Token token) {
        token.setExpired(true);
        token.setValid(false);
        tokenRepository.save(token);
    }

    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationCode(6);
        Token token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(5))
                .tokenType(TokenType.ACTIVATION)
                .isExpired(false)
                .isValid(true)
                .user(user)
                .build();

        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationCode(int codeLength) {
        String characters = "0123456789";
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < codeLength; i++) {
            int randomIndex = new SecureRandom().nextInt(characters.length());
            stringBuilder.append(characters.charAt(randomIndex));
        }
        return stringBuilder.toString();
    }
}
