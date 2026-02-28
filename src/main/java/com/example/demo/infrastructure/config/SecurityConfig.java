package com.example.demo.infrastructure.config;

import com.example.demo.infrastructure.adapter.input.oidc.CustomAuthenticationFailureHandler;
import com.example.demo.infrastructure.adapter.input.oidc.CustomOidcUserService;
import com.example.demo.infrastructure.exception.AuthCustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOidcUserService customOidcUserService;
    private final CustomAuthenticationFailureHandler failureHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        try {
            return http
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/").permitAll()
                            .anyRequest().authenticated()
                    )
                    .oauth2Login(oauth2 -> oauth2
                            .userInfoEndpoint(userInfo -> userInfo.oidcUserService(customOidcUserService))
                            .failureHandler(failureHandler)
                    )
                    .build();
        } catch (Exception e) {
            // Al lanzar una RuntimeException personalizada, SonarLint ve que el error está controlado
            throw new AuthCustomException("Error fatal al configurar la cadena de filtros de seguridad", e);
        }
    }
}
