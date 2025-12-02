package com.poly.livre.backend.controllers;

import com.poly.livre.backend.models.dtos.AuthenticationFinishRequest;
import com.poly.livre.backend.models.dtos.AuthenticationResponse;
import com.poly.livre.backend.models.dtos.RegistrationFinishRequest;
import com.poly.livre.backend.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.webauthn4j.data.PublicKeyCredentialCreationOptions;
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
    public void finishPasskeyRegistration(@RequestParam String email, @RequestBody RegistrationFinishRequest request) {
        authenticationService.finishPasskeyRegistration(email, request);
    }

    @PostMapping("/passkey/login/start")
    public PublicKeyCredentialRequestOptions startPasskeyLogin(@RequestParam String email) {
        return authenticationService.startPasskeyLogin(email);
    }

    @PostMapping("/passkey/login/finish")
    public AuthenticationResponse finishPasskeyLogin(@RequestParam String email,
            @RequestBody AuthenticationFinishRequest request) {
        return authenticationService.finishPasskeyLogin(email, request);
    }
}