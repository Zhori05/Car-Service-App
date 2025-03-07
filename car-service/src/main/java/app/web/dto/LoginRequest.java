package app.web.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    @Size(min = 6, message = "Името трябва да бъде минимум 6 символа")
    private String username;

    @Size(min = 6, message = "Паролата трябва да е минимум 6 символа")
    private String password;
}
