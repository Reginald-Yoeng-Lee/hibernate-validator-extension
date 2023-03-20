package io.github.reginald.hv.extension;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LongEnumValueValidator.class)
@Documented
public @interface LongEnumValue {

    String message() default "Value ${validatedValue} not valid. Should be one of {values}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    long[] value();

    boolean allowEmpty() default false;
}
