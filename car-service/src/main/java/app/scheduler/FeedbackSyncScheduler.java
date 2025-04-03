package app.scheduler;

import app.appointment.model.Appointment;
import app.appointment.service.AppointmentService;
import app.web.dto.FeedbackResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.UUID;
import java.util.Arrays;

@Component
public class FeedbackSyncScheduler {

    private final AppointmentService appointmentService;
    private final RestTemplate restTemplate;

    public FeedbackSyncScheduler(AppointmentService appointmentService, RestTemplate restTemplate) {
        this.appointmentService = appointmentService;
        this.restTemplate = restTemplate;
    }

    @Scheduled(cron = "0 */2 * * * ?") // Изпълнява се всеки час
    public void syncFeedbackForCompletedAppointments() {
        List<Appointment> completedAppointments = appointmentService.findByIsFinishedTrue();

        for (Appointment appointment : completedAppointments) {
            try {
                String feedbackUrl = "http://localhost:8081/feedback/appointment/" + appointment.getId();
                FeedbackResponse[] feedbacks = restTemplate.getForObject(feedbackUrl, FeedbackResponse[].class);

                if (feedbacks != null && feedbacks.length > 0) {
                    Arrays.stream(feedbacks).forEach(feedback ->
                            System.out.println("Feedback за резервация " + appointment.getId() +
                                    " -> Рейтинг: " + feedback.getRating() +
                                    ", Коментар: " + feedback.getComment()));
                } else {
                    System.out.println("Няма feedback за резервация: " + appointment.getId());
                }
            } catch (Exception e) {
                System.err.println("Грешка при извличане на feedback за резервация " + appointment.getId() + ": " + e.getMessage());
            }
        }
    }
}
