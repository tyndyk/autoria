package com.example.auto_ria.models.car;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.auto_ria.enums.EBrand;
import com.example.auto_ria.enums.ECurrency;
import com.example.auto_ria.enums.EModel;
import com.example.auto_ria.models.user.UserSQL;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = { "user" })
public class CarSQL {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    private EBrand brand;

    private int powerH;

    private String city;

    private String country;

    private EModel model;

    @ElementCollection
    private List<String> photo = new ArrayList<>();

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "seller_cars", joinColumns = @JoinColumn(name = "car_id", nullable = false), inverseJoinColumns = @JoinColumn(name = "seller_id"))
    private UserSQL user;

    private double price;
    private double priceBase;

    private ECurrency currency;

    private String description;

    private boolean isActivated;

    @Column(updatable = false)
    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDateTime createdAt;

    @UpdateTimestamp()
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "\"dd/MM/yyyy\"", timezone = "GMT")
    private LocalDateTime updatedAt;

    @Builder
    public CarSQL(EBrand brand, Integer powerH, String city, String country,
            EModel model, List<String> photo, UserSQL user, int price,
            ECurrency currency, String description, boolean isActivated, int priceBase) {
        this.brand = brand;
        this.model = model;
        this.powerH = powerH;
        this.city = city;
        this.country = country;
        this.photo = photo;
        this.user = user;
        this.price = price;
        this.currency = currency;
        this.description = description;
        this.isActivated = isActivated;
        this.priceBase = priceBase;
    }

}