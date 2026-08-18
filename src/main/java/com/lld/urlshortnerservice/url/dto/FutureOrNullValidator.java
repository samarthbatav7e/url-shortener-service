package com.lld.urlshortnerservice.url.dto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class FutureOrNullValidator implements ConstraintValidator<FutureOrNull, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context)
    {
        if(value==null)
        {
            return true;
        }
        return value.isAfter(LocalDateTime.now());
    }

}
