package app.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RegisterRequest {

    @Size(min = 6, message = "Името трябва да бъде минимум 6 символа")
    private String username;

    @Email
    private String email;

    @Size(min = 6, message = "Паролата трябва да е минимум 6 символа")
    private String password;

    @Size(min=9, max = 10, message = "Моля въведете валиден телефонен номер")
    private String phoneNumber;



}

