package com.poly.livre.backend.models.dtos;

import com.poly.livre.backend.models.enums.DeliveryType;
import com.poly.livre.backend.models.enums.LocaleLanguage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.UUID;

public record BookRequestDto(
        @NotBlank String title,
        @NotBlank String author,
        @NotBlank String description,
        @NotNull @Positive Double price,
        @NotNull @Positive Integer loanDuration,
        List<String> styles,
        @NotNull InformationDto information,
        @NotNull UUID ownerId) {
    public record InformationDto(
            @NotNull @Positive Integer pages,
            @NotNull @Positive Integer year,
            @NotNull LocaleLanguage language,
            @NotNull DeliveryType delivery) {
    }
}
