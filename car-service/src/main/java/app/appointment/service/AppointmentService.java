package app.appointment.service;

import app.Exception.DomainException;
import app.appointment.model.Appointment;
import app.appointment.repository.AppointmentRepository;
import app.car.model.Car;
import app.serviceForCars.model.ServiceForCar;
import app.user.model.User;
import app.web.dto.AppointmentRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional
    public Appointment addAppointment(AppointmentRequest appointmentRequest, User user) {
        // Проверка за наличност на механика
        checkMechanicAvailability(appointmentRequest);

        // Проверка за работно време
        checkWorkingHours(appointmentRequest);

        // Създаване и записване на appointment
        Appointment appointment = appointmentRepository.save(initializeAppointment(appointmentRequest, user));

        return appointment;
    }

    private void checkMechanicAvailability(AppointmentRequest appointmentRequest) {
        LocalDateTime start = appointmentRequest.getStart();
        ServiceForCar service = appointmentRequest.getServiceForCars();
        LocalDateTime end = start.plusMinutes(service.getMinutesToFinish());

        // Взимаме всички активни appointments за механика
        List<Appointment> mechanicAppointments = appointmentRepository
                .findByMechanicIdAndIsFinishedFalse(appointmentRequest.getMechanic().getId());

        for (Appointment existing : mechanicAppointments) {
            LocalDateTime existingStart = existing.getStart();
            LocalDateTime existingEnd = existing.getFinish();

            // 2.2 Проверка дали има appointment, който започва преди и завършва след нашето начало
            if (existingStart.isBefore(start) && existingEnd.isAfter(start)) {
                throw new IllegalArgumentException("Механикът е зает по това време");
            }

            // 2.3 Проверка за съвпадащо начало
            if (existingStart.equals(start)) {
                throw new IllegalArgumentException("Механикът е зает по това време");
            }

            // 2.4 Проверка дали новия appointment се навлиза в съществуващ
            if (start.isBefore(existingEnd) && end.isAfter(existingStart)) {
                throw new IllegalArgumentException("Механикът е зает по това време");
            }
        }
    }

    private void checkWorkingHours(AppointmentRequest appointmentRequest) {
        LocalDateTime start = appointmentRequest.getStart();
        ServiceForCar service = appointmentRequest.getServiceForCars();
        LocalDateTime end = start.plusMinutes(service.getMinutesToFinish());

        // Проверка дали услугата започва и завършва в един и същи ден
        if (!start.toLocalDate().equals(end.toLocalDate())) {
            throw new IllegalArgumentException("Услугата не може да започва в един ден и да завършва в друг");
        }

        DayOfWeek dayOfWeek = start.getDayOfWeek();
        LocalTime startTime = start.toLocalTime();
        LocalTime endTime = end.toLocalTime();

        // Проверка за почивен ден (неделя)
        if (dayOfWeek == DayOfWeek.SUNDAY) {
            throw new IllegalArgumentException("Дадения час не влиза в работното време (неделя е почивен ден)");
        }

        // Проверка за работно време според деня
        if (dayOfWeek == DayOfWeek.SATURDAY) {
            // Събота: 10:00-16:00
            if (startTime.isBefore(LocalTime.of(10, 0)) || startTime.isAfter(LocalTime.of(16, 0))) {
                throw new IllegalArgumentException("Дадения час не влиза в работното време (сервизът работи от 10:00 до 16:00 в събота)");
            }
            if (endTime.isAfter(LocalTime.of(16, 0))) {
                throw new IllegalArgumentException("Услугата не може да завърши след 16:00 в събота");
            }
        } else {
            // Понеделник-Петък: 9:00-18:00
            if (startTime.isBefore(LocalTime.of(9, 0)) || startTime.isAfter(LocalTime.of(18, 0))) {
                throw new IllegalArgumentException("Дадения час не влиза в работното време (сервизът работи от 9:00 до 18:00)");
            }
            if (endTime.isAfter(LocalTime.of(18, 0))) {
                throw new IllegalArgumentException("Услугата не може да завърши след 18:00 през седмицата");
            }
        }
    }

    private Appointment initializeAppointment(AppointmentRequest appointmentRequest, User user) {
        // Добавяме 30 минути допълнително време към крайното време
        LocalDateTime finish = appointmentRequest.getStart()
                .plusMinutes(appointmentRequest.getServiceForCars().getMinutesToFinish())
                .plusMinutes(30);

        return Appointment.builder()
                .owner(user)
                .car(appointmentRequest.getCar())
                .serviceForCar(appointmentRequest.getServiceForCars())
                .mechanic(appointmentRequest.getMechanic())
                .start(appointmentRequest.getStart())
                .finish(finish)
                .isFinished(false)
                .moreInfo(appointmentRequest.getMoreInfo())
                .build();
    }

    public List<Appointment> getAppointments() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> getTodayAppointmentsForMechanic(UUID mechanicId) {
        return appointmentRepository.findTodayAppointmentsByMechanic(mechanicId);
    }

    public List<Appointment> getAppointmentsForNextWeekByMechanic(UUID mechanicId) {
        return appointmentRepository.findAppointmentsForNextWeekByMechanic(mechanicId);
    }

    public List<Appointment> findByMechanicIdAndIsFinishedFalse(UUID mechanicId) {
        return appointmentRepository.findByMechanicIdAndIsFinishedFalse(mechanicId);
    }

    public List<Appointment> findByMechanicIdAndIsFinishedTrue(UUID mechanicId) {
        return appointmentRepository.findByMechanicIdAndIsFinishedTrue(mechanicId);
    }




    @CacheEvict(value = {"todayMechanicWork", "weekMechanicWork"}, allEntries = true)
    public void switchStatus(UUID id) {

        Appointment appointment = getById(id);

        // НАЧИН 1:
//        if (user.isActive()){
//            user.setActive(false);
//        } else {
//            user.setActive(true);
//        }

        // false -> true
        // true -> false
        appointment.setFinished(!appointment.isFinished());
        appointmentRepository.save(appointment);
    }


    private Appointment getById(UUID appointmentId) {
        return appointmentRepository.findById(appointmentId).orElseThrow(() -> new DomainException("Appointment with id [%s] does not exist.".formatted(appointmentId)));
    }
    public List<Appointment> getCompletedAppointmentsByCarId(UUID carId) {
        return appointmentRepository.findCompletedAppointmentsForCar(carId);
    }





}
