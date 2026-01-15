package com.poly.livre.backend.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity(name = "USERS")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class User extends AuditDateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "USERNAME", columnDefinition = "VARCHAR(255)")
    private String username;

    @Column(name = "EMAIL", columnDefinition = "VARCHAR(255)", nullable = false, unique = true)
    private String email;

    @Column(name = "MAGIC_LINK_TOKEN", columnDefinition = "VARCHAR(255)")
    private String magicLinkToken;

    @Column(name = "MAGIC_LINK_TOKEN_EXPIRATION", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant magicLinkTokenExpiration;

    @Column(name = "CURRENT_CHALLENGE", columnDefinition = "VARCHAR(255)")
    private String currentChallenge;

    @Column(name = "LAST_LOGOUT_AT", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant lastLogoutAt;

}