package app.web;

import app.appointment.model.Appointment;
import app.appointment.service.AppointmentService;
import app.car.model.Car;
import app.car.service.CarService;
import app.mechanic.model.Mechanic;
import app.mechanic.service.MechanicService;
import app.security.AuthenticationMetadata;
import app.serviceForCars.model.ServiceForCar;
import app.serviceForCars.service.ServiceForCarService;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.AppointmentRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/bookAService")
public class BookingController {
    private final UserService userService;
    private final CarService carService;
    private final ServiceForCarService serviceForCarService;
    private final AppointmentService appointmentService;

    @Autowired
    public BookingController(UserService userService, CarService carService, ServiceForCarService serviceForCarService, AppointmentService appointmentService) {
        this.userService = userService;
        this.carService = carService;
        this.serviceForCarService = serviceForCarService;
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public ModelAndView getBookAServicePage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());
        List<Car> userCars = carService.getCarsByOwnerId(user.getId());
        List<ServiceForCar> serviceForCars= serviceForCarService.getAllServices();
        List<User> mechanics= userService.getAllMechanics();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("bookAService");
        modelAndView.addObject("user", user);
        modelAndView.addObject("userCars", userCars);
        modelAndView.addObject("serviceForCars", serviceForCars);
        modelAndView.addObject("mechanics", mechanics);
        modelAndView.addObject("appointmentRequest", new AppointmentRequest());



        return modelAndView;
    }
    @PostMapping
    public ModelAndView addAppointment(@ModelAttribute("appointmentRequest") @Valid AppointmentRequest appointmentRequest, BindingResult bindingResult, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        if (bindingResult.hasErrors()) {
            return new ModelAndView("bookAService");
        }

        User user = userService.getById(authenticationMetadata.getUserId());
        appointmentService.addAppointment(appointmentRequest,user);

        return new ModelAndView("redirect:/bookAService");
    }






}
