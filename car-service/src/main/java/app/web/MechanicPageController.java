package app.web;

import app.car.model.Car;
import app.car.service.CarService;
import app.mechanic.model.Mechanic;
import app.mechanic.service.MechanicService;
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
@RequestMapping("/mechanicPage")
public class MechanicPageController {
    private final UserService userService;
    private final MechanicService mechanicService;
    private final CarService carService;

    @Autowired
    public MechanicPageController(UserService userService, MechanicService mechanicService, CarService carService) {
        this.userService = userService;
        this.mechanicService = mechanicService;
        this.carService = carService;
    }

    @GetMapping
    public ModelAndView getMechanicPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        Mechanic mechanic = mechanicService.getById(authenticationMetadata.getUserId());


        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("mechanicPage");
        modelAndView.addObject("mechanic", mechanic);




        return modelAndView;
    }



}
