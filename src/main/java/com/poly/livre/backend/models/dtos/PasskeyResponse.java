package com.poly.livre.backend.models.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class PasskeyResponse {
    private String id;
    private String name;
    private Instant createdAt;
}
