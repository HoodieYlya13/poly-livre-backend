package com.poly.livre.backend.controllers;

import com.poly.livre.backend.models.dtos.AuthenticationFinishRequest;
import com.poly.livre.backend.models.dtos.AuthenticationResponse;
import com.poly.livre.backend.models.dtos.RegistrationFinishRequest;
import com.poly.livre.backend.services.AuthenticationService;
import com.webauthn4j.data.PublicKeyCredentialCreationOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webauthn4j.data.PublicKeyCredentialRequestOptions;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/magic-link/request")
    public void requestMagicLink(@RequestParam String email) {
        authenticationService.requestMagicLink(email);
    }

    @PostMapping("/magic-link/verify")
    public AuthenticationResponse verifyMagicLink(@RequestParam String token) {
        return authenticationService.verifyMagicLink(token);
    }

    @PostMapping("/passkey/register/start")
    public PublicKeyCredentialCreationOptions startPasskeyRegistration(@RequestParam String email) {
        return authenticationService.startPasskeyRegistration(email);
    }

    @PostMapping("/passkey/register/finish")
    public void finishPasskeyRegistration(@RequestParam String email, @RequestParam String name,
            @RequestBody RegistrationFinishRequest request) {
        authenticationService.finishPasskeyRegistration(email, name, request);
    }

    @PostMapping("/passkey/login/start")
    public PublicKeyCredentialRequestOptions startPasskeyLogin(@RequestParam(required = false) String email,
            jakarta.servlet.http.HttpServletResponse response) {
        if (email != null)
            return authenticationService.startPasskeyLogin(email);
        else {
            java.util.Map<String, Object> result = authenticationService.startDiscoverableLogin();
            String challenge = (String) result.get("challenge");
            String token = authenticationService.generateChallengeToken(challenge);

            jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("challenge_token", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(false); // Should be true in production
            cookie.setPath("/");
            cookie.setMaxAge(300); // 5 minutes
            response.addCookie(cookie);

            return (PublicKeyCredentialRequestOptions) result.get("options");
        }
    }

    @PostMapping("/passkey/login/finish")
    public AuthenticationResponse finishPasskeyLogin(
            @RequestParam(required = false) String email,
            @RequestBody AuthenticationFinishRequest request,
            @CookieValue(value = "challenge_token", required = false) String challengeToken,
            jakarta.servlet.http.HttpServletResponse response) {

        if (email != null)
            return authenticationService.finishPasskeyLogin(email, request);
        else {
            if (challengeToken == null)
                throw new RuntimeException("Challenge token missing");

            jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("challenge_token", null);
            cookie.setHttpOnly(true);
            cookie.setSecure(false);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);

            return authenticationService.finishDiscoverableLogin(challengeToken, request);
        }
    }

}