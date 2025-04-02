package com.example.auto_ria.models.responses.car;

import com.example.auto_ria.enums.ECurrency;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MiddlePriceResponse {

    private ECurrency currency;
    private int middlePrice;
}
