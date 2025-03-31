package app.web.dto;

import java.util.UUID;

public class FeedbackRequest {
    private UUID appointmentId;
    private String comment;
    private int rating;

    // Гетъри и сетъри
    public UUID getAppointmentId() { return appointmentId; }
    public void setAppointmentId(UUID appointmentId) { this.appointmentId = appointmentId; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
}