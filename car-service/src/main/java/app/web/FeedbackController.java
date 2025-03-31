package app.web;

import app.web.dto.FeedbackRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class FeedbackController {
    private static final Logger logger = LoggerFactory.getLogger(FeedbackController.class);

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/submit-feedback")
    public String submitFeedback(@ModelAttribute FeedbackRequest feedbackRequest, Model model) {
        logger.info("Получена заявка за отзив: AppointmentId={}, Comment={}, Rating={}",
                feedbackRequest.getAppointmentId(), feedbackRequest.getComment(), feedbackRequest.getRating());

        String feedbackServiceUrl = "http://localhost:8081/feedback";

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(feedbackServiceUrl, feedbackRequest, String.class);
            logger.info("Успешно изпратен отзив към feedback-service. Отговор: {}", response.getBody());
            model.addAttribute("successMessage", "Отзивът е изпратен успешно!");
        } catch (Exception e) {
            logger.error("Грешка при изпращане на отзив: {}", e.getMessage());
            model.addAttribute("errorMessage", "Грешка при изпращане на отзива.");
        }

        return "redirect:/serviceBook";
    }
}
