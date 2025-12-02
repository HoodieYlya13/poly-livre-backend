package com.poly.livre.backend.models.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AuthenticationRequest {

    @NotEmpty
    private String email;

    @NotEmpty
    private String password;
}

// TODO : Modify for magic link and passkey