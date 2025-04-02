package com.example.car_service.web;



import app.car.model.Brand;
import app.car.service.CarService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.service.UserService;
import app.web.IndexController;
import app.web.dto.AddCarRequest;
import app.web.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.security.Principal;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class IndexControllerApiTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private CarService carService;

    @InjectMocks
    private IndexController indexController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(indexController)
                .setViewResolvers(new ViewResolver() {
                    @Override
                    public View resolveViewName(String viewName, Locale locale) throws Exception {
                        if (viewName.startsWith("redirect:")) {
                            return new RedirectView(viewName.substring("redirect:".length()));
                        }
                        return new MappingJackson2JsonView();
                    }
                })
                .build();
    }

    @Test
    void getIndexPage_ShouldReturnIndexView() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void getLoginPage_WithoutErrorParam_ShouldReturnLoginView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("loginRequest"))
                .andExpect(model().attributeDoesNotExist("errorMessage"));
    }

    @Test
    void getLoginPage_WithErrorParam_ShouldReturnLoginViewWithErrorMessage() throws Exception {
        mockMvc.perform(get("/login").param("error", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("loginRequest"))
                .andExpect(model().attribute("errorMessage", "Грешно име и парола!"));
    }

    @Test
    void getRegisterPage_ShouldReturnRegisterView() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("registerRequest"));
    }

    @Test
    void registerNewUser_WithValidRequest_ShouldRedirectToLogin() throws Exception {
        // Подготовка на данни
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password");
        registerRequest.setPhoneNumber("1234567890");

        // Изпълнение и проверки
        mockMvc.perform(post("/register")
                        .param("username", "testuser")
                        .param("email", "test@example.com")
                        .param("password", "password")
                        .param("phoneNumber", "1234567890"))
                .andExpect(status().is3xxRedirection())  // Проверка за пренасочване
                .andExpect(redirectedUrl("/login"));     // Проверка за точния URL

        // Проверка дали service методът е извикан
        verify(userService).register(any(RegisterRequest.class));
    }

    @Test
    void registerNewUser_WithInvalidRequest_ShouldReturnRegisterView() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "")
                        .param("email", "invalid-email")
                        .param("password", "short")
                        .param("phoneNumber", "123"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("registerRequest"));

        verifyNoInteractions(userService);
    }

    @Test
    void getAddCarPage_Authenticated_ShouldReturnAddCarView() throws Exception {
        UUID userId = UUID.randomUUID();
        User mockUser = new User();
        mockUser.setId(userId);

        AuthenticationMetadata auth = new AuthenticationMetadata(userId, "username", "password", UserRole.USER, true);

        when(userService.getById(userId)).thenReturn(mockUser);

        mockMvc.perform(get("/addCar").principal(auth))
                .andExpect(status().isOk())
                .andExpect(view().name("addCar"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("addCarRequest"));
    }

    @Test
    void addNewCar_WithValidRequest_ShouldProcessCar() throws Exception {
        // 1. Подготовка на тестови данни
        UUID userId = UUID.randomUUID();
        User mockUser = new User();
        mockUser.setId(userId);

        AuthenticationMetadata auth = new AuthenticationMetadata(userId, "username", "password", UserRole.USER, true);

        // 2. Настройка на моковете
        when(userService.getById(userId)).thenReturn(mockUser);

        // 3. Изпълнение на заявката с ВСИЧКИ задължителни полета
        mockMvc.perform(post("/addCar")
                        .principal(auth)
                        .param("brand", "BMW")
                        .param("model", "X5")
                        .param("year", "2020")
                        .param("color", "Black")
                        .param("licensePlate", "CA1234AB")
                        .param("mileage", "50000")
                        .param("picture", "car.jpg"))
                .andExpect(status().isOk());

        // 4. Проверки
        ArgumentCaptor<AddCarRequest> carRequestCaptor = ArgumentCaptor.forClass(AddCarRequest.class);
        verify(carService).addCar(carRequestCaptor.capture(), eq(mockUser));

        AddCarRequest capturedRequest = carRequestCaptor.getValue();
        assertEquals(Brand.BMW, capturedRequest.getBrand()); // Проверка за enum
        assertEquals("X5", capturedRequest.getModel());
        assertEquals(2020, capturedRequest.getYear());
    }

    @Test
    void addNewCar_WithInvalidRequest_ShouldReturnAddCarView() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata auth = new AuthenticationMetadata(userId, "username", "password", UserRole.USER, true);

        mockMvc.perform(post("/addCar")
                        .principal(auth)
                        .param("make", "")
                        .param("model", "")
                        .param("year", "invalid"))
                .andExpect(status().isOk())
                .andExpect(view().name("addCar"))
                .andExpect(model().attributeExists("addCarRequest"));

        verifyNoInteractions(carService);
    }
}