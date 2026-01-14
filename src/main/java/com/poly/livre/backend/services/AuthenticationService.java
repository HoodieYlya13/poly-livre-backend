package com.poly.livre.backend.services;

import com.poly.livre.backend.exceptions.ForbiddenException;
import com.poly.livre.backend.exceptions.errors.AuthenticationErrorCode;
import com.poly.livre.backend.managers.JwtManager;
import com.poly.livre.backend.models.dtos.AuthenticationFinishRequest;
import com.poly.livre.backend.models.dtos.AuthenticationResponse;
import com.poly.livre.backend.models.dtos.RegistrationFinishRequest;
import com.poly.livre.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.webauthn4j.data.PublicKeyCredentialCreationOptions;
import com.webauthn4j.data.PublicKeyCredentialRequestOptions;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import com.poly.livre.backend.models.dtos.PasskeyResponse;
import com.poly.livre.backend.models.entities.User;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtManager jwtManager;
    private final EmailService emailService;
    private final WebAuthnService webAuthnService;
    private final Clock clock;

    @Value("${webauthn.origin}")
    private String frontEndOrigin;

    @Transactional
    public void requestMagicLink(String email) {
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> registerNewUser(email));

        String token = UUID.randomUUID().toString();
        String hashedToken = hashToken(token);

        user.setMagicLinkToken(hashedToken);
        user.setMagicLinkTokenExpiration(Instant.now(clock).plus(15, ChronoUnit.MINUTES));
        userRepository.save(user);

        emailService.sendMagicLink(email, frontEndOrigin + "/auth/magic-link?token=" + token);
    }

    @Transactional
    public AuthenticationResponse verifyMagicLink(String token) {
        String hashedToken = hashToken(token);

        return userRepository.findByMagicLinkToken(hashedToken)
                .filter(u -> u.getMagicLinkTokenExpiration().isAfter(Instant.now(clock)))
                .map(user -> {
                    user.setMagicLinkToken(null);
                    user.setMagicLinkTokenExpiration(null);
                    userRepository.save(user);
                    return generateAuthResponse(user);
                })
                .orElseThrow(() -> new ForbiddenException(AuthenticationErrorCode.FAILED));
    }

    @Transactional
    public PublicKeyCredentialCreationOptions startPasskeyRegistration(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ForbiddenException(AuthenticationErrorCode.FAILED));

        return webAuthnService.startRegistration(user);
    }

    @Transactional
    public PublicKeyCredentialRequestOptions startPasskeyLogin(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ForbiddenException(AuthenticationErrorCode.FAILED));

        return webAuthnService.startAuthentication(user);
    }

    @Transactional
    public void finishPasskeyRegistration(String email, String name, RegistrationFinishRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ForbiddenException(AuthenticationErrorCode.FAILED));

        webAuthnService.finishRegistration(user, name, request);
    }

    @Transactional
    public AuthenticationResponse finishPasskeyLogin(String email, AuthenticationFinishRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ForbiddenException(AuthenticationErrorCode.FAILED));

        webAuthnService.finishLogin(user, request);

        return generateAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public List<PasskeyResponse> getUserPasskeys(String userId) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ForbiddenException(AuthenticationErrorCode.FAILED));
        return webAuthnService.getPasskeys(user);
    }

    @Transactional
    public void renamePasskey(String userId, String passkeyId, String name) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ForbiddenException(AuthenticationErrorCode.FAILED));
        webAuthnService.renamePasskey(user, passkeyId, name);
    }

    @Transactional
    public void deletePasskey(String userId, String passkeyId) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ForbiddenException(AuthenticationErrorCode.FAILED));
        webAuthnService.deletePasskey(user, passkeyId);
    }

    @Transactional
    public java.util.Map<String, Object> startDiscoverableLogin() {
        return webAuthnService.startDiscoverableLogin();
    }

    public String generateChallengeToken(String challenge) {
        return jwtManager.generateChallengeToken(challenge);
    }

    @Transactional
    public AuthenticationResponse finishDiscoverableLogin(String challengeToken, AuthenticationFinishRequest request) {
        String challenge = jwtManager.extractChallenge(challengeToken);
        if (challenge == null)
            throw new ForbiddenException(AuthenticationErrorCode.FAILED);

        User user = webAuthnService.finishDiscoverableLogin(challenge, request);
        return generateAuthResponse(user);
    }

    private User registerNewUser(String email) {
        User user = User.builder()
                .email(email)
                .build();
        return userRepository.save(user);
    }

    private AuthenticationResponse generateAuthResponse(User user) {
        String accessToken = jwtManager.generateToken(user);
        return AuthenticationResponse.builder()
                .token(accessToken)
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .expiresIn(jwtManager.getExpirationTime())
                .build();
    }

    private String hashToken(String token) {
        return DigestUtils.sha256Hex(token);
    }

}