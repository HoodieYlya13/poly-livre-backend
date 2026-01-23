package com.poly.livre.backend.controllers;

import com.poly.livre.backend.models.dtos.AuthenticationFinishRequest;
import com.poly.livre.backend.models.dtos.AuthenticationResponse;
import com.poly.livre.backend.models.dtos.RegistrationFinishRequest;
import com.poly.livre.backend.services.AuthenticationService;
import com.webauthn4j.data.PublicKeyCredentialCreationOptions;
import lombok.RequiredArgsConstructor;

import com.webauthn4j.data.PublicKeyCredentialRequestOptions;

import org.springframework.beans.factory.annotation.Value;
import com.poly.livre.backend.models.dtos.PasskeyResponse;
import com.poly.livre.backend.models.auth.CustomPrincipal;
import com.poly.livre.backend.exceptions.ForbiddenException;
import com.poly.livre.backend.exceptions.errors.AuthenticationErrorCode;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.poly.livre.backend.models.dtos.AuthenticationRequests.*;
import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Authentication", description = "Endpoints for user authentication and passkey management")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Value("${app.password}")
    private String appPassword;

    @io.swagger.v3.oas.annotations.Operation(summary = "Verify testing mode", description = "Verifies if the testing mode password is correct.")
    @PostMapping("/testing-mode")
    public boolean verifyTestingModePassword(@RequestBody String password) {
        return appPassword.equals(password);
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Logout", description = "Invalidates the user's session token.")
    @PostMapping("/logout")
    public void logout(@RequestHeader("Authorization") String token) {
        authenticationService.logout(token);
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Request magic link", description = "Sends a magic link to the user's email for passwordless login.")
    @PostMapping("/magic-link/request")
    public void requestMagicLink(@RequestBody MagicLinkRequest request) {
        authenticationService.requestMagicLink(request.getEmail());
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Verify magic link", description = "Verifies the magic link token and authenticates the user.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Authentication successful")
    @PostMapping("/magic-link/verify")
    public AuthenticationResponse verifyMagicLink(@RequestBody MagicLinkVerifyRequest request) {
        return authenticationService.verifyMagicLink(request.getToken());
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Start passkey registration", description = "Initiates the WebAuthn registration process for a new passkey.")
    @PostMapping("/passkey/register/start")
    public PublicKeyCredentialCreationOptions startPasskeyRegistration(
            @RequestBody PasskeyRegisterStartRequest request) {
        return authenticationService.startPasskeyRegistration(request.getEmail());
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Finish passkey registration", description = "Completes the WebAuthn registration process.")
    @PostMapping("/passkey/register/finish")
    public void finishPasskeyRegistration(@RequestBody RegistrationFinishRequest request) {
        authenticationService.finishPasskeyRegistration(request.getEmail(), request.getPasskeyName(), request);
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Start passkey login", description = "Initiates the WebAuthn login process.")
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
            cookie.setSecure(false); // TODO: Should be true in production
            cookie.setPath("/");
            cookie.setMaxAge(300); // 5 minutes // TODO: env var ?
            response.addCookie(cookie);

            return (PublicKeyCredentialRequestOptions) result.get("options");
        }
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Finish passkey login", description = "Completes the WebAuthn login process.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Authentication successful")
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

    @io.swagger.v3.oas.annotations.Operation(summary = "Get user passkeys", description = "Retrieves all passkeys registered to the user.")
    @GetMapping("/passkeys/{userId}")
    public List<PasskeyResponse> getUserPasskeys(@PathVariable String userId,
            @AuthenticationPrincipal CustomPrincipal user) {
        if (!user.getId().toString().equals(userId)) {
            throw new ForbiddenException(AuthenticationErrorCode.FAILED);
        }
        return authenticationService.getUserPasskeys(userId);
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Rename passkey", description = "Renames a specific passkey.")
    @PutMapping("/passkeys/{userId}/{passkeyId}")
    public void renamePasskey(@PathVariable String userId, @PathVariable String passkeyId,
            @RequestBody RenamePasskeyRequest request,
            @AuthenticationPrincipal CustomPrincipal user) {
        if (!user.getId().toString().equals(userId)) {
            throw new ForbiddenException(AuthenticationErrorCode.FAILED);
        }
        authenticationService.renamePasskey(userId, passkeyId, request.getPasskeyName());
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Delete passkey", description = "Deletes a specific passkey.")
    @DeleteMapping("/passkeys/{userId}/{passkeyId}")
    public void deletePasskey(@PathVariable String userId, @PathVariable String passkeyId,
            @AuthenticationPrincipal CustomPrincipal user) {
        if (!user.getId().toString().equals(userId)) {
            throw new ForbiddenException(AuthenticationErrorCode.FAILED);
        }
        authenticationService.deletePasskey(userId, passkeyId);
    }

}