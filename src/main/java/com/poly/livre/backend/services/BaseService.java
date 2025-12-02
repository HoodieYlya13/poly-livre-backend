package com.poly.livre.backend.services;

import com.poly.livre.backend.models.auth.CustomPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;
import java.util.Optional;

public interface BaseService {

    default Optional<CustomPrincipal> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (Objects.nonNull(authentication) && authentication.getPrincipal() instanceof CustomPrincipal user)
            return Optional.of(user);
        return Optional.empty();
    }

}