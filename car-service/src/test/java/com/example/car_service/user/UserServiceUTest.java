package com.example.car_service.user;



import app.Exception.DomainException;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.repository.UserRepository;
import app.user.service.UserService;
import app.web.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceUTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private RegisterRequest registerRequest;
    private final UUID testUserId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(testUserId)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .phoneNumber("1234567890")
                .role(UserRole.USER)
                .isActive(true)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("new@example.com");
        registerRequest.setPassword("password");
        registerRequest.setPhoneNumber("9876543210");
    }

    @Test
    void register_NewUser_Success() {
        // Arrange
        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.register(registerRequest);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        verify(userRepository, times(1)).findByUsernameOrEmail(registerRequest.getUsername(), registerRequest.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_ExistingUser_ThrowsException() {
        // Arrange
        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.of(testUser));

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class,
                () -> userService.register(registerRequest));
        assertEquals("Username with this email or username already exist.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getById_ExistingUser_ReturnsUser() {
        // Arrange
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.getById(testUserId);

        // Assert
        assertNotNull(result);
        assertEquals(testUserId, result.getId());
    }

    @Test
    void getById_NonExistingUser_ThrowsException() {
        // Arrange
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class,
                () -> userService.getById(testUserId));
        assertEquals("User with id [" + testUserId + "] does not exist.", exception.getMessage());
    }

    @Test
    void loadUserByUsername_ExistingUser_ReturnsCorrectAuthenticationMetadata() {
        // Arrange
        User testUser = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .password("encodedPass123")
                .role(UserRole.ADMIN)
                .isActive(true)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        UserDetails result = userService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof AuthenticationMetadata);

        AuthenticationMetadata authMetadata = (AuthenticationMetadata) result;

        // Verify basic UserDetails properties
        assertEquals("testuser", authMetadata.getUsername());
        assertEquals("encodedPass123", authMetadata.getPassword());
        assertTrue(authMetadata.isEnabled());

        // Verify custom properties
        assertEquals(testUser.getId(), authMetadata.getUserId());
        assertEquals(UserRole.ADMIN, authMetadata.getRole());
        assertTrue(authMetadata.isActive());

        // Verify authorities/roles
        Collection<? extends GrantedAuthority> authorities = authMetadata.getAuthorities();
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));

        // Verify account status methods
        assertTrue(authMetadata.isAccountNonExpired());
        assertTrue(authMetadata.isAccountNonLocked());
        assertTrue(authMetadata.isCredentialsNonExpired());
    }
    @Test
    void loadUserByUsername_NonExistingUser_ThrowsDomainException() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class,
                () -> userService.loadUserByUsername("nonexistent"));
        assertEquals("Потребител с това име не съществува.", exception.getMessage());
    }

    @Test
    void getAllUsers_ReturnsAllUsers() {
        // Arrange
        List<User> users = List.of(testUser);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertEquals(1, result.size());
        assertEquals(testUser, result.get(0));
    }

    @Test
    void getActiveUsers_ReturnsOnlyActiveUsers() {
        // Arrange
        User inactiveUser = User.builder().isActive(false).build();
        when(userRepository.findByIsActiveTrue()).thenReturn(List.of(testUser));

        // Act
        List<User> result = userService.getActiveUsers();

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get(0).isActive());
    }

    @Test
    void getNotActiveUsers_ReturnsOnlyInactiveUsers() {
        // Arrange
        User inactiveUser = User.builder().isActive(false).build();
        when(userRepository.findByIsActiveFalse()).thenReturn(List.of(inactiveUser));

        // Act
        List<User> result = userService.getNotActiveUsers();

        // Assert
        assertEquals(1, result.size());
        assertFalse(result.get(0).isActive());
    }

    @Test
    void getAllMechanics_ReturnsOnlyMechanics() {
        // Arrange
        User mechanic = User.builder().role(UserRole.MECHANIC).build();
        when(userRepository.findByRole(UserRole.MECHANIC)).thenReturn(List.of(mechanic));

        // Act
        List<User> result = userService.getAllMechanics();

        // Assert
        assertEquals(1, result.size());
        assertEquals(UserRole.MECHANIC, result.get(0).getRole());
    }

    @Test
    void getAllBasicUsers_ReturnsOnlyBasicUsers() {
        // Arrange
        when(userRepository.findByRole(UserRole.USER)).thenReturn(List.of(testUser));

        // Act
        List<User> result = userService.getAllBasicUsers();

        // Assert
        assertEquals(1, result.size());
        assertEquals(UserRole.USER, result.get(0).getRole());
    }

    @Test
    void getAllNonMechanics_ReturnsNonMechanicUsers() {
        // Arrange
        User admin = User.builder().role(UserRole.ADMIN).build();
        when(userRepository.findAllByRoleNot(UserRole.MECHANIC)).thenReturn(List.of(testUser, admin));

        // Act
        List<User> result = userService.getAllNonMechanics();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().noneMatch(u -> u.getRole() == UserRole.MECHANIC));
    }

    @Test
    void switchRole_UserToAdmin_Success() {
        // Arrange
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.switchRole(testUserId);

        // Assert
        assertEquals(UserRole.ADMIN, testUser.getRole());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void switchRole_AdminToUser_Success() {
        // Arrange
        testUser.setRole(UserRole.ADMIN);
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.switchRole(testUserId);

        // Assert
        assertEquals(UserRole.USER, testUser.getRole());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void switchRole_MechanicToAdmin_Success() {
        // Arrange
        testUser.setRole(UserRole.MECHANIC);
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.switchRole(testUserId);

        // Assert
        assertEquals(UserRole.ADMIN, testUser.getRole());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void switchMechanic_UserToMechanic_Success() {
        // Arrange
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.switchMechanic(testUserId);

        // Assert
        assertEquals(UserRole.MECHANIC, testUser.getRole());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void switchMechanic_MechanicToUser_Success() {
        // Arrange
        testUser.setRole(UserRole.MECHANIC);
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.switchMechanic(testUserId);

        // Assert
        assertEquals(UserRole.USER, testUser.getRole());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void switchStatus_ActiveToInactive_Success() {
        // Arrange
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.switchStatus(testUserId);

        // Assert
        assertFalse(testUser.isActive());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void switchStatus_InactiveToActive_Success() {
        // Arrange
        testUser.setActive(false);
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.switchStatus(testUserId);

        // Assert
        assertTrue(testUser.isActive());
        verify(userRepository, times(1)).save(testUser);
    }
}