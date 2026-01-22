package com.poly.livre.backend.models.dtos;

import com.poly.livre.backend.models.enums.DeliveryType;
import com.poly.livre.backend.models.enums.LocaleLanguage;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class BookDto {
    private UUID id;
    private String title;
    private String description;
    private String author;
    private String cover;
    private boolean favorite;
    private List<String> styles;
    private Double rating;
    private List<BookReviewDto> reviews;
    private Double price;
    private UserDto owner;
    private InformationDto information;
    private Integer loanDuration;
    private boolean loaned;
    private Instant createdAt;
    private Instant updatedAt;

    @Data
    @Builder
    public static class InformationDto {
        private Integer pages;
        private Integer year;
        private LocaleLanguage language;
        private DeliveryType delivery;
    }
}
