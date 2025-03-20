package app.web;

import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/notActiveUsers")
public class NotActiveUsersController {
    private final UserService userService;

    @Autowired
    public NotActiveUsersController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getNotActiveUsers(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        List<User> notActiveUsers = userService.getNotActiveUsers();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("notActiveUsers");
        modelAndView.addObject("notActiveUsers", notActiveUsers);

        return modelAndView;
    }
    @PutMapping("/{id}/status") // PUT /users/{id}/status
    public String switchUserStatus(@PathVariable UUID id) {

        userService.switchStatus(id);

        return "redirect:/notActiveUsers";
    }

    @PutMapping("/{id}/role") // PUT /users/{id}/role
    public String switchUserRole(@PathVariable UUID id) {

        userService.switchRole(id);

        return "redirect:/notActiveUsers";
    }

}
