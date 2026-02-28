package com.example.demo.domain.port.output;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;


//@Service
@Slf4j
public class Auth0IdentityService {

    private final WebClient webClient;
    private final String issuer;

    @Value("${AUTH0_M2M_CLIENT_ID}")
    private String m2mClientId;

    @Value("${AUTH0_M2M_CLIENT_SECRET}")
    private String m2mClientSecret;

    public Auth0IdentityService(WebClient.Builder builder,
                                @Value("${spring.security.oauth2.client.provider.auth0.issuer-uri}") String issuer) {
        this.issuer = issuer.endsWith("/") ? issuer : issuer + "/";
        this.webClient = builder.build();
    }

    /**
     * Paso 1: Pide un Management Token fresco a Auth0 usando credenciales M2M
     */
    private String getFreshManagementToken() {
        Map<String, String> body = Map.of(
                "client_id", m2mClientId,
                "client_secret", m2mClientSecret,
                "audience", issuer + "api/v2/",
                "grant_type", "client_credentials"
        );

        Map<String, Object> response = webClient.post()
                .uri(issuer + "oauth/token")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .block();

        return (String) response.get("access_token");
    }

    /**
     * Paso 2: Usa ese token para obtener la identidad de Google del usuario
     */
    public String getGoogleAccessToken(String auth0UserId) {
        try {
            String token = getFreshManagementToken();
            log.info("🔍 Consultando a Auth0 por el usuario: {}", auth0UserId);

            Map<String, Object> response = webClient.get()
                    .uri(issuer + "api/v2/users/{id}", auth0UserId)
                    .headers(h -> h.setBearerAuth(token))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            // LOG CRÍTICO: Vamos a ver qué hay en 'identities'
            List<Map<String, Object>> identities = (List<Map<String, Object>>) response.get("identities");
            log.info("📦 Identidades encontradas: {}", identities);

            if (identities != null) {
                for (Map<String, Object> id : identities) {
                    if ("google-oauth2".equals(id.get("provider"))) {
                        String googleToken = (String) id.get("access_token");
                        if (googleToken != null) {
                            log.info("✅ ¡TOKEN ENCONTRADO!: {}", googleToken.substring(0, 10) + "...");
                            return googleToken;
                        }
                    }
                }
            }
            log.warn("❌ El JSON de Auth0 NO TIENE el campo 'access_token'.");
        } catch (Exception e) {
            log.error("💥 Error grave en el servicio: {}", e.getMessage());
        }
        return null;
    }
}
