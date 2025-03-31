package app.web.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class ReviewRequest {
    private UUID appointmentId;
    private UUID userId;
    private UUID carId;
    private UUID mechanicId;
    private UUID serviceId;
    private Integer rating;
    private String comment;
}