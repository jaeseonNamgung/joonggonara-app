package com.hit.joonggonara.dto.response;

import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.stream.Collectors;

public record FieldError(
        String field,
        String message
) {

    public static FieldError of(
            String field,
            String message
    ){
        return new FieldError(field, message);
    }

    public static List<FieldError> create(BindingResult bindingResult){

        List<org.springframework.validation.FieldError> fieldErrors = bindingResult.getFieldErrors();

        return fieldErrors.stream()
                .map(fieldError ->
                        FieldError.of(
                                fieldError.getField(),
                                fieldError.getDefaultMessage())
                        ).collect(Collectors.toList());
    }

}
