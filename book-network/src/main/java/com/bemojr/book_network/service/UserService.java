package com.bemojr.book_network.service;

import com.bemojr.book_network.dto.RegistrationRequest;
import com.bemojr.book_network.entity.Role;
import com.bemojr.book_network.entity.User;
import com.bemojr.book_network.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public User createUser(RegistrationRequest request, List<Role> roles) {
        User user = User.builder()
                .email(request.email())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .password(
                        passwordEncoder.encode(
                                request.password()
                        )
                )
                .enabled(false)
                .accountLocked(false)
                .roles(roles)
                .build();

        return userRepository.save(user);
    }
}
