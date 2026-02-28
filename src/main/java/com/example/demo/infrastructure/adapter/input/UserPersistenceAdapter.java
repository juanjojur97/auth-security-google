package com.example.demo.infrastructure.adapter.input;

import com.example.demo.domain.entity.User;
import com.example.demo.domain.repository.UserRepository;
import com.example.demo.infrastructure.entity.UserJpaEntity;
import com.example.demo.infrastructure.mapper.UserMapper;
import com.example.demo.infrastructure.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepository {
    private final UserJpaRepository jpaRepository; // Spring Data
    private final UserMapper userMapper;           // MapStruct

    @Override
    public User save(User user) {
        // 1. Convertimos de Dominio a JPA
        UserJpaEntity entity = userMapper.toJpaEntity(user);
        // 2. Guardamos en Postgres (1578)
        UserJpaEntity savedEntity = jpaRepository.save(entity);
        // 3. Devolvemos al Dominio convertido
        return userMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(userMapper::toDomain);
    }
}
