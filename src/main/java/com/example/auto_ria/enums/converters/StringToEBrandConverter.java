package com.example.auto_ria.enums.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.example.auto_ria.enums.EBrand;
import com.example.auto_ria.exceptions.general.InternalServerException;

@Component
public class StringToEBrandConverter implements Converter<String, EBrand> {
    @Override
    public EBrand convert(String source) {
        try {
            return EBrand.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InternalServerException("Invalid brand: " + source);
        }
    }
}
