package app.web;

import app.car.model.Car;
import app.car.service.CarService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/bookAService")
public class BookingController {
    private final UserService userService;
    private final CarService carService;

    @Autowired
    public BookingController(UserService userService, CarService carService) {
        this.userService = userService;
        this.carService = carService;
    }

    @GetMapping
    public ModelAndView getBookAServicePage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());
        List<Car> userCars = carService.getCarsByOwnerId(user.getId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("bookAService");
        modelAndView.addObject("user", user);
        modelAndView.addObject("userCars", userCars);



        return modelAndView;
    }



}
