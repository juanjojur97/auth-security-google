package com.example.demo.infrastructure.adapter.output.jwt;

import com.example.demo.domain.entity.User;
import com.example.demo.domain.port.output.TokenService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtAdapter implements TokenService {
    @Value("${JWT_SECRET}")
    private String secret;

    @Override
    public String generateCustomJwt(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }
}
