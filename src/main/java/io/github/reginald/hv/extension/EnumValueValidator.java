package io.github.reginald.hv.extension;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.annotation.Annotation;
import java.util.Arrays;

public abstract class EnumValueValidator<A extends Annotation, T> implements ConstraintValidator<A, T> {

    @Override
    public boolean isValid(T value, ConstraintValidatorContext context) {
        if (value == null) {
            return allowEmpty();
        }
        return Arrays.asList(range()).contains(value);
    }

    protected abstract boolean allowEmpty();

    protected abstract T[] range();
}
