package app.web;

import app.appointment.model.Appointment;
import app.appointment.service.AppointmentService;
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
    private final AppointmentService  appointmentService;



    @Autowired
    public MechanicController(UserService userService, AppointmentService appointmentService) {
        this.userService = userService;

        this.appointmentService = appointmentService;
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

    @GetMapping("/todayMechanicWork")
    public ModelAndView getWorkForToday(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        User user = userService.getById(authenticationMetadata.getUserId());
        List<Appointment> todayMechanicWork = appointmentService.getTodayAppointmentsForMechanic(user.getId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("todayMechanicWork");
        modelAndView.addObject("todayMechanicWork", todayMechanicWork);
        modelAndView.addObject("user", user);
        return modelAndView;
    }
    @GetMapping("/weekMechanicWork")
    public ModelAndView getWorkForThisWeek(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        User user = userService.getById(authenticationMetadata.getUserId());
        List<Appointment> weekMechanicWork = appointmentService.getAppointmentsForNextWeekByMechanic(user.getId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("weekMechanicWork");
        modelAndView.addObject("weekMechanicWork", weekMechanicWork);
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @PutMapping("todayMechanicWork/{id}/status")
    public String switchTodayAppointmentStatus(@PathVariable UUID id) {

        appointmentService.switchStatus(id);

        return "redirect:/todayMechanicWork";
    }

    @PutMapping("weekMechanicWork/{id}/status")
    public String switchWeekAppointmentStatus(@PathVariable UUID id) {

        appointmentService.switchStatus(id);

        return "redirect:/weekMechanicWork";
    }

    @GetMapping("/finishedWorkMechanic")
    public ModelAndView getFinishedWork(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        User user = userService.getById(authenticationMetadata.getUserId());
        List<Appointment> finishedWorkMechanic = appointmentService.findByMechanicIdAndIsFinishedTrue(user.getId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("finishedWorkMechanic");
        modelAndView.addObject("finishedWorkMechanic", finishedWorkMechanic);
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @GetMapping("/bookedAppointments")
    public ModelAndView getBookedAppointments(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        User user = userService.getById(authenticationMetadata.getUserId());
        List<Appointment> bookedAppointments = appointmentService.getAppointments();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("bookedAppointments");
        modelAndView.addObject("bookedAppointments", bookedAppointments);
        modelAndView.addObject("user", user);
        return modelAndView;
    }



}