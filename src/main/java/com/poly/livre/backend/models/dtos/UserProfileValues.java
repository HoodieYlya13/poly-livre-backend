package com.poly.livre.backend.models.dtos;

import jakarta.validation.constraints.NotNull;

public record UserProfileValues(
        @NotNull String username,
        @NotNull String firstName,
        @NotNull String lastName,
        @NotNull String status) {
}
