package com.poly.livre.backend.models.dtos;

import com.poly.livre.backend.models.enums.LocaleLanguage;
import com.poly.livre.backend.models.enums.UserStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class BookReviewDto {
    private UUID reviewId;
    private UUID userId;
    private String username;
    private String firstName;
    private String lastName;
    private UserStatus status;
    private UUID bookId;
    private Integer rating;
    private String comment;
    private LocaleLanguage language;
    private Instant createdAt;
}
