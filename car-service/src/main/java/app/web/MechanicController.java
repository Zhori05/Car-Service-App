package app.web;

import app.mechanic.model.Mechanic;
import app.mechanic.service.MechanicService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.AddMechanicRequest;
import app.web.dto.AddServiceRequest;
import app.web.dto.LoginMechanicRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
public class MechanicController {
    private final UserService userService;



    @Autowired
    public MechanicController(UserService userService) {
        this.userService = userService;

    }

    @GetMapping("/addMechanic")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAllUsers(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        List<User> addMechanic = userService.getAllBasicUsers();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("addMechanic");
        modelAndView.addObject("addMechanic", addMechanic);

        return modelAndView;
    }




    @PutMapping("/addMechanic/{id}/role") // PUT /addMechanic/{id}/role
    public String switchMechanicRole(@PathVariable UUID id) {

        userService.switchMechanic(id);

        return "redirect:/addMechanic";
    }




    @GetMapping("/allMechanics")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAllMechanics(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        List<User> allMechanics = userService.getAllMechanics();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("allMechanics");
        modelAndView.addObject("allMechanics", allMechanics);

        return modelAndView;
    }
    @PutMapping("/allMechanics/{id}/role") // PUT /allMechanic/{id}/role
    public String switchMechanicRoleToUser(@PathVariable UUID id) {

        userService.switchMechanic(id);

        return "redirect:/allMechanics";
    }



}