package com.example.auto_ria.models.responses.car;

import java.time.LocalDateTime;
import java.util.List;

import com.example.auto_ria.enums.EBrand;
import com.example.auto_ria.enums.ECurrency;
import com.example.auto_ria.enums.EModel;
import com.example.auto_ria.models.responses.user.UserCarResponse;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Data;

@Data
public class CarResponse {

    private int id;
    private EBrand brand;
    private EModel model;
    private int powerH;
    private String city;
    private String country;
    private Double price;
    private ECurrency currency;
    private List<String> photo;
    private String description;
    private Boolean isActivated;
    private UserCarResponse user;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Builder
    public CarResponse(int id, EBrand brand, EModel model, int powerH, String city,
            String country, Double price, ECurrency currency,
            List<String> photo, String description, UserCarResponse user, Boolean isActivated,
            LocalDateTime createdAt) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.powerH = powerH;
        this.city = city;
        this.country = country;
        this.price = price;
        this.currency = currency;
        this.photo = photo;
        this.description = description;
        this.isActivated = isActivated;
        this.user = user;
        this.createdAt = createdAt;
    }
}
