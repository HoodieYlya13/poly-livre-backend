package com.poly.livre.backend.models.dtos;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ToggleFavoriteRequestDto(@NotNull UUID userId) {
}
