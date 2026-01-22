package com.poly.livre.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poly.livre.backend.managers.JwtManager;
import com.poly.livre.backend.models.dtos.UserDto;
import com.poly.livre.backend.models.dtos.UserProfileValues;
import com.poly.livre.backend.models.enums.UserStatus;
import com.poly.livre.backend.repositories.UserRepository;
import com.poly.livre.backend.repositories.BookReviewRepository;
import com.poly.livre.backend.services.TestimonialService;
import com.poly.livre.backend.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private TestimonialService testimonialService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtManager jwtManager;

    @MockBean
    private BookReviewRepository bookReviewRepository;

    @Test
    void shouldCreateProfile_WhenInputIsValid() throws Exception {
        UUID userId = UUID.randomUUID();
        String username = "testuser";
        UserProfileValues values = new UserProfileValues(username, "Test", "User", "STUDENT");

        UserDto expectedDto = UserDto.builder()
                .id(userId)
                .username(username)
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .status(UserStatus.STUDENT)
                .build();

        when(userService.createProfile(eq(username), any(UserProfileValues.class))).thenReturn(expectedDto);

        mockMvc.perform(post("/users/{username}", username)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(values)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.status").value("STUDENT"));
    }

    // @Test
    // void shouldReturnBadRequest_WhenInputIsInvalid() throws Exception {

    // }
}
