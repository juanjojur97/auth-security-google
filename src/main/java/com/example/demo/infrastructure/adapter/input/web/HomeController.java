package com.example.demo.infrastructure.adapter.input.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, String> home(@RequestParam(value = "token", required = false) String token) {
        if (token != null) {
            return Map.of(
                    "message", "¡Login exitoso!",
                    "your_custom_jwt", token,
                    "info", "Copia este token y úsalo en el header Authorization: Bearer"
            );
        }
        return Map.of("message", "Bienvenido, por favor haz login en /oauth2/authorization/auth0");
    }
}
