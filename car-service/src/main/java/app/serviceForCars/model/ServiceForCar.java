package app.serviceForCars.model;

import app.appointment.model.Appointment;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ServiceForCar {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false,unique = true)
    private String name;

    @Column(nullable = false)
    private int minutesToFinish;
    @Column(nullable = false)
    private double price;
    @Enumerated(EnumType.STRING)
    private Special special;

    @OneToMany(fetch = FetchType.EAGER,mappedBy = "serviceForCar")
    private List<Appointment> appointments;
}
