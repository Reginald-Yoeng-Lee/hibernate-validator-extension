package io.github.reginald.hv.extension.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Indicates that the validating value should be {@code String} and equal to one of the provided constants.
 */
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.ANNOTATION_TYPE, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StringEnumValueValidator.class)
@Documented
public @interface StringEnumValue {

    String message() default "Value ${validatedValue} not valid. Should be one of {value}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Defines the valid candidates which the validating value could be.
     *
     * @return The valid constants.
     */
    String[] value();

    boolean allowEmpty() default false;
}
