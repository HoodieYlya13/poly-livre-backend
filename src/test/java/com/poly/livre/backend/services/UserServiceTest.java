package com.poly.livre.backend.services;

import com.poly.livre.backend.exceptions.ForbiddenException;
import com.poly.livre.backend.exceptions.NotFoundException;
import com.poly.livre.backend.managers.JwtManager;
import com.poly.livre.backend.models.auth.CustomPrincipal;
import com.poly.livre.backend.models.converters.UserConverter;
import com.poly.livre.backend.models.dtos.UserDto;
import com.poly.livre.backend.models.entities.User;
import com.poly.livre.backend.models.enums.UserStatus;
import com.poly.livre.backend.models.dtos.UserProfileValues;
import com.poly.livre.backend.exceptions.BadRequestException;
import com.poly.livre.backend.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserConverter userConverter;

    @Mock
    private JwtManager jwtManager;

    @InjectMocks
    private UserService userService;

    private UUID userId;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = User.builder()
                .id(userId)
                .username("testuser")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .status(UserStatus.STUDENT)
                .build();

        userDto = UserDto.builder()
                .id(userId)
                .username("testuser")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .status(UserStatus.STUDENT)
                .build();
    }

    @Test
    void shouldReturnUserById_WhenUserExists() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userConverter.convert(user)).thenReturn(userDto);

        UserDto result = userService.getUserById(userId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(userRepository).findById(userId);
        verify(userConverter).convert(user);
    }

    @Test
    void shouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void shouldReturnCurrentUserDto_WhenUserIsAuthenticated() {
        CustomPrincipal principal = new CustomPrincipal(user);

        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(principal);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(userConverter.convert(user)).thenReturn(userDto);
            when(jwtManager.getExpirationTime()).thenReturn(3600);

            UserDto result = userService.getCurrentUserDto();

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(userId);
            assertThat(result.getExpiresIn()).isEqualTo(3600);
        }
    }

    @Test
    void shouldThrowForbiddenException_WhenUserNotAuthenticated() {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);

            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(null);

            assertThatThrownBy(() -> userService.getCurrentUserDto())
                    .isInstanceOf(ForbiddenException.class);
        }
    }

    @Test
    void shouldUpdateUsername_WhenUserIsAuthenticated() {
        String newUsername = "newusername";
        CustomPrincipal principal = new CustomPrincipal(user);
        User updatedUser = User.builder()
                .id(userId)
                .username(newUsername)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .status(UserStatus.STUDENT)
                .build();

        UserDto updatedUserDto = UserDto.builder()
                .id(userId)
                .username(newUsername)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .status(UserStatus.STUDENT)
                .build();

        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(principal);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenReturn(updatedUser);
            when(userConverter.convert(updatedUser)).thenReturn(updatedUserDto);
            when(jwtManager.getExpirationTime()).thenReturn(3600);

            UserDto result = userService.updateUsername(newUsername);

            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo(newUsername);
            assertThat(result.getExpiresIn()).isEqualTo(3600);
            verify(userRepository).save(any(User.class));
            verify(userConverter).convert(updatedUser);
        }
    }

    @Test
    void shouldThrowForbiddenException_WhenUpdatingUsernameAndNotAuthenticated() {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);

            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(null);

            assertThatThrownBy(() -> userService.updateUsername("newusername"))
                    .isInstanceOf(ForbiddenException.class);
        }
    }

    @Test
    void shouldThrowNotFoundException_WhenUpdatingUsernameAndUserNotFound() {
        CustomPrincipal principal = new CustomPrincipal(user);

        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(principal);

            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateUsername("newusername"))
                    .isInstanceOf(ForbiddenException.class);
        }
    }

    @Test
    void shouldCreateProfile_WhenUserIsAuthenticatedAndDataIsValid() {
        UserProfileValues values = new UserProfileValues("newuser", "New", "User", "TEACHER");
        CustomPrincipal principal = new CustomPrincipal(user);

        User updatedUser = User.builder()
                .id(userId)
                .username("newuser")
                .email("test@example.com")
                .firstName("New")
                .lastName("User")
                .status(UserStatus.TEACHER)
                .build();

        UserDto expectedDto = UserDto.builder()
                .id(userId)
                .username("newuser")
                .email("test@example.com")
                .firstName("New")
                .lastName("User")
                .status(UserStatus.TEACHER)
                .build();

        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(principal);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenReturn(updatedUser);
            when(userConverter.convert(updatedUser)).thenReturn(expectedDto);
            when(jwtManager.getExpirationTime()).thenReturn(3600);

            UserDto result = userService.createProfile("newuser", values);

            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo("newuser");
            assertThat(result.getStatus()).isEqualTo(UserStatus.TEACHER);
            verify(userRepository).save(any(User.class));
        }
    }

    @Test
    void shouldThrowBadRequestException_WhenUsernameMismatch() {
        UserProfileValues values = new UserProfileValues("otheruser", "New", "User", "TEACHER");

        assertThatThrownBy(() -> userService.createProfile("username", values))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Username mismatch");
    }

    @Test
    void shouldThrowBadRequestException_WhenStatusIsInvalid() {
        UserProfileValues values = new UserProfileValues("newuser", "New", "User", "INVALID_STATUS");
        CustomPrincipal principal = new CustomPrincipal(user);

        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(principal);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            assertThatThrownBy(() -> userService.createProfile("newuser", values))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("Invalid status");
        }
    }
}
