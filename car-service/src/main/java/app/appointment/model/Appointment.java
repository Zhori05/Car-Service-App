package app.appointment.model;

import app.car.model.Car;

import app.serviceForCars.model.ServiceForCar;
import app.user.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.apache.catalina.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private User owner;

    @ManyToOne
    private Car car;

    @ManyToOne
    private ServiceForCar serviceForCar;

    @ManyToOne
    private User mechanic;

    @Column(nullable = false)
    private LocalDateTime start;

    @Column(nullable = false)
    private LocalDateTime finish;

    @Column(nullable = false, name = "is_finished")
    private boolean isFinished;

    private String moreInfo;

}
