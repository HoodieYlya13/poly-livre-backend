package com.poly.livre.backend.models.auth;

import com.poly.livre.backend.models.entities.User;
import lombok.*;

import java.util.UUID;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class CustomPrincipal {

    public CustomPrincipal (User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
    }

    private UUID id;
    private String username;
    private String email;

}

// TODO : Modify for magic link and passkey 