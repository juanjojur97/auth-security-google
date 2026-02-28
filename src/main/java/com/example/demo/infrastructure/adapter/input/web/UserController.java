package com.example.demo.infrastructure.adapter.input.web;

import com.example.demo.application.service.AuthService;
import com.example.demo.domain.entity.User;
import com.example.demo.infrastructure.adapter.input.web.dto.UserResponseDTO;
import com.example.demo.infrastructure.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;
    private final UserMapper userMapper;

    @GetMapping("/me")
    public UserResponseDTO getMyProfile(@AuthenticationPrincipal OidcUser principal) {
        // Obtenemos el usuario procesado por nuestra lógica
        User user = authService.processOAuth2User(principal.getAttributes());

        // Convertimos a DTO y devolvemos
        return userMapper.toDto(user);
    }
}

