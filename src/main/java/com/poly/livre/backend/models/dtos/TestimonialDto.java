package com.poly.livre.backend.models.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class TestimonialDto {
    private UUID id;
    private UserDto user;
    private Integer rating;
    private String comment;
}
