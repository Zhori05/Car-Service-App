package app.appointment.service;

import app.appointment.model.Appointment;
import app.appointment.repository.AppointmentRepository;
import app.car.model.Car;
import app.user.model.User;
import app.web.dto.AppointmentRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Appointment addAppointment(AppointmentRequest appointmentRequest , User user){
        Appointment appointment =appointmentRepository.save(initializeAppointment(appointmentRequest,user));

        return appointment;
    }

    private Appointment initializeAppointment(AppointmentRequest appointmentRequest, User user) {
        return Appointment.builder()
                .owner(user)
                .car(appointmentRequest.getCar())
                .serviceForCar(appointmentRequest.getServiceForCars())
                .mechanic(appointmentRequest.getMechanic())
                .start(appointmentRequest.getStart())
                .finish(appointmentRequest.getStart())
                .isFinished(false)
                .moreInfo(appointmentRequest.getMoreInfo())
                .build();
    }

    public List<Appointment> getTodayAppointmentsForMechanic(UUID mechanicId) {
        return appointmentRepository.findTodayAppointmentsByMechanic(mechanicId);
    }
    public List<Appointment> getAppointmentsForNextWeekByMechanic(UUID mechanicId) {
        return appointmentRepository.findAppointmentsForNextWeekByMechanic(mechanicId);
    }



}
