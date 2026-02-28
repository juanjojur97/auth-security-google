package com.example.demo.infrastructure.config;

import com.example.demo.domain.port.output.ExternalFolderService;
import com.example.demo.domain.port.output.TokenService;
import com.example.demo.infrastructure.adapter.input.oidc.CustomOidcUserService;
import com.example.demo.infrastructure.exception.AuthCustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOidcUserService customOidcUserService;
    private final TokenService tokenService; // Inyectamos tu generador de JWT (Domain Port)
    private final ExternalFolderService folderService; // Inyectamos el servicio de carpeta (Domain Port)

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, OAuth2AuthorizedClientService clientService) {
        try {
            return http
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/", "/debug/**").permitAll() // Permitimos el debug para ver tus IDs
                            .anyRequest().authenticated()
                    )
                    .oauth2Login(oauth2 -> oauth2
                            // Usamos el SuccessHandler para capturar el token directo de Google
                            .successHandler((request, response, authentication) -> {
                                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

                                // 1. Extraemos el cliente autorizado (Google)
                                OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(
                                        oauthToken.getAuthorizedClientRegistrationId(),
                                        oauthToken.getName());

                                // 2. ¡ESTA ES LA LLAVE! El token ya29 directo de Google
                                String googleToken = client.getAccessToken().getTokenValue();

                                OAuth2User principal = oauthToken.getPrincipal();
                                String name = principal.getAttribute("name");
                                String email = principal.getAttribute("email");

                                log.info("🚀 LOGIN DIRECTO: Token de Google obtenido para {}", email);

                                // 3. Crear la carpeta en Drive inmediatamente
                                if (googleToken != null) {
                                    folderService.createFolder("Carpeta Google Directa - " + name, googleToken);
                                    log.info("✅ Carpeta creada con éxito usando flujo directo.");
                                }

                                response.sendRedirect("/debug/session"); // Te mando al debug para que copies tus IDs
                            })
                    )
                    .build();
        } catch (Exception e) {
            throw new AuthCustomException("Error en la cadena de seguridad", e);
        }
    }
}
