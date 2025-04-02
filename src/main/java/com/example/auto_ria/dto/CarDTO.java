package com.example.auto_ria.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CarDTO {

    @NotNull(message = "Brand cannot be null")
    private String brand;

    @NotNull(message = "Model cannot be null")
    private String model;

    @Min(value = 200, message = "Power must be at least 200")
    @Max(value = 3000, message = "Power must be less than 3000")
    private int powerH;

    @NotEmpty(message = "City cannot be empty")
    @Size(min = 2, max = 20, message = "City length must be between 2 and 20")
    private String city;

    @NotEmpty(message = "Region cannot be empty")
    private String country;

    @NotNull(message = "Price cannot be empty")
    private int price;

    @NotNull(message = "Currency cannot be null")
    private String currency;

    private MultipartFile[] photos;

    @NotEmpty(message = "Description cannot be empty")
    private String description;

    private boolean isActivated;

    @Builder
    public CarDTO(String brand, String model, int powerH, String city, String country, int price, String currency,
            MultipartFile[] photos, String description, boolean isActivated) {
        this.brand = brand;
        this.model = model;
        this.powerH = powerH;
        this.city = city;

        this.country = country;
        this.price = price;
        this.currency = currency;
        this.photos = photos;
        this.description = description;
        this.isActivated = isActivated;
    }
}
