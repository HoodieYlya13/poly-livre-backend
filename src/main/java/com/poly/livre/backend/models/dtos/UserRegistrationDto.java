package com.poly.livre.backend.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationDto {

    @NotEmpty
    private String username;

    @Email
    private String email;

    @NotEmpty
    private String password;

}

// TODO : Modify for magic link and passkey