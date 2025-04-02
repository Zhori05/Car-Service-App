package app.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FeedbackResponse {
    private UUID id;
    private UUID appointmentId;
    private Integer rating;
    private String comment;
}