package com.example.auto_ria.enums.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.example.auto_ria.enums.EModel;
import com.example.auto_ria.exceptions.general.InternalServerException;

@Component
public class StringToEModelConverter implements Converter<String, EModel> {
    @Override
    public EModel convert(String source) {
        try {
            return EModel.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InternalServerException("Invalid brand: " + source);
        }
    }
}
