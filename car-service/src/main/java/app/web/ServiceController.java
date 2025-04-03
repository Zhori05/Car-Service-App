package app.web;

import app.security.AuthenticationMetadata;
import app.serviceForCars.model.ServiceForCar;
import app.serviceForCars.service.ServiceForCarService;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.AddCarRequest;
import app.web.dto.AddServiceRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

public class ServiceController {
    private final UserService userService;
    private final ServiceForCarService serviceForCarService;

    @Autowired
    public ServiceController(UserService userService, ServiceForCarService serviceForCarService) {
        this.userService = userService;
        this.serviceForCarService = serviceForCarService;
    }

    @GetMapping("/addService")
    public ModelAndView getAddServicePage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getById(authenticationMetadata.getUserId());
        ModelAndView modelAndView = new ModelAndView("addService");
        modelAndView.addObject("addServiceRequest", new AddServiceRequest()); // <-- Добавяме празен обект
        modelAndView.addObject("user", user);
        return modelAndView;
    }
    @PostMapping("/addService")
    public ModelAndView addServiceForCar(@ModelAttribute("addServiceRequest") @Valid AddServiceRequest addServiceRequest,
    BindingResult bindingResult, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        if (bindingResult.hasErrors()) {
            return new ModelAndView("addService");
        }
        User user = userService.getById(authenticationMetadata.getUserId());
        serviceForCarService.addService(addServiceRequest);

        return new ModelAndView("redirect:/bookAService");

    }

    @GetMapping("/allServices")
    public ModelAndView getAllServicesPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        User user = userService.getById(authenticationMetadata.getUserId());

        List<ServiceForCar> serviceForCars = serviceForCarService.getAllServices();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("allServices");
        modelAndView.addObject("allServices", serviceForCars);
        modelAndView.addObject("user", user);
        return modelAndView;
    }

}
