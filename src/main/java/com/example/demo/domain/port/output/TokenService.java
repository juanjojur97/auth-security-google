package com.example.demo.domain.port.output;

import com.example.demo.domain.entity.User;

public interface TokenService {
    String generateCustomJwt(String email);
}
