package app.web;

import app.mechanic.model.Mechanic;
import app.mechanic.service.MechanicService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.AddMechanicRequest;
import app.web.dto.AddServiceRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class MechanicController {
    private final UserService userService;
    private final MechanicService mechanicService;

    @Autowired
    public MechanicController(UserService userService, MechanicService mechanicService) {
        this.userService = userService;
        this.mechanicService = mechanicService;
    }
    @GetMapping("/addMechanic")
    public ModelAndView getAddMechanicPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getById(authenticationMetadata.getUserId());
        ModelAndView modelAndView = new ModelAndView("addMechanic");
        modelAndView.addObject("user", user);
        modelAndView.addObject("addMechanicRequest", new AddMechanicRequest()); // <-- Добавяме празен обект

        return modelAndView;
    }
    @PostMapping("/addMechanic")
    public ModelAndView addMechanic(@ModelAttribute("addMechanicRequest") @Valid AddMechanicRequest addMechanicRequest,
                                         BindingResult bindingResult, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        if (bindingResult.hasErrors()) {
            return new ModelAndView("addMechanic");
        }
        User user = userService.getById(authenticationMetadata.getUserId());
        mechanicService.addMechanic(addMechanicRequest);

        return new ModelAndView("redirect:/bookAService");

    }
    @GetMapping("/allMechanics")
    public ModelAndView getAllMechanicsPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        User user = userService.getById(authenticationMetadata.getUserId());

        List<Mechanic> mechanics = mechanicService.getAllMechanics();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("allMechanics");
        modelAndView.addObject("allMechanics", mechanics);
        modelAndView.addObject("user", user);
        return modelAndView;
    }


}
