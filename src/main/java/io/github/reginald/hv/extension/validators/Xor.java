package io.github.reginald.hv.extension.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Indicates that exactly one of the specific fields of the annotated object contains a valid value.
 */
@Target({ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = XorValidator.class)
@Documented
@Repeatable(Xor.List.class)
public @interface Xor {

    String message() default "One and only one of the fields {fields} required.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * The fields of the annotated object should be checked.<br>
     *
     * @return The fields' names.
     */
    String[] fields();

    /**
     * Defines the field accessor implementation.
     *
     * @return The implementation class of the field accessor gonna be used.
     * @see FieldAccessor
     * @see PojoFieldAccessor#access(Object, String)
     */
    Class<? extends FieldAccessor> accessor() default PojoFieldAccessor.class;

    /**
     * Defines the field verifier implementation.
     *
     * @return The implementation class of the field verifier gonna be used.
     * @see FieldVerifier
     * @see NonEmptyStringFieldVerifier#verify(Object, String, Object)
     */
    Class<? extends FieldVerifier> fieldVerifier() default NonEmptyStringFieldVerifier.class;

    /**
     * Defines multiple constraints for the same element.
     */
    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        Xor[] value();
    }
}
