package io.github.reginald.hv.extension;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Indicates that at least one of the specific fields of the annotated object contains a valid value.
 */
@Target({ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OrValidator.class)
@Documented
@Repeatable(Or.List.class)
public @interface Or {

    String message() default "At least one of the fields {fields} required.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * The fields of the annotated object should be checked.<br>
     *
     * @return The fields' names.
     */
    String[] fields();

    Class<? extends FieldAccessor> accessor() default PojoFieldAccessor.class;

    Class<? extends FieldVerifier> fieldVerifier() default NonEmptyStringFieldVerifier.class;

    /**
     * Defines multiple constraints for the same element.
     */
    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        Or[] value();
    }
}
