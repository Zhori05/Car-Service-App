package com.example.car_service.web;

import app.appointment.service.AppointmentService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.service.UserService;
import app.web.UserController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerApiTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private AppointmentService appointmentService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        // Конфигурираме ViewResolver за тестовете
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".html"); //

        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    void getActiveUsers_AdminAccess_ShouldReturnUsersView() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata auth = new AuthenticationMetadata(userId, "admin", "pass", UserRole.ADMIN, true);

        User user1 = User.builder()
                .id(UUID.randomUUID())
                .username("user1")
                .email("user1@test.com")
                .role(UserRole.USER)
                .isActive(true)
                .build();

        List<User> mockUsers = List.of(user1);

        when(userService.getActiveUsers()).thenReturn(mockUsers);

        // Act & Assert
        mockMvc.perform(get("/users").principal(auth))
                .andExpect(status().isOk())
                .andExpect(view().name("users"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attribute("users", mockUsers));
    }

    @Test
    void switchUserStatus_ShouldCallServiceAndRedirect() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(put("/users/{id}/status", userId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));

        verify(userService).switchStatus(userId);
    }

    @Test
    void switchUserRole_ShouldCallServiceAndRedirect() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(put("/users/{id}/role", userId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));

        verify(userService).switchRole(userId);
    }

//    @Test
//    @WithMockUser(roles = "USER")
//    void getActiveUsers_NonAdminAccess_ShouldDenyAccess() throws Exception {
//        mockMvc.perform(get("/users"))
//                .andExpect(status().isForbidden());
//    }

//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void getActiveUsers_AdminAccess_ShouldAllowAccess() throws Exception {
//        when(userService.getActiveUsers()).thenReturn(List.of());
//
//        mockMvc.perform(get("/users"))
//                .andExpect(status().isOk());
//    }
}