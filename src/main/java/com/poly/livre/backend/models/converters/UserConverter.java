package com.poly.livre.backend.models.converters;

import com.poly.livre.backend.models.dtos.UserDto;
import com.poly.livre.backend.models.entities.User;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class UserConverter implements Converter<User, UserDto> {

    @Override
    public UserDto convert(@NonNull User user) {

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .status(user.getStatus())
                .build();
    }

}