package com.example.demo.application.service.impl;

import com.example.demo.application.service.AuthService;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    @Override
    public User processOAuth2User(Map<String, Object> attributes) {
        String email = (String) attributes.get("email");

        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(email)
                            .name((String) attributes.get("name"))
                            .picture((String) attributes.get("picture"))
                            .build();
                    return userRepository.save(newUser);
                });
    }
}
