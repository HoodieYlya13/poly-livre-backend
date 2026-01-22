package com.poly.livre.backend.controllers;

import com.poly.livre.backend.models.dtos.UserDto;
import com.poly.livre.backend.models.dtos.UserProfileValues;
import com.poly.livre.backend.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
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
    private final com.poly.livre.backend.services.TestimonialService testimonialService;

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

    @Operation(summary = "Create user profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile created", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input or username mismatch"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping(path = "/{username}")
    public ResponseEntity<UserDto> createProfile(
            @PathVariable @NonNull String username,
            @RequestBody @Valid UserProfileValues values) {
        return ResponseEntity.ok(userService.createProfile(username, values));
    }

    @GetMapping(path = "/testimonials/{locale}")
    public ResponseEntity<java.util.List<com.poly.livre.backend.models.dtos.TestimonialDto>> getTestimonials(
            @PathVariable @NonNull String locale) {
        return ResponseEntity.ok(testimonialService.getTestimonialsByLocale(locale));
    }

}