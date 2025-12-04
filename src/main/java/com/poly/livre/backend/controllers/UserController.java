package com.poly.livre.backend.controllers;

import com.poly.livre.backend.models.dtos.UserDto;
import com.poly.livre.backend.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(path = "/users", produces = "application/json")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get a user by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class)) }),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping(path = "/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable @NonNull UUID userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @GetMapping(path = "/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUserDto());
    }

    @PutMapping(path = "/{username}")
    public ResponseEntity<UserDto> updateUsername(@PathVariable @NonNull String username) {
        return ResponseEntity.ok(userService.updateUsername(username));
    }

}