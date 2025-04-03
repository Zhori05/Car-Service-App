package app.web.dto;


import app.serviceForCars.model.Special;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddServiceRequest {

    @Size(min = 5, message = "Името на услугата трябва да бъде по дулго от 5 символа")
    private String name;
    @NotNull
    private int minutesToFinish;
    @NotNull
    private double price;
    @NotNull
    private Special special;
}
