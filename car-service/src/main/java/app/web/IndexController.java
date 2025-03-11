package app.web;

import app.car.service.CarService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.AddCarRequest;
import app.web.dto.LoginRequest;
import app.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController {
    private final UserService userService;
    private final CarService carService;

    @Autowired
    public IndexController(UserService userService, CarService carService) {
        this.userService = userService;
        this.carService = carService;
    }

    @GetMapping("/")
    public String getIndexPage() {

        return "index";
    }

    @GetMapping("/login")
    public ModelAndView getLoginPage(@RequestParam(value = "error", required = false) String errorParam) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        modelAndView.addObject("loginRequest", new LoginRequest());

        if (errorParam != null) {
            modelAndView.addObject("errorMessage", "Грешно име и парола!");
        }

        return modelAndView;
    }

    @GetMapping("/register")
    public ModelAndView getRegisterPage() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register");
        modelAndView.addObject("registerRequest", new RegisterRequest());

        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView registerNewUser(@Valid RegisterRequest registerRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("register");
        }

        userService.register(registerRequest);

        return new ModelAndView("redirect:/login");
    }



    @GetMapping("/addCar")
    public ModelAndView getAddCarPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getById(authenticationMetadata.getUserId());

        ModelAndView modelAndView = new ModelAndView("addCar");
        modelAndView.addObject("user", user);
        modelAndView.addObject("addCarRequest", new AddCarRequest()); // <-- Добавяме празен обект

        return modelAndView;
    }

    @PostMapping("/addCar")
    public ModelAndView addNewCar(@ModelAttribute("addCarRequest") @Valid AddCarRequest addCarRequest,
                                  BindingResult bindingResult,
                                  @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("addCar");
        }

        User user = userService.getById(authenticationMetadata.getUserId());
        carService.addCar(addCarRequest, user);

        return new ModelAndView("redirect:/bookAService");
    }

}
