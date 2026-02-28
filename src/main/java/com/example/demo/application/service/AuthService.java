package com.example.demo.application.service;

import com.example.demo.domain.entity.User;

import java.util.Map;

public interface AuthService {
    User processOAuth2User(Map<String, Object> attributes);
}
