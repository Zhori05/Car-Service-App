package app.web.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginMechanicRequest {

    @Size(min = 2, message = "Името ти трябва да бъде минимум 2 символа")
    private String email;
    @Size(min = 2)
    private String logInCode;
}
