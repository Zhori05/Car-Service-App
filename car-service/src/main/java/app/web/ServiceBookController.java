package app.web;

import app.appointment.model.Appointment;
import app.appointment.service.AppointmentService;
import app.car.model.Car;
import app.car.service.CarService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/serviceBook")
public class ServiceBookController {
    private final UserService userService;
    private final CarService carService;
    private  final AppointmentService appointmentService;

    @Autowired
    public ServiceBookController(UserService userService, CarService carService, AppointmentService appointmentService) {
        this.userService = userService;
        this.carService = carService;
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public ModelAndView getServiceBookPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());
        List<Car> userCars = carService.getCarsByOwnerId(user.getId());


        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("serviceBook");
        modelAndView.addObject("user", user);
        modelAndView.addObject("userCars", userCars);



        return modelAndView;
    }


}
