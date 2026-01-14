package com.poly.livre.backend.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poly.livre.backend.authentications.JwtAuthenticationFilter;
import com.poly.livre.backend.managers.JwtManager;
import com.poly.livre.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfiguration implements WebMvcConfigurer {

        @Value("${webauthn.origin}")
        private String frontEndOrigin;

        private final ObjectMapper objectMapper;
        private final JwtManager jwtManager;
        private final UserRepository userRepository;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(requests -> requests
                                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/error").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/v3/api-docs/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/swagger-ui/**").permitAll()

                                                .requestMatchers(HttpMethod.POST, "/auth/testing-mode").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/auth/magic-link/**").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/auth/passkey/**").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/users/**").permitAll()

                                                .anyRequest().authenticated())
                                .addFilterBefore(new JwtAuthenticationFilter(objectMapper, userRepository, jwtManager),
                                                AnonymousAuthenticationFilter.class);

                return http.build();
        }

        @Override
        public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/**")
                                .exposedHeaders(HttpHeaders.CONTENT_DISPOSITION)
                                .allowedOrigins(frontEndOrigin)
                                .allowedMethods("*")
                                .allowCredentials(true);
        }

}