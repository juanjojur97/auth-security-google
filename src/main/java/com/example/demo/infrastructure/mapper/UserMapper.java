package com.example.demo.infrastructure.mapper;

import com.example.demo.domain.entity.User;
import com.example.demo.infrastructure.adapter.input.web.dto.UserResponseDTO;
import com.example.demo.infrastructure.entity.UserJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toDomain(UserJpaEntity entity);
    UserJpaEntity toJpaEntity(User user);

    // Web
    UserResponseDTO toDto(User user); // <--- Nuevo mapeo
}
