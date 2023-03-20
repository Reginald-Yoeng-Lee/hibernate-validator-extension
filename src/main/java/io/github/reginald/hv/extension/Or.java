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
     * Can include fields of the nested field, which field names separated by dot (.)<br>
     * e.g. Suppose we have a record like
     * <pre>
     *     record Pojo(String a, NestedPojo inner) {
     *         record NestedPojo(String b) {}
     *     }
     * </pre>
     * The field {@code b} of the inner record {@code NestedPojo} could be referenced as {@code inner.b}. That is, if we
     * want to indicate that at least one of the string {@code a} or the string {@code b} contains a valid value, we could
     * use this annotation like
     * <pre>
     *     &#064;Or(fields = {"a", "inner.b"})
     *     record Pojo (String a, NestedPojo inner) {...}
     * </pre>
     * This works for both nested POJO and Map.
     *
     * @return The fields' names.
     */
    String[] fields();

    boolean allowEmptyString() default false;

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
