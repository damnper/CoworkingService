package ru.y_lab.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.y_lab.enums.ResourceType;

import java.util.Arrays;

public class ResourceTypeValidator implements ConstraintValidator<ValidResourceType, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return Arrays.stream(ResourceType.values())
                .anyMatch(resourceType -> resourceType.name().equals(value));
    }
}
