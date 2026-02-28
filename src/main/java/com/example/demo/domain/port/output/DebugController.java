package com.example.demo.domain.port.output;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedHashMap;
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

        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(),
                oauthToken.getName());

        Map<String, Object> debugInfo = new LinkedHashMap<>(); // Usamos Linked para mantener el orden

        // 1. Datos de la App
        debugInfo.put("CLIENT_ID", clientId);
        debugInfo.put("CLIENT_SECRET", clientSecret);

        // 2. Access Token (Para Google Drive - el 'ya29')
        debugInfo.put("GOOGLE_ACCESS_TOKEN", client.getAccessToken().getTokenValue());

        // 3. RECUPERAR EL JWT (ID Token)
        if (oauthToken.getPrincipal() instanceof OidcUser oidcUser) {
            // Este es el JWT firmado por Google que pediste
            debugInfo.put("JWT_ID_TOKEN", oidcUser.getIdToken().getTokenValue());
            debugInfo.put("JWT_CLAIMS", oidcUser.getClaims());
        } else {
            debugInfo.put("JWT_ID_TOKEN", "No es un usuario OIDC (revisa el scope 'openid')");
        }

        return debugInfo;
    }
}
