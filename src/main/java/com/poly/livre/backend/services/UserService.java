package com.poly.livre.backend.services;

import com.poly.livre.backend.exceptions.ConflictException;
import com.poly.livre.backend.exceptions.NotFoundException;
import com.poly.livre.backend.exceptions.errors.UserErrorCode;
import com.poly.livre.backend.models.converters.UserConverter;
import com.poly.livre.backend.models.dtos.UserDto;
import com.poly.livre.backend.models.dtos.UserRegistrationDto;
import com.poly.livre.backend.models.entities.User;
import com.poly.livre.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final PasswordEncoder encoder;

    /**
     * Register a new user
     * @param dto
     * @return
     */
    @Transactional
    public UserDto registerUser(UserRegistrationDto dto) {
        log.info("Registering user : {}", dto.getUsername());

        userRepository.findByEmail(dto.getEmail())
                .ifPresent(user -> {
                    throw new ConflictException(UserErrorCode.EMAIL_ALREADY_EXISTS, dto.getEmail());
                });

        String encodedPassword = encoder.encode(dto.getPassword());

        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(encodedPassword)
                .build();

        return userConverter.convert(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(UUID userId) {
        log.info("Getting user by id : {}", userId);

        return userRepository.findById(userId)
                .map(userConverter::convert)
                .orElseThrow(() -> new NotFoundException(UserErrorCode.NOT_FOUND, userId.toString()));
    }

}