package com.poly.livre.backend.services;

import com.poly.livre.backend.exceptions.BadRequestException;
import com.poly.livre.backend.exceptions.ForbiddenException;
import com.poly.livre.backend.exceptions.NotFoundException;
import com.poly.livre.backend.exceptions.errors.UserErrorCode;
import com.poly.livre.backend.managers.JwtManager;
import com.poly.livre.backend.models.converters.UserConverter;
import com.poly.livre.backend.models.dtos.UserDto;
import com.poly.livre.backend.models.dtos.UserProfileValues;
import com.poly.livre.backend.models.enums.UserStatus;
import com.poly.livre.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poly.livre.backend.models.entities.User;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements BaseService {
    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final JwtManager jwtManager;

    @Transactional(readOnly = true)
    public UserDto getUserById(@NonNull UUID userId) {
        log.info("Getting user by id : {}", userId);

        return userRepository.findById(userId)
                .map(userConverter::convert)
                .orElseThrow(() -> new NotFoundException(UserErrorCode.NOT_FOUND, userId.toString()));
    }

    @Transactional(readOnly = true)
    public UserDto getCurrentUserDto() {
        return getCurrentUser()
                .map(principal -> {
                    UserDto dto = getUserById(principal.getId());
                    dto.setExpiresIn(jwtManager.getExpirationTime());
                    return dto;
                })
                .orElseThrow(() -> new ForbiddenException(UserErrorCode.NOT_FOUND));
    }

    @Transactional
    public UserDto updateUsername(String username) {
        User user = getCurrentUser()
                .flatMap(principal -> userRepository.findById(principal.getId()))
                .orElseThrow(() -> new ForbiddenException(UserErrorCode.NOT_FOUND));

        user.setUsername(username);
        UserDto dto = userConverter.convert(userRepository.save(user));
        dto.setExpiresIn(jwtManager.getExpirationTime());
        return dto;
    }

    @Transactional
    public UserDto createProfile(@NonNull String username, @NonNull UserProfileValues values) {
        log.info("Creating profile for user : {}", username);

        if (!username.equals(values.username())) {
            throw new BadRequestException(UserErrorCode.USERNAME_MISMATCH);
        }

        User user = getCurrentUser()
                .flatMap(principal -> userRepository.findById(principal.getId()))
                .orElseThrow(() -> new ForbiddenException(UserErrorCode.NOT_FOUND));

        user.setUsername(values.username());
        user.setFirstName(values.firstName());
        user.setLastName(values.lastName());
        try {
            user.setStatus(UserStatus.valueOf(values.status()));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(UserErrorCode.INVALID_STATUS, values.status());
        }

        UserDto dto = userConverter.convert(userRepository.save(user));
        dto.setExpiresIn(jwtManager.getExpirationTime());
        return dto;
    }

}