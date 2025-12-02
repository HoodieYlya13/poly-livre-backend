package com.poly.livre.backend.controllers;

import com.poly.livre.backend.models.dtos.AuthenticationRequest;
import com.poly.livre.backend.models.dtos.AuthenticationResponse;
import com.poly.livre.backend.services.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Operation(summary = "Authenticate a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationResponse.class)) }),
            @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    @PostMapping(path = "/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody @Valid AuthenticationRequest dto) {
        return ResponseEntity.ok(authenticationService.authenticateUser(dto));
    }

}

// TODO : Modify for magic link and passkey