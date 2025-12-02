package com.poly.livre.backend.controllers;

import com.poly.livre.backend.models.dtos.AuthenticationResponse;
import com.poly.livre.backend.models.dtos.UserDto;
import com.poly.livre.backend.models.dtos.UserRegistrationDto;
import com.poly.livre.backend.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(path = "/users", produces = "application/json")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationResponse.class)) }),
            @ApiResponse(responseCode = "409", description = "User with the same email already exists")
    })
    @PostMapping(path = "/register")
    public ResponseEntity<UserDto> register(@RequestBody @Valid UserRegistrationDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerUser(dto));
    }

    @Operation(summary = "Get a user by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class)) }),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping(path = "/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

}

// TODO : Modify for magic link and passkey