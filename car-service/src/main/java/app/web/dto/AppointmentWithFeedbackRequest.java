package app.web.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AppointmentWithFeedbackRequest {
    private String appointmentId;
    private String ownerId;
    private String carId;
    private String serviceForCarId;
    private String mechanicId;
    private String ownerName;
    private String carModel;
    private String serviceName;
    private String mechanicName;
    private LocalDateTime start;
    private LocalDateTime finish;
    private String moreInfo;
    private boolean isFinished;
    private Integer rating;  // Може да бъде null, ако няма отзив
    private String comment;  // Може да бъде null, ако няма отзив

    public void setAppointmentId(UUID appointmentId) { this.appointmentId = appointmentId.toString(); }
    public void setOwnerId(UUID ownerId) { this.ownerId = ownerId.toString(); }
    public void setCarId(UUID carId) { this.carId = carId.toString(); }
    public void setServiceForCarId(UUID serviceForCarId) { this.serviceForCarId = serviceForCarId.toString(); }
    public void setMechanicId(UUID mechanicId) { this.mechanicId = mechanicId.toString(); }
}
