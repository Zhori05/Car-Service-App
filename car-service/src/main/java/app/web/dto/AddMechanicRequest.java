package app.web.dto;

import app.mechanic.model.Specials;
import app.serviceForCars.model.Special;
import app.user.model.UserRole;
import jakarta.validation.constraints.Email;
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
public class AddMechanicRequest {

    @Size(min = 2, message = "Името на услугата трябва да бъде по дулго от 2 символа")
    private String name;
    @Email
    private String email;
    @NotNull
    private String logInCode;
    @NotNull
    private Specials specialisedIn;

    private UserRole role;


}
