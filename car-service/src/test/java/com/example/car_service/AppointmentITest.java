package com.example.car_service;

import app.CarServiceApplication;
import app.appointment.model.Appointment;
import app.appointment.repository.AppointmentRepository;
import app.appointment.service.AppointmentService;
import app.car.model.Brand;
import app.car.model.Car;
import app.car.repository.CarRepository;
import app.serviceForCars.model.ServiceForCar;
import app.serviceForCars.model.Special;
import app.serviceForCars.repository.ServiceForCarRepository;
import app.user.model.*;
import app.user.repository.UserRepository;
import app.user.service.UserService;
import app.web.dto.AppointmentRequest;
import app.web.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ActiveProfiles("test")
@SpringBootTest(classes = CarServiceApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class AppointmentITest {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ServiceForCarRepository serviceForCarRepository;

    private User testUser;
    private User testMechanic;
    private Car testCar;
    private ServiceForCar testService;

    @BeforeEach
    void setUp() {
        // Create and save test user
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("0881234567")
                .build();
        testUser = userService.register(registerRequest);

        // Create and save test mechanic
        testMechanic = User.builder()
                .username("mechanic1")
                .email("mechanic@example.com")
                .password("mech123")
                .phoneNumber("0887654321")
                .role(UserRole.MECHANIC)
                .isActive(true)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
        testMechanic = userRepository.save(testMechanic);

        // Create and save test car
        testCar = Car.builder()
                .brand(Brand.BMW)
                .model("Corolla")
                .year(2020)
                .color("Red")
                .licensePlate("CA1234AB")
                .mileage("50000")
                .owner(testUser)
                .build();
        testCar = carRepository.save(testCar);

        // Create and save test service
        testService = ServiceForCar.builder()
                .name("Oil Change")
                .minutesToFinish(30)
                .price(50.0)
                .special(Special.ENGINE)
                .build();
        testService = serviceForCarRepository.save(testService);
    }
    @Test
    void createAppointment_ShouldSuccessfullyCreateAppointment() {
        // Arrange
        LocalDateTime appointmentTime = getValidWorkingTime();
        AppointmentRequest request = buildAppointmentRequest(appointmentTime);

        // Act
        Appointment result = appointmentService.addAppointment(request, testUser);

        // Assert
        assertNotNull(result.getId());
        assertEquals(appointmentTime, result.getStart());
        assertEquals(testUser, result.getOwner());
        assertEquals(testMechanic, result.getMechanic());
        assertEquals(testCar, result.getCar());
        assertEquals(testService, result.getServiceForCar());
        assertFalse(result.isFinished());
    }

    @Test
    void createAppointment_ShouldFailWhenMechanicIsBusy() {
        // Arrange - create first appointment
        LocalDateTime firstAppointmentTime = getValidWorkingTime();
        AppointmentRequest firstRequest = buildAppointmentRequest(firstAppointmentTime);
        appointmentService.addAppointment(firstRequest, testUser);

        // Try to create overlapping appointment
        LocalDateTime overlappingTime = firstAppointmentTime.plusMinutes(15);
        AppointmentRequest overlappingRequest = buildAppointmentRequest(overlappingTime);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            appointmentService.addAppointment(overlappingRequest, testUser);
        });

        assertThat(exception.getMessage(), containsString("Механикът е зает"));
    }

    @Test
    void createAppointment_ShouldFailWhenOutsideWorkingHours() {
        // Sunday appointment
        LocalDateTime sundayTime = LocalDateTime.now()
                .with(DayOfWeek.SUNDAY)
                .withHour(10).withMinute(0);

        AppointmentRequest request = buildAppointmentRequest(sundayTime);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            appointmentService.addAppointment(request, testUser);
        });
        assertThat(exception.getMessage(), containsString("неделя е почивен ден"));
    }

    @Test
    void getTodayAppointmentsForMechanic_ShouldReturnOnlyTodayAppointments() {
        // Arrange
        LocalDateTime todayTime = LocalDateTime.now()
                .withHour(10).withMinute(0);
        LocalDateTime tomorrowTime = todayTime.plusDays(1);

        Appointment todayAppointment = createTestAppointment(todayTime);
        createTestAppointment(tomorrowTime);

        // Act
        List<Appointment> result = appointmentService.getTodayAppointmentsForMechanic(testMechanic.getId());

        // Assert
        assertEquals(1, result.size());
        assertEquals(todayAppointment.getId(), result.get(0).getId());
    }

    @Test
    void switchStatus_ShouldToggleFinishedStatus() {
        // Arrange
        Appointment appointment = createTestAppointment(getValidWorkingTime());
        boolean initialStatus = appointment.isFinished();

        // Act
        appointmentService.switchStatus(appointment.getId());

        // Assert
        Appointment updated = appointmentRepository.findById(appointment.getId()).orElseThrow();
        assertEquals(!initialStatus, updated.isFinished());
    }

    private AppointmentRequest buildAppointmentRequest(LocalDateTime startTime) {
        return AppointmentRequest.builder()
                .car(testCar)
                .serviceForCars(testService)
                .mechanic(testMechanic)
                .start(startTime)
                .build();
    }

    private Appointment createTestAppointment(LocalDateTime startTime) {
        AppointmentRequest request = buildAppointmentRequest(startTime);
        return appointmentService.addAppointment(request, testUser);
    }

    private LocalDateTime getValidWorkingTime() {
        LocalDateTime now = LocalDateTime.now();
        DayOfWeek dayOfWeek = now.getDayOfWeek();

        // Adjust time to be within working hours
        if (dayOfWeek == DayOfWeek.SATURDAY) {
            return now.withHour(10).withMinute(0);
        } else if (dayOfWeek == DayOfWeek.SUNDAY) {
            // Move to Monday if it's Sunday
            return now.plusDays(1).withHour(9).withMinute(0);
        } else {
            return now.withHour(9).withMinute(0);
        }
    }
}