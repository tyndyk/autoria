package com.example.auto_ria.models.responses.car;

import com.example.auto_ria.enums.EBrand;
import com.example.auto_ria.enums.EModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarQuery {

    private EBrand brand;

    private EModel model;

    private Integer powerH;

    @Builder.Default
    private int page = 1;

    @Builder.Default
    private boolean isActivated = true;
}
