package com.bemojr.book_network.controller;

import com.bemojr.book_network.dto.UserDetailsDto;
import com.bemojr.book_network.entity.User;
import com.bemojr.book_network.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class UserController {
    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<?> me(){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        User user = (User) authentication.getPrincipal();
        UserDetailsDto userDetailsDto = UserDetailsDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .dob(user.getDob())
                .createdAt(user.getCreatedAt())
                .roles(user.getRoles())
                .build();

        return ResponseEntity.ok(userDetailsDto);
    }

    @GetMapping("/{user-id}")
    @PreAuthorize("hasAnyAuthority('admin','user')")
    public ResponseEntity<?> getUserDetails(
            @PathVariable("user-id") Integer userId
    ) {
        User user = userRepository.findById(userId).orElseThrow(()-> new EntityNotFoundException("User not found!"));
        UserDetailsDto userDetailsDto = UserDetailsDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .dob(user.getDob())
                .createdAt(user.getCreatedAt())
                .roles(user.getRoles())
                .build();
        return ResponseEntity.ok(userDetailsDto);
    }
}
