package com.poly.livre.backend.configurations;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
@EnableJpaAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
@EnableTransactionManagement
public class PersistenceConfiguration {

    private final Clock clock;

    @Bean(name = "auditingDateTimeProvider")
    public DateTimeProvider dateTimeProvider() {
        return () -> Optional.of(Instant.now(clock));
    }

}
