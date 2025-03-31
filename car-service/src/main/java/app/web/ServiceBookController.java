package app.web;

import app.appointment.model.Appointment;
import app.appointment.service.AppointmentService;
import app.car.model.Car;
import app.car.service.CarService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import ch.qos.logback.core.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/serviceBook")
public class ServiceBookController {
    private final UserService userService;
    private final CarService carService;
    private final AppointmentService appointmentService;

    @Autowired
    public ServiceBookController(UserService userService, CarService carService, AppointmentService appointmentService) {
        this.userService = userService;
        this.carService = carService;
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public ModelAndView getServiceBookPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
                                           @RequestParam(required = false) UUID carId) {
        User user = userService.getById(authenticationMetadata.getUserId());
        List<Car> userCars = carService.getCarsByOwnerId(user.getId());

        List<Appointment> completedAppointments = (carId != null)
                ? appointmentService.getCompletedAppointmentsByCarId(carId).stream()
                .filter(Appointment::isFinished)
                .toList()
                : List.of(); // Празен списък, ако няма избрана кола

        ModelAndView modelAndView = new ModelAndView("serviceBook");
        modelAndView.addObject("user", user);
        modelAndView.addObject("userCars", userCars);
        modelAndView.addObject("completedAppointments", completedAppointments);
        modelAndView.addObject("selectedCarId", carId); // За да знаем кой автомобил е избран

        return modelAndView;
    }

    @PostMapping
    public ModelAndView selectCar(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
                                  @RequestParam("carId") UUID carId) {
        return getServiceBookPage(authenticationMetadata, carId);
    }


}
