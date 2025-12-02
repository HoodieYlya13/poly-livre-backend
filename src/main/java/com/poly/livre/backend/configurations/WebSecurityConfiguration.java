package com.poly.livre.backend.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poly.livre.backend.authentications.JwtAuthenticationFilter;
import com.poly.livre.backend.managers.JwtManager;
import com.poly.livre.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfiguration implements WebMvcConfigurer {

        private final ObjectMapper objectMapper;
        private final JwtManager jwtManager;
        private final UserRepository userRepository;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                        .csrf(csrf -> csrf
                                        .ignoringRequestMatchers(toH2Console())
                                        .disable())
                        .headers(headers -> headers
                                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                        .authorizeHttpRequests(requests -> requests
                                        .requestMatchers(HttpMethod.GET, "/actuator/**").permitAll()
                                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/favicon.ico").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/error").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/h2-console/**").permitAll()
                                        .requestMatchers(HttpMethod.POST, "/h2-console/**").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/v3/api-docs/**").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/swagger-ui/**").permitAll()

                                        .requestMatchers(HttpMethod.GET, "/hello-world/**").permitAll()
                                        .requestMatchers(HttpMethod.POST, "/hello-world/entity").permitAll()

                                        .requestMatchers(HttpMethod.POST, "/users/register").permitAll()
                                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()

                                        .requestMatchers(HttpMethod.GET, "/movies/**").permitAll()
                                        .anyRequest().authenticated())
                        .addFilterBefore(new JwtAuthenticationFilter(objectMapper, userRepository, jwtManager),
                                        AnonymousAuthenticationFilter.class);

                return http.build();
        }

        @Override
        public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/**")
                        .exposedHeaders(HttpHeaders.CONTENT_DISPOSITION)
                        .allowedOrigins("*")
                        .allowedMethods("*");
        }

}