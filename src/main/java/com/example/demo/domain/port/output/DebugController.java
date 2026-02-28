package com.example.demo.domain.port.output;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/debug")
@Slf4j
public class DebugController {
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    private final OAuth2AuthorizedClientService authorizedClientService;

    public DebugController(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    @GetMapping("/session")
    public Map<String, Object> getSessionInfo(Authentication authentication) {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

        // Recuperamos el Access Token (el 'ya29...')
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(),
                oauthToken.getName());

        String accessToken = client.getAccessToken().getTokenValue();

        Map<String, Object> debugInfo = new HashMap<>();
        debugInfo.put("1_CLIENT_ID", clientId);
        debugInfo.put("2_CLIENT_SECRET", clientSecret);
        debugInfo.put("3_GOOGLE_ACCESS_TOKEN", accessToken);
        debugInfo.put("4_USER_DETAILS", oauthToken.getPrincipal().getAttributes());

        log.info("🚀 Información de sesión recuperada con éxito");
        return debugInfo;
    }
}
