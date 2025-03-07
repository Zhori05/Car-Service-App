package app.web.dto;

import app.car.model.Brand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Year;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddCarRequest {
@NotNull
private Brand brand;
@Size(min = 2, message = "Моделът трябва да е минимум 2 символа")
private String model;

private int year;

private String color;

private String licensePlate;

private String mileage;

private String picture;

}
