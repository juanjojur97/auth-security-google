package com.example.demo.infrastructure.adapter.input.oidc;

import com.example.demo.application.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final AuthService authService; // Llamamos a la capa de Aplicación

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        // 1. Recuperamos los datos de Auth0/Google
        OidcUser oidcUser = super.loadUser(userRequest);

        // 2. Ejecutamos la lógica de negocio (Guardar en Postgres si no existe)
        authService.processOAuth2User(oidcUser.getAttributes());

        return oidcUser;
    }
}
