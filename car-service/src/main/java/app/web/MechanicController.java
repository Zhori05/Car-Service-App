package app.web;

import app.appointment.model.Appointment;
import app.appointment.service.AppointmentService;
import app.config.BeanConfiguration;

import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class MechanicController {
    private final UserService userService;
    private final AppointmentService  appointmentService;
    private final RestTemplate restTemplate;



    @Autowired
    public MechanicController(UserService userService, AppointmentService appointmentService, RestTemplate restTemplate) {
        this.userService = userService;

        this.appointmentService = appointmentService;


        this.restTemplate = restTemplate;
    }

    @GetMapping("/addMechanic")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAllUsers(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getById(authenticationMetadata.getUserId());
        List<User> addMechanic = userService.getAllBasicUsers();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("addMechanic");
        modelAndView.addObject("addMechanic", addMechanic);
        modelAndView.addObject("user",user);

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
        User user = userService.getById(authenticationMetadata.getUserId());
        List<User> allMechanics = userService.getAllMechanics();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("allMechanics");
        modelAndView.addObject("allMechanics", allMechanics);
        modelAndView.addObject("user",user);

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

        List<AppointmentWithFeedbackRequest> finishedAppointments = finishedWorkMechanic.stream().map(appointment -> {
            AppointmentWithFeedbackRequest dto = new AppointmentWithFeedbackRequest();
            dto.setAppointmentId(appointment.getId());
            dto.setOwnerId(appointment.getOwner().getId());
            dto.setOwnerName(appointment.getOwner().getUsername());
            dto.setCarId(appointment.getCar().getId());
            dto.setCarModel(appointment.getCar().getModel());
            dto.setServiceForCarId(appointment.getServiceForCar().getId());
            dto.setServiceName(appointment.getServiceForCar().getName());
            dto.setMechanicId(appointment.getMechanic().getId());
            dto.setMechanicName(appointment.getMechanic().getUsername());
            dto.setStart(appointment.getStart());
            dto.setFinish(appointment.getFinish());
            dto.setMoreInfo(appointment.getMoreInfo());
            dto.setFinished(appointment.isFinished());

            // Извличане на Feedback от другия микросървис
            try {
                String feedbackUrl = "http://localhost:8081/feedback/appointment/" + appointment.getId().toString();
                ResponseEntity<List<FeedbackResponse>> response = restTemplate.exchange(
                        feedbackUrl,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<FeedbackResponse>>() {}
                );

                List<FeedbackResponse> feedbackList = response.getBody();
                FeedbackResponse feedback = (feedbackList != null && !feedbackList.isEmpty()) ? feedbackList.get(0) : null;


                if (feedback != null) {
                    dto.setRating(feedback.getRating());
                    dto.setComment(feedback.getComment());
                    System.out.println("Feedback received: " + feedback.getRating() + ", " + feedback.getComment());
                } else {
                    System.out.println("No feedback found for appointment: " + appointment.getId());
                }
            } catch (Exception e) {
                System.err.println("Error fetching feedback: " + e.getMessage());
                dto.setRating(null);
                dto.setComment(null);
            }

            return dto;
        }).collect(Collectors.toList());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("finishedWorkMechanic");
        modelAndView.addObject("finishedWorkMechanic", finishedAppointments);
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @GetMapping("/bookedAppointments")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getBookedAppointments(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getById(authenticationMetadata.getUserId());
        List<Appointment> appointments = appointmentService.getAppointments();

        // Добавяме feedback към записите
        List<AppointmentWithFeedbackRequest> bookedAppointments = appointments.stream().map(appointment -> {
            AppointmentWithFeedbackRequest dto = new AppointmentWithFeedbackRequest();
            dto.setAppointmentId(appointment.getId());
            dto.setOwnerId(appointment.getOwner().getId());
            dto.setCarId(appointment.getCar().getId());
            dto.setServiceForCarId(appointment.getServiceForCar().getId());
            dto.setMechanicId(appointment.getMechanic().getId());
            dto.setMechanicName(appointment.getMechanic().getUsername());
            dto.setStart(appointment.getStart());
            dto.setFinish(appointment.getFinish());
            dto.setMoreInfo(appointment.getMoreInfo());
            dto.setFinished(appointment.isFinished());

            // Извличане на Feedback от другия микросървис
            try {
                String feedbackUrl = "http://localhost:8081/feedback/appointment/" + appointment.getId().toString();
                ResponseEntity<List<FeedbackResponse>> response = restTemplate.exchange(
                        feedbackUrl,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<FeedbackResponse>>() {}
                );

                List<FeedbackResponse> feedbackList = response.getBody();
                FeedbackResponse feedback = (feedbackList != null && !feedbackList.isEmpty()) ? feedbackList.get(0) : null;


                if (feedback != null) {
                    dto.setRating(feedback.getRating());
                    dto.setComment(feedback.getComment());
                    System.out.println("Feedback received: " + feedback.getRating() + ", " + feedback.getComment());
                } else {
                    System.out.println("No feedback found for appointment: " + appointment.getId());
                }
            } catch (Exception e) {
                System.err.println("Error fetching feedback: " + e.getMessage());
                dto.setRating(null);
                dto.setComment(null);
            }

            return dto;
        }).collect(Collectors.toList());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("bookedAppointments");
        modelAndView.addObject("bookedAppointments", bookedAppointments);
        modelAndView.addObject("user", user);
        return modelAndView;
    }



}