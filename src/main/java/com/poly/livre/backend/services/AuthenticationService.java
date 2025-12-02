package com.poly.livre.backend.services;

import com.poly.livre.backend.exceptions.ForbiddenException;
import com.poly.livre.backend.exceptions.errors.AuthenticationErrorCode;
import com.poly.livre.backend.managers.JwtManager;
import com.poly.livre.backend.models.dtos.AuthenticationRequest;
import com.poly.livre.backend.models.dtos.AuthenticationResponse;
import com.poly.livre.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService  {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtManager jwtManager;

    @Transactional
    public AuthenticationResponse authenticateUser(AuthenticationRequest dto) {
        log.info("Authenticating user : {}", dto.getEmail());

        return userRepository.findByEmail(dto.getEmail())
                .filter(user -> encoder.matches(dto.getPassword(), user.getPassword()))
                .map(user -> AuthenticationResponse.builder()
                        .userId(user.getId())
                        .email(user.getEmail())
                        .token(jwtManager.generateToken(user))
                        .expiresIn(jwtManager.getExpirationTime())
                        .build())
                .orElseThrow(() -> new ForbiddenException(AuthenticationErrorCode.FAILED));
    }

}

// TODO : Modify for magic link and passkey