package app.web.dto;

import app.car.model.Car;
import app.serviceForCars.model.ServiceForCar;
import app.user.model.User;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentRequest {


    @NotNull
    private Car car;
    @NotNull
    private ServiceForCar serviceForCars;
    @NotNull
    private User mechanic;

    @NotNull
    private LocalDateTime start;

    @NotNull
    private String moreInfo;
}
