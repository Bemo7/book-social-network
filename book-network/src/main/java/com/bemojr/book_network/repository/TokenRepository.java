package com.bemojr.book_network.repository;

import com.bemojr.book_network.entity.Token;
import com.bemojr.book_network.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {
    Optional<Token> findByToken(String token);

    @Query("""
    SELECT t FROM Token t\s
    JOIN t.user u\s
    WHERE t.user = :user AND\s
    (t.isExpired = false OR t.isValid = true)
    """)
    List<Token> findValidTokensByUser(User user);
}
