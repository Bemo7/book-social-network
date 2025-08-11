package com.bemojr.book_network.entity;

import com.bemojr.book_network.enumeration.TokenType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Token {
    @Id
    @GeneratedValue
    private Integer id;

    private String token;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
    private boolean isExpired;
    private LocalDateTime validatedAt;
    private boolean isValid;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
