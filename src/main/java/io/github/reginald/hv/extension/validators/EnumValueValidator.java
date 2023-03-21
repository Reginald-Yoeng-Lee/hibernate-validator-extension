package io.github.reginald.hv.extension.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.annotation.Annotation;
import java.util.Arrays;

/**
 * Base validator for validating the <i>Enum-Like</i> value, i.e. value which only be valid within the group of several
 * constants.
 *
 * @param <A> The validation annotation type.
 * @param <T> The validating type.
 */
public abstract class EnumValueValidator<A extends Annotation, T> implements ConstraintValidator<A, T> {

    @Override
    public boolean isValid(T value, ConstraintValidatorContext context) {
        if (value == null) {
            return allowEmpty();
        }
        return Arrays.asList(range()).contains(value);
    }

    /**
     * Defines whether the value is allowed to be {@code null}.
     *
     * @return {@code true} for allowing the value being null.
     */
    protected abstract boolean allowEmpty();

    /**
     * Defines the group of valid constants.
     *
     * @return The valid constants.
     */
    protected abstract T[] range();
}
