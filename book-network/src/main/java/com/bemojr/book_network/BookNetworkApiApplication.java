package com.bemojr.book_network;

import com.bemojr.book_network.entity.Role;
import com.bemojr.book_network.entity.User;
import com.bemojr.book_network.repository.RoleRepository;
import com.bemojr.book_network.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableJpaAuditing
@RequiredArgsConstructor
public class BookNetworkApiApplication {
	private final RoleRepository roleRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(BookNetworkApiApplication.class, args);
	}

	@Profile("dev")
	@Bean
	public CommandLineRunner commandLineRunner() {
		return args -> {
			List<Role> roles = new ArrayList<>();
			if (roleRepository.findByName("USER").isEmpty()) {
				Role role = roleRepository.saveAndFlush(Role.builder().name("USER").build());
				roles.add(role);
			};

			if (roleRepository.findByName("ADMIN").isEmpty()) {
				Role role = roleRepository.saveAndFlush(Role.builder().name("ADMIN").build());
				roles.add(role);
			};

			User user = User.builder()
					.firstName("John")
					.lastName("Doe")
					.email("johndoe@email.com")
					.enabled(true)
					.roles(roles)
					.password(passwordEncoder.encode("12345678"))
					.build();

			userRepository.save(user);
		};
	}

}
