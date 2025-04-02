package com.example.auto_ria.services.validation.car;

import java.util.Arrays;

import org.springframework.stereotype.Service;

import com.example.auto_ria.dto.CarDTO;
import com.example.auto_ria.enums.EBrand;
import com.example.auto_ria.enums.ECurrency;
import com.example.auto_ria.enums.EModel;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CarValidationService {

    public EBrand convertStringToEBrand(String brand) {
        try {
            return EBrand.valueOf(brand.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid brand value: " + brand);
        }
    }

    public EModel convertStringToEModel(String brand) {
        try {
            return EModel.valueOf(brand.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid model value: " + brand);
        }
    }

    public void isValidCarName(String brand, String model) {
        if (!isValidCarBrand(brand)) {
            throw new IllegalArgumentException("Brand not valid: " + brand);
        }

        if (!isValidCarModel(brand, model)) {
            throw new IllegalArgumentException("Model not valid for brand " + brand + ": " + model);
        }
    }

    public void validateCarEnums(CarDTO car) {
        isValidCarName(car.getBrand(), car.getModel());
        isValidCurrency(car.getCurrency());
    }

    public void isValidCurrency(String currency) {
        if (!Arrays.stream(ECurrency.values())
                .anyMatch(eCurrency -> eCurrency.name().equalsIgnoreCase(currency))) {
            throw new IllegalArgumentException("Invalid currency: " + currency);
        }
    }

    private boolean isValidCarBrand(String brand) {
        return Arrays.stream(EBrand.values())
                .anyMatch(eBrand -> eBrand.name().equalsIgnoreCase(brand));
    }

    private boolean isValidCarModel(String brand, String model) {
        return Arrays.stream(EModel.values())
                .filter(carModel -> carModel.getBrand().name().equalsIgnoreCase(brand))
                .anyMatch(carModel -> carModel.name().equalsIgnoreCase(model));
    }
}
