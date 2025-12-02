package com.poly.livre.backend.services;

import com.poly.livre.backend.exceptions.NotFoundException;
import com.poly.livre.backend.exceptions.errors.UserErrorCode;
import com.poly.livre.backend.models.converters.UserConverter;
import com.poly.livre.backend.models.dtos.UserDto;
import com.poly.livre.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserConverter userConverter;

    @Transactional(readOnly = true)
    public UserDto getUserById(@NonNull UUID userId) {
        log.info("Getting user by id : {}", userId);

        return userRepository.findById(userId)
                .map(userConverter::convert)
                .orElseThrow(() -> new NotFoundException(UserErrorCode.NOT_FOUND, userId.toString()));
    }

}