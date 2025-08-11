package com.bemojr.book_network.dto;

import com.bemojr.book_network.entity.Role;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record UserDetailsDto (
        String firstName,
        String lastName,
        LocalDate dob,
        String email,
        LocalDateTime createdAt,
        List<Role> roles
){
}
